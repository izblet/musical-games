package com.example.musicalgames.game.game_core.input

import com.example.musicalgames.music_model.ChromaticNote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Adapts [MicrophoneNoteInput]'s octave-specific Note stream down to ChromaticNote, for games
 * that work in pitch-class space - no duplicated detection logic, just a type-level translation.
 */
class MicrophoneChromaticNoteInput(private val delegate: MicrophoneNoteInput) : ChromaticNoteInputSource {
    override val noteSelected: Flow<ChromaticNote> = delegate.noteSelected.map { it.noteChromatic }

    override fun start() = delegate.start()
    override fun stop() = delegate.stop()
    override fun reset() = delegate.reset()
    override fun mute() = delegate.mute()
    override fun unmuteWhenQuiet() = delegate.unmuteWhenQuiet()

    //TODO: must be wired into the game's endGame(), same as MicrophoneNoteInput.release() itself.
    fun release() = delegate.release()
}
