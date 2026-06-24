package com.example.musicalgames.game.game_core.input

import com.example.musicalgames.music_model.MusicUtil
import com.example.musicalgames.music_model.Note
import kotlin.math.roundToInt

/**
 * Turns a stream of raw microphone pitch+energy samples into discrete, edge-triggered
 * note-onset events: returns a [Note] exactly once when that note becomes the
 * dominant pitch over a trailing time window, and null on every other sample
 * (including while a note remains dominant, or while nothing clears the
 * threshold).
 *
 * Recognition rule: a note is "recognised" once it accounts for more than
 * [entryThresholdPercent]% of the samples within the trailing [windowMs]
 * milliseconds; once recognised, it stays recognised until EITHER its share of
 * the window drops below [exitThresholdPercent]% (defaults to [entryThresholdPercent]),
 * OR [onsetDetector] reports a fresh attack - so a note that's re-attacked (sung/played
 * again with no pitch change and no silence gap) is cleared and immediately re-recognised,
 * instead of only ever clearing via prevalence decay. Pitch is snapped to the nearest
 * chromatic note.
 */
class MicrophoneNoteDetector(
    private val minWindowSize: Int,
    private val entryThresholdPercent: Int,
    private val windowMs: Long,
    private val exitThresholdPercent: Int,
    private val onsetDetector: OnsetDetector
) {
    companion object {
        //suggested fraction of the caller's own expected window size (it knows the actual
        //sample interval, this class doesn't) to use when computing minWindowSize - not
        //user-tunable, unlike the constructor params above (see MicrophoneSettings)
        const val DEFAULT_MIN_WINDOW_SIZE_PERCENT = 50
    }

    private data class Sample(val timestampMs: Long, val note: Note?)

    private val window = ArrayDeque<Sample>()
    private var recognisedNote: Note? = null

    //maps midicode to the frequency of the note
    private val counts = IntArray(128)
    //for each frequency, we keep a mutable set of notes with this frequency (update in time log n)
    private val countsByFrequency = java.util.TreeMap<Int, MutableSet<Note>>()

    /** Feed one raw pitch+energy sample (null pitch = no clear pitch this sample, e.g. silence). */
    fun onSample(spicePitch: Float?, energy: Float, timestampMs: Long): Note? {
        val note = spicePitch?.let(::snapToNearestNote)
        window.addLast(Sample(timestampMs, note))
        note?.let(::incrementCount)
        evictOldSamples(timestampMs)

        val onsetFired = onsetDetector.onEnergySample(energy, timestampMs)

        val current = recognisedNote
        if (current != null) {
            val decayed = !meetsThreshold(counts[current.midiCode], exitThresholdPercent)
            if (decayed || onsetFired) {
                recognisedNote = null
                //decay means the note has genuinely left the window - purge it. An attack-
                //triggered clear leaves counts untouched: the audio hasn't changed, so the same
                //note is still dominant and should immediately re-qualify on the next sample.
                if (decayed) purgeNote(current)
                return null
            }
        }

        if (recognisedNote == null && window.size >= minWindowSize) {
            val (candidate, candidateCount) = mostPrevalentNote()
            if (candidate != null && meetsThreshold(candidateCount, entryThresholdPercent)) {
                recognisedNote = candidate
                return candidate
            }
        }

        return null
    }

    private fun purgeNote(note: Note) {
        window.removeAll { it.note == note }
        val count = counts[note.midiCode]
        if (count > 0) {
            removeFromBucket(note, count)
            counts[note.midiCode] = 0
        }
    }

    private fun mostPrevalentNote(): Pair<Note?, Int> {
        val (topCount, topNotes) = countsByFrequency.lastEntry() ?: return null to 0
        return topNotes.first() to topCount
    }

    private fun meetsThreshold(count: Int, thresholdPercent: Int): Boolean {
        return window.isNotEmpty() && count * 100 >= thresholdPercent * window.size
    }

    // --- self-contained recognition core (candidate for extraction later) ---

    private val spiceAtMidi0 = MusicUtil.spice(0)
    private val spicePerSemitone = MusicUtil.spice(1) - spiceAtMidi0

    private fun snapToNearestNote(spicePitch: Float): Note {
        val semitoneOffset = (spicePitch - spiceAtMidi0) / spicePerSemitone
        return Note(semitoneOffset.roundToInt())
    }

    private fun incrementCount(note: Note) {
        val oldCount = counts[note.midiCode]
        if (oldCount > 0) removeFromBucket(note, oldCount)
        counts[note.midiCode] = oldCount + 1
        countsByFrequency.getOrPut(oldCount + 1) { mutableSetOf() }.add(note)
    }
    private fun evictOldSamples(nowMs: Long) {
        while (window.isNotEmpty() && nowMs - window.first().timestampMs > windowMs) {
            val evicted = window.removeFirst()
            evicted.note?.let(::decrementCount)
        }
    }
    private fun decrementCount(note: Note) {
        val oldCount = counts[note.midiCode]
        removeFromBucket(note, oldCount)
        val newCount = oldCount - 1
        counts[note.midiCode] = newCount
        if (newCount > 0) countsByFrequency.getOrPut(newCount) { mutableSetOf() }.add(note)
    }

    private fun removeFromBucket(note: Note, count: Int) {
        val bucket = countsByFrequency[count] ?: return
        bucket.remove(note)
        if (bucket.isEmpty()) countsByFrequency.remove(count)
    }


}
