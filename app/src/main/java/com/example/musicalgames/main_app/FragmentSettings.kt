package com.example.musicalgames.main_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.musicalgames.databinding.FragmentSettingsBinding
import com.example.musicalgames.settings.MicrophoneSettings
import com.example.musicalgames.settings.MicrophoneSettingsRepository
import com.google.android.material.slider.Slider

class FragmentSettings : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: MicrophoneSettingsRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        repository = MicrophoneSettingsRepository(requireContext())

        setUpValueText(binding.energyThresholdSlider, binding.energyThresholdValueText) { it.toInt().toString() }
        setUpValueText(binding.maxUncertaintySlider, binding.maxUncertaintyValueText) { it.toString() }
        setUpValueText(binding.entryThresholdPercentSlider, binding.entryThresholdPercentValueText) { it.toInt().toString() }
        setUpValueText(binding.exitThresholdPercentSlider, binding.exitThresholdPercentValueText) { it.toInt().toString() }
        setUpValueText(binding.windowMsSlider, binding.windowMsValueText) { it.toLong().toString() }

        populateFields(repository.get())

        binding.energyThresholdResetButton.setOnClickListener {
            setEnergyThreshold(MicrophoneSettings.DEFAULT_ENERGY_THRESHOLD)
        }
        binding.maxUncertaintyResetButton.setOnClickListener {
            setMaxUncertainty(MicrophoneSettings.DEFAULT_MAX_UNCERTAINTY)
        }
        binding.entryThresholdPercentResetButton.setOnClickListener {
            setEntryThresholdPercent(MicrophoneSettings.DEFAULT_ENTRY_THRESHOLD_PERCENT)
        }
        binding.exitThresholdPercentResetButton.setOnClickListener {
            setExitThresholdPercent(MicrophoneSettings.DEFAULT_EXIT_THRESHOLD_PERCENT)
        }
        binding.windowMsResetButton.setOnClickListener {
            setWindowMs(MicrophoneSettings.DEFAULT_WINDOW_MS)
        }

        binding.resetAllSettingsButton.setOnClickListener { populateFields(MicrophoneSettings()) }
        binding.saveSettingsButton.setOnClickListener { attemptSave() }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /** Keeps a slider's adjacent value-readout TextView in sync as the user drags it. */
    private fun setUpValueText(slider: Slider, valueText: TextView, format: (Float) -> String) {
        valueText.text = format(slider.value)
        slider.addOnChangeListener { _, value, _ -> valueText.text = format(value) }
    }

    private fun populateFields(settings: MicrophoneSettings) {
        setEnergyThreshold(settings.energyThreshold)
        setMaxUncertainty(settings.maxUncertainty)
        setEntryThresholdPercent(settings.entryThresholdPercent)
        setExitThresholdPercent(settings.exitThresholdPercent)
        setWindowMs(settings.windowMs)
    }

    private fun setEnergyThreshold(value: Int) {
        binding.energyThresholdSlider.value = value.toFloat()
    }

    private fun setMaxUncertainty(value: Float) {
        binding.maxUncertaintySlider.value = value
    }

    private fun setEntryThresholdPercent(value: Int) {
        binding.entryThresholdPercentSlider.value = value.toFloat()
    }

    private fun setExitThresholdPercent(value: Int) {
        binding.exitThresholdPercentSlider.value = value.toFloat()
    }

    private fun setWindowMs(value: Long) {
        binding.windowMsSlider.value = value.toFloat()
    }

    private fun currentSettings(): MicrophoneSettings {
        return MicrophoneSettings(
            energyThreshold = binding.energyThresholdSlider.value.toInt(),
            maxUncertainty = binding.maxUncertaintySlider.value,
            entryThresholdPercent = binding.entryThresholdPercentSlider.value.toInt(),
            exitThresholdPercent = binding.exitThresholdPercentSlider.value.toInt(),
            windowMs = binding.windowMsSlider.value.toLong()
        )
    }

    private fun attemptSave() {
        repository.save(currentSettings())
        findNavController().navigateUp()
    }
}
