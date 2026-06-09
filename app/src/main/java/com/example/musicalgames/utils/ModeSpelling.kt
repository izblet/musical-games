package com.example.musicalgames.utils

object ModeSpelling {
    fun modal(mode: Mode): String = mode.name.lowercase().replaceFirstChar { it.uppercaseChar() }

    fun common(mode: Mode): String = when (mode) {
        Mode.IONIAN -> "Major"
        Mode.AEOLIAN -> "Minor"
        else -> modal(mode)
    }
}
