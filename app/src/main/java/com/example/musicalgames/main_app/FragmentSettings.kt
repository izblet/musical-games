package com.example.musicalgames.main_app

import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.musicalgames.R
import com.example.musicalgames.databinding.FragmentSettingsBinding
import com.example.musicalgames.music_model.Interval
import com.example.musicalgames.settings.IntervalColorSettings
import com.example.musicalgames.settings.IntervalColorSettingsRepository
import com.example.musicalgames.settings.MicrophoneSettings
import com.example.musicalgames.settings.MicrophoneSettingsRepository
import com.example.musicalgames.utils.components.ui_components.EditableSettingsBlock
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

class FragmentSettings : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var micSettingsRepository: MicrophoneSettingsRepository
    private lateinit var intervalColorRepository: IntervalColorSettingsRepository

    private var currentlyEditingBlock: EditableSettingsBlock? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        micSettingsRepository = MicrophoneSettingsRepository(requireContext())
        intervalColorRepository = IntervalColorSettingsRepository(requireContext())

        binding.energyThresholdRow.configure(
            label = "Noise gate (dBFS, -60 to -10)",
            valueFrom = -60f, valueTo = -10f, stepSize = 1f,
            defaultValue = energyToDbfs(MicrophoneSettings.DEFAULT_ENERGY_THRESHOLD),
            format = { "${it.toInt()} dBFS" },
            onCommit = { dbfs -> updateMicSettings { it.copy(energyThreshold = dbfsToEnergy(dbfs)) } }
        )
        binding.maxUncertaintyRow.configure(
            label = "Max pitch uncertainty (0.01 - 1.0)",
            valueFrom = 0.01f, valueTo = 1f, stepSize = 0.01f,
            defaultValue = MicrophoneSettings.DEFAULT_MAX_UNCERTAINTY,
            format = { it.toString() },
            onCommit = { value -> updateMicSettings { it.copy(maxUncertainty = value) } }
        )
