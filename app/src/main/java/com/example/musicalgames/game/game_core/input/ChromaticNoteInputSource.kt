package com.example.musicalgames.game.game_core.input

import com.example.musicalgames.music_model.ChromaticNote
import kotlinx.coroutines.flow.Flow

/**
 * Sibling of [NoteInputSource] for games that work in pitch-class space (no octave concept
 * in their answer/onscreen palette - Chords, and eventually Mental/Circle).
 */
interface ChromaticNoteInputSource {
    val noteSelected: Flow<ChromaticNote>

    fun start()
    fun stop()
}
