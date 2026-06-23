package com.example.musicalgames.settings

import android.content.Context
import com.example.musicalgames.music_model.Interval

class IntervalColorSettingsRepository(context: Context) {
    private val prefs = context.getSharedPreferences(MicrophoneSettingsRepository.PREFS_NAME, Context.MODE_PRIVATE)

    fun get(): IntervalColorSettings {
        val colors = Interval.entries.associateWith { interval ->
            val default = IntervalColorSettings.DEFAULT_COLORS.getValue(interval)
            prefs.getInt(keyFor(interval), default)
        }
        return IntervalColorSettings(colors)
    }

    fun save(settings: IntervalColorSettings) {
        val editor = prefs.edit()
        settings.colors.forEach { (interval, color) -> editor.putInt(keyFor(interval), color) }
        editor.apply()
    }

    private fun keyFor(interval: Interval) = "interval_color_${interval.name}"
}
