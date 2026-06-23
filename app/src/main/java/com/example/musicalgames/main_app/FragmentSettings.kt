package com.example.musicalgames.main_app

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.musicalgames.databinding.FragmentSettingsBinding
import com.example.musicalgames.settings.MicrophoneSettings
import com.example.musicalgames.settings.MicrophoneSettingsRepository
import com.example.musicalgames.utils.components.ui_components.TunableSliderRow
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

class FragmentSettings : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: MicrophoneSettingsRepository

    private var currentlyEditingRow: TunableSliderRow? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        repository = MicrophoneSettingsRepository(requireContext())

        binding.energyThresholdRow.configure(
            label = "Noise gate (dBFS, -60 to -10)",
            valueFrom = -60f, valueTo = -10f, stepSize = 1f,
            defaultValue = energyToDbfs(MicrophoneSettings.DEFAULT_ENERGY_THRESHOLD),
            format = { "${it.toInt()} dBFS" },
            onCommit = { dbfs -> updateSettings { it.copy(energyThreshold = dbfsToEnergy(dbfs)) } }
        )
        binding.maxUncertaintyRow.configure(
            label = "Max pitch uncertainty (0.01 - 1.0)",
            valueFrom = 0.01f, valueTo = 1f, stepSize = 0.01f,
            defaultValue = MicrophoneSettings.DEFAULT_MAX_UNCERTAINTY,
            format = { it.toString() },
            onCommit = { value -> updateSettings { it.copy(maxUncertainty = value) } }
        )
        binding.entryThresholdPercentRow.configure(
            label = "Note entry confidence % (1 - 100)",
            valueFrom = 1f, valueTo = 100f, stepSize = 1f,
            defaultValue = MicrophoneSettings.DEFAULT_ENTRY_THRESHOLD_PERCENT.toFloat(),
            format = { it.toInt().toString() },
            onCommit = { value -> updateSettings { it.copy(entryThresholdPercent = value.toInt()) } }
        )
        binding.exitThresholdPercentRow.configure(
            label = "Note exit confidence % (1 - 100)",
            valueFrom = 1f, valueTo = 100f, stepSize = 1f,
            defaultValue = MicrophoneSettings.DEFAULT_EXIT_THRESHOLD_PERCENT.toFloat(),
            format = { it.toInt().toString() },
            onCommit = { value -> updateSettings { it.copy(exitThresholdPercent = value.toInt()) } }
        )
        binding.windowMsRow.configure(
            label = "Recognition window (ms) (10 - 1000)",
            valueFrom = 10f, valueTo = 1000f, stepSize = 10f,
            defaultValue = MicrophoneSettings.DEFAULT_WINDOW_MS.toFloat(),
            format = { it.toLong().toString() },
            onCommit = { value -> updateSettings { it.copy(windowMs = value.toLong()) } }
        )

        allRows().forEach { setUpEditing(it) }
        binding.editBlockOverlay.setOnTouchListener { _, event -> handleOverlayTouch(event) }

        populateFields(repository.get())

        binding.resetAllSettingsButton.setOnClickListener { resetAll() }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun allRows(): List<TunableSliderRow> = listOf(
        binding.energyThresholdRow,
        binding.maxUncertaintyRow,
        binding.entryThresholdPercentRow,
        binding.exitThresholdPercentRow,
        binding.windowMsRow
    )

    /** Only one row edits at a time - the overlay physically blocks reaching any other row's
     * edit button until the active one is resolved, so there's no arbitration to do here. */
    private fun setUpEditing(row: TunableSliderRow) {
        row.onEditRequested = {
            currentlyEditingRow = row
            row.beginEdit()
            binding.editBlockOverlay.visibility = View.VISIBLE
        }
        row.onEditButtonReTapped = { promptSaveChanges(row) }
        row.onEditEnded = {
            binding.editBlockOverlay.visibility = View.GONE
            currentlyEditingRow = null
        }
    }

    /** Lets touches inside the actively-edited row's bounds pass through untouched; anything
     * else is treated as "tap outside" and prompts to save/discard, same as FragmentLevelOptions. */
    private fun handleOverlayTouch(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_DOWN) return false
        val row = currentlyEditingRow ?: return false
        val rect = Rect()
        row.getGlobalVisibleRect(rect)
        if (rect.contains(event.rawX.toInt(), event.rawY.toInt())) return false
        promptSaveChanges(row)
        return true
    }

    private fun promptSaveChanges(row: TunableSliderRow) {
        AlertDialog.Builder(requireContext())
            .setTitle("Save changes?")
            .setPositiveButton("Save") { _, _ -> row.confirmEdit() }
            .setNegativeButton("Discard") { _, _ -> row.discardEdit() }
            .show()
    }

    private fun populateFields(settings: MicrophoneSettings) {
        binding.energyThresholdRow.setValue(energyToDbfs(settings.energyThreshold))
        binding.maxUncertaintyRow.setValue(settings.maxUncertainty)
        binding.entryThresholdPercentRow.setValue(settings.entryThresholdPercent.toFloat())
        binding.exitThresholdPercentRow.setValue(settings.exitThresholdPercent.toFloat())
        binding.windowMsRow.setValue(settings.windowMs.toFloat())
    }

    private fun resetAll() {
        val defaults = MicrophoneSettings()
        repository.save(defaults)
        populateFields(defaults)
    }

    private fun updateSettings(update: (MicrophoneSettings) -> MicrophoneSettings) {
        repository.save(update(repository.get()))
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
