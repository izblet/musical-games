package com.example.musicalgames.game.game_core.input

import com.example.musicalgames.music_model.Note
import kotlinx.coroutines.flow.Flow

/**
 * A pluggable source of discrete, edge-triggered note-selected events
 * (e.g. tapping an onscreen keyboard, or singing/playing into a mic).
 */
interface NoteInputSource {
    val noteSelected: Flow<Note>

    fun start()
    fun stop()
}