//        binding.entryThresholdPercentRow.configure(
//            label = "Note entry confidence % (1 - 100)",
//            valueFrom = 1f, valueTo = 100f, stepSize = 1f,
//            defaultValue = MicrophoneSettings.DEFAULT_ENTRY_THRESHOLD_PERCENT.toFloat(),
//            format = { it.toInt().toString() },
//            onCommit = { value -> updateMicSettings { it.copy(entryThresholdPercent = value.toInt()) } }
//        )
//        binding.exitThresholdPercentRow.configure(
//            label = "Note exit confidence % (1 - 100)",
//            valueFrom = 1f, valueTo = 100f, stepSize = 1f,
//            defaultValue = MicrophoneSettings.DEFAULT_EXIT_THRESHOLD_PERCENT.toFloat(),
//            format = { it.toInt().toString() },
//            onCommit = { value -> updateMicSettings { it.copy(exitThresholdPercent = value.toInt()) } }
//        )
        binding.windowMsRow.configure(
            label = "Recognition window (ms) (10 - 1000)",
            valueFrom = 10f, valueTo = 1000f, stepSize = 10f,
            defaultValue = MicrophoneSettings.DEFAULT_WINDOW_MS.toFloat(),
            format = { it.toLong().toString() },
            onCommit = { value -> updateMicSettings { it.copy(windowMs = value.toLong()) } }
        )
        binding.onsetRiseFactorRow.configure(
            label = "Onset sensitivity (rise factor) (1.2 - 5.0)",
            valueFrom = 1.2f, valueTo = 5f, stepSize = 0.1f,
            defaultValue = MicrophoneSettings.DEFAULT_ONSET_RISE_FACTOR,
            format = { "%.1fx".format(it) },
            onCommit = { value -> updateMicSettings { it.copy(onsetRiseFactor = value) } }
        )
        binding.onsetRefractoryMsRow.configure(
            label = "Onset refractory period (ms) (20 - 500)",
            valueFrom = 20f, valueTo = 500f, stepSize = 10f,
            defaultValue = MicrophoneSettings.DEFAULT_ONSET_REFRACTORY_MS.toFloat(),
            format = { it.toLong().toString() },
            onCommit = { value -> updateMicSettings { it.copy(onsetRefractoryMs = value.toLong()) } }
        )
        binding.intervalColorBlock.configure(
            title = "Intervals",
            keys = Interval.entries.map { it.name },
            onPickColor = {_, current, onPicked->{}},
            onCommit = { colorsByName -> updateIntervalColors(colorsByName) }
        )

        allEditableBlocks().forEach { setUpEditing(it) }
        binding.editBlockOverlay.setOnTouchListener { _, event -> handleOverlayTouch(event) }

        populateMicFields(micSettingsRepository.get())
        populateIntervalColors(intervalColorRepository.get())

        binding.resetAllSettingsButton.setOnClickListener { resetMicSettings() }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun allEditableBlocks(): List<EditableSettingsBlock> = listOf(
        binding.energyThresholdRow,
        binding.maxUncertaintyRow,
        //binding.entryThresholdPercentRow,
        //binding.exitThresholdPercentRow,
        binding.windowMsRow,
        binding.onsetRiseFactorRow,
        binding.onsetRefractoryMsRow,
        binding.intervalColorBlock
    )

    /** Only one block edits at a time - the overlay physically blocks reaching any other block's
     * edit button until the active one is resolved, so there's no arbitration to do here. */
    private fun setUpEditing(block: EditableSettingsBlock) {
        block.onEditRequested = {
            currentlyEditingBlock = block
            block.beginEdit()
            binding.editBlockOverlay.visibility = View.VISIBLE
        }
        block.onEditButtonReTapped = { promptSaveChanges(block) }
        block.onEditEnded = {
            binding.editBlockOverlay.visibility = View.GONE
            currentlyEditingBlock = null
        }
    }

    /** Lets touches inside the actively-edited block's bounds pass through untouched; anything
     * else is treated as "tap outside" and prompts to save/discard, same as FragmentLevelOptions. */
    private fun handleOverlayTouch(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_DOWN) return false
        val block = currentlyEditingBlock ?: return false
        val rect = Rect()
        block.getBoundsOnScreen(rect)
        if (rect.contains(event.rawX.toInt(), event.rawY.toInt())) return false
        promptSaveChanges(block)
        return true
    }

    private fun promptSaveChanges(block: EditableSettingsBlock) {
        AlertDialog.Builder(requireContext())
            .setTitle("Save changes?")
            .setPositiveButton("Save") { _, _ -> block.confirmEdit() }
            .setNegativeButton("Discard") { _, _ -> block.discardEdit() }
            .show()
    }

    private fun populateMicFields(settings: MicrophoneSettings) {
        binding.energyThresholdRow.setValue(energyToDbfs(settings.energyThreshold))
        binding.maxUncertaintyRow.setValue(settings.maxUncertainty)
        //binding.entryThresholdPercentRow.setValue(settings.entryThresholdPercent.toFloat())
        //binding.exitThresholdPercentRow.setValue(settings.exitThresholdPercent.toFloat())
        binding.windowMsRow.setValue(settings.windowMs.toFloat())
        binding.onsetRiseFactorRow.setValue(settings.onsetRiseFactor)
        binding.onsetRefractoryMsRow.setValue(settings.onsetRefractoryMs.toFloat())
    }

    private fun resetMicSettings() {
        val defaults = MicrophoneSettings()
        micSettingsRepository.save(defaults)
        populateMicFields(defaults)
    }

    private fun updateMicSettings(update: (MicrophoneSettings) -> MicrophoneSettings) {
        micSettingsRepository.save(update(micSettingsRepository.get()))
    }

    private fun populateIntervalColors(settings: IntervalColorSettings) {
        binding.intervalColorBlock.setColors(settings.colors.mapKeys { (interval, _) -> interval.name })
    }

    private fun updateIntervalColors(colorsByName: Map<String, Int>) {
        val colors = colorsByName.mapKeys { (name, _) -> Interval.valueOf(name) }
        intervalColorRepository.save(IntervalColorSettings(colors))
    }

    /**
     * The slider operates in dBFS (the standard unit for describing signal loudness) rather
     * than the raw mean-square-of-int16-samples unit [PitchRecogniser] actually compares
     * against, since dBFS is far more intuitive to tune by ear. The stored/consumed
     * [MicrophoneSettings.energyThreshold] stays in the pipeline's native raw unit - the
     * conversion happens only at this UI boundary.
     */
    private fun energyToDbfs(energy: Int): Float {
        return (10 * log10(energy.toDouble() / FULL_SCALE_ENERGY)).toFloat()
    }

    private fun dbfsToEnergy(dbfs: Float): Int {
        return (FULL_SCALE_ENERGY * 10.0.pow(dbfs / 10.0)).roundToInt()
    }

    companion object {
        // 0 dBFS reference point: the max possible mean-square energy for 16-bit PCM samples
        // (32768^2), matching the units PitchRecogniser.calculateEnergy() produces
        private const val FULL_SCALE_ENERGY = 32768.0 * 32768.0
    }
}
