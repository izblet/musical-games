package com.example.musicalgames.game.game_core.input

import com.example.musicalgames.music_model.MusicUtil
import com.example.musicalgames.music_model.Note
import kotlin.math.roundToInt

/**
 * Turns a stream of raw microphone pitch samples into discrete, edge-triggered
 * note-onset events: returns a [Note] exactly once when that note becomes the
 * dominant pitch over a trailing time window, and null on every other sample
 * (including while a note remains dominant, or while nothing clears the
 * threshold).
 *
 * Recognition rule: a note is "recognised" once it accounts for more than
 * [entryThreshold] of the samples within the trailing [windowMs] milliseconds;
 * once recognised, it stays recognised until its prevalence in the window
 * drops below [exitThreshold] (defaults to [entryThreshold], i.e. no
 * hysteresis unless a lower exit value is supplied). Pitch is snapped to the
 * nearest chromatic note regardless of which notes the current level expects
 * - a snapped note that isn't a valid answer is just handled as a wrong
 * answer further up, the same as a wrong tap.
 *
 * The snapping/windowing/prevalence logic below is deliberately self-contained
 * (no Android/coroutine types, no game-specific knowledge) so it can be lifted
 * out wholesale if this moves to an external pitch-recognition tool later -
 * see the input-method-abstraction project notes.
 */
class MicrophoneNoteDetector(
    private val entryThreshold: Float = 0.95f,
    private val windowMs: Long = DEFAULT_WINDOW_MS,
    private val exitThreshold: Float = entryThreshold
) {
    companion object {
        const val DEFAULT_WINDOW_MS = 300L
    }


    private data class Sample(val timestampMs: Long, val note: Note?)

    private val window = ArrayDeque<Sample>()
    private var recognisedNote: Note? = null

    /** Feed one raw pitch sample (null = no clear pitch this sample, e.g. silence). */
    fun onPitchSample(spicePitch: Float?, timestampMs: Long): Note? {
        window.addLast(Sample(timestampMs, spicePitch?.let(::snapToNearestNote)))
        evictOldSamples(timestampMs)

        val current = recognisedNote
        val stillDominant = current != null && prevalenceOf(current) >= exitThreshold
        if (!stillDominant) {
            recognisedNote = null
        }

        if (recognisedNote == null) {
            val (candidate, candidatePrevalence) = mostPrevalentNote()
            if (candidate != null && candidatePrevalence >= entryThreshold) {
                recognisedNote = candidate
                return candidate
            }
        }

        return null
    }

    // --- self-contained recognition core (candidate for extraction later) ---

    private val spiceAtMidi0 = MusicUtil.spice(0)
    private val spicePerSemitone = MusicUtil.spice(1) - spiceAtMidi0

    private fun snapToNearestNote(spicePitch: Float): Note {
        val semitoneOffset = (spicePitch - spiceAtMidi0) / spicePerSemitone
        return Note(semitoneOffset.roundToInt())
    }

    private fun evictOldSamples(nowMs: Long) {
        while (window.isNotEmpty() && nowMs - window.first().timestampMs > windowMs) {
            window.removeFirst()
        }
    }

    private fun mostPrevalentNote(): Pair<Note?, Float> {
        if (window.isEmpty()) return null to 0f
        val mostCommon = window.mapNotNull { it.note }
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?: return null to 0f
        return mostCommon.key to mostCommon.value.toFloat() / window.size
    }

    private fun prevalenceOf(note: Note): Float {
        if (window.isEmpty()) return 0f
        return window.count { it.note == note }.toFloat() / window.size
    }
}
