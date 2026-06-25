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

    /** Discards any in-progress recognition without reporting it - e.g. so input that
     * overlapped the caller's own speaker playback can't surface later as a misleading
     * report. No-op by default: sources with no persistent recognition state (like tapping
     * an onscreen keyboard) have nothing to discard. */
    fun reset() {}

    /** Stops reporting notes immediately, and keeps doing so until [unmuteWhenQuiet] is called
     * - deliberately does not start counting down to a resume on its own, since the caller may
     * still be making more of whatever sound caused the mute (e.g. partway through playing a
     * multi-note sequence, where ordinary gaps between notes shouldn't be mistaken for "done").
     * No-op by default. */
    fun mute() {}

    /** Arms a resume: once the source confirms it's safe (e.g. genuine silence for a while,
     * long enough to outlast whatever caused the mute - not just a guessed duration, since how
     * long that takes can vary with room acoustics/hardware buffering), it starts reporting
     * again on its own. No-op while not muted. No-op by default. */
    fun unmuteWhenQuiet() {}
}
