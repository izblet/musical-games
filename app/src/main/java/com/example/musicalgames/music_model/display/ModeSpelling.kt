package com.example.musicalgames.music_model.display

import com.example.musicalgames.music_model.Mode

object ModeSpelling {
    fun modal(mode: Mode): String = mode.name.lowercase().replaceFirstChar { it.uppercaseChar() }

    fun common(mode: Mode): String = when (mode) {
        Mode.IONIAN -> "Major"
        Mode.AEOLIAN -> "Minor"
        else -> modal(mode)
    }
}