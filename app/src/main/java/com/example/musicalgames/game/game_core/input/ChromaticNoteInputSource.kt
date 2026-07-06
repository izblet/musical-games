package com.example.musicalgames.game.game_core.input

import com.example.musicalgames.music_model.ChromaticNote
import kotlinx.coroutines.flow.Flow

/**
 * Sibling of [NoteInputSource] for games that work in pitch-class space (no octave concept
 * in their answer/onscreen palette - Chords, and eventually Mental/Circle). See
 * [NoteInputSource] for what [noteStarted]/[noteFinished] mean.
 */
interface ChromaticNoteInputSource {
    val noteStarted: Flow<ChromaticNote>
    val noteFinished: Flow<ChromaticNote>

    fun start()
    fun stop()

    /** See [NoteInputSource.reset]. No-op by default. */
    fun reset() {}

    /** See [NoteInputSource.mute]. No-op by default. */
    fun mute() {}

    /** See [NoteInputSource.unmuteWhenQuiet]. No-op by default. */
    fun unmuteWhenQuiet() {}
}
