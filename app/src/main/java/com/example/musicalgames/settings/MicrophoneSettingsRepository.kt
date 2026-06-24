package com.example.musicalgames.settings

import android.content.Context

class MicrophoneSettingsRepository(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun get(): MicrophoneSettings {
        val defaults = MicrophoneSettings()
        return MicrophoneSettings(
            energyThreshold = prefs.getInt(KEY_ENERGY_THRESHOLD, defaults.energyThreshold),
            maxUncertainty = prefs.getFloat(KEY_MAX_UNCERTAINTY, defaults.maxUncertainty),
            entryThresholdPercent = prefs.getInt(KEY_ENTRY_THRESHOLD_PERCENT, defaults.entryThresholdPercent),
            exitThresholdPercent = prefs.getInt(KEY_EXIT_THRESHOLD_PERCENT, defaults.exitThresholdPercent),
            windowMs = prefs.getLong(KEY_WINDOW_MS, defaults.windowMs),
            onsetRiseFactor = prefs.getFloat(KEY_ONSET_RISE_FACTOR, defaults.onsetRiseFactor),
            onsetRefractoryMs = prefs.getLong(KEY_ONSET_REFRACTORY_MS, defaults.onsetRefractoryMs)
        )
    }

    fun save(settings: MicrophoneSettings) {
        prefs.edit()
            .putInt(KEY_ENERGY_THRESHOLD, settings.energyThreshold)
            .putFloat(KEY_MAX_UNCERTAINTY, settings.maxUncertainty)
            .putInt(KEY_ENTRY_THRESHOLD_PERCENT, settings.entryThresholdPercent)
            .putInt(KEY_EXIT_THRESHOLD_PERCENT, settings.exitThresholdPercent)
            .putLong(KEY_WINDOW_MS, settings.windowMs)
            .putFloat(KEY_ONSET_RISE_FACTOR, settings.onsetRiseFactor)
            .putLong(KEY_ONSET_REFRACTORY_MS, settings.onsetRefractoryMs)
            .apply()
    }

    companion object {
        // shared by any future settings-group repository added to this package
        const val PREFS_NAME = "musical_games_settings"

        private const val KEY_ENERGY_THRESHOLD = "microphone_energy_threshold"
        private const val KEY_MAX_UNCERTAINTY = "microphone_max_uncertainty"
        private const val KEY_ENTRY_THRESHOLD_PERCENT = "microphone_entry_threshold_percent"
        private const val KEY_EXIT_THRESHOLD_PERCENT = "microphone_exit_threshold_percent"
        private const val KEY_WINDOW_MS = "microphone_window_ms"
        private const val KEY_ONSET_RISE_FACTOR = "microphone_onset_rise_factor"
        private const val KEY_ONSET_REFRACTORY_MS = "microphone_onset_refractory_ms"
    }
}
