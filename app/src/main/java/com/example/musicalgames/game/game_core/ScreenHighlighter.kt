package com.example.musicalgames.game_activity

class ScreenHighlighter(
    private val onCorrect: () -> Unit,
    private val onWrong: () -> Unit
) {
    fun correct() = onCorrect()
    fun wrong() = onWrong()

    companion object {
        const val FLASH_DURATION_MS = 900L
    }
}
