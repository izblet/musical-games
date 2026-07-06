package com.example.musicalgames.game.game_core.input

import com.example.musicalgames.music_model.MusicUtil
import com.example.musicalgames.music_model.Note
import kotlin.math.roundToInt

/**
 * Turns a stream of raw microphone pitch+energy samples into discrete, edge-triggered
 * [NoteEvent]s: a [NoteEvent.Started] exactly once when a note becomes recognised, and a
 * [NoteEvent.Finished] exactly once when it stops being recognised - never both for the same
 * sample, and null on every other sample. Callers that only care about complete, settled notes
 * can listen for [NoteEvent.Finished] only; callers that want to react as soon as a note is
 * confidently identified (not caring yet whether/when it ends) can listen for
 * [NoteEvent.Started] only - both are derived from the same underlying recognition state, so
 * either view is always consistent with the other.
 *
 * Recognition rule: a note is "recognised" once it accounts for more than
 * [entryThresholdPercent]% of the samples within the trailing [windowMs]
 * milliseconds; once recognised, it stays recognised until EITHER its share of
 * the window drops below [exitThresholdPercent]% (defaults to [entryThresholdPercent]),
 * OR [onsetDetector] reports a fresh attack - so a note that's re-attacked (sung/played
 * again with no pitch change and no silence gap) is cleared (and reported as finished)
 * instead of only ever clearing via prevalence decay. Either way, the note being left has its
 * own samples purged from the window, not just its recognised-flag cleared: an onset reacts on
 * transient-time, well before the window's own vote has caught up, so leaving its stale samples
 * in place would let the entry-check immediately hand back the note being left rather than the
 * one being attacked, whenever the attack is actually a pitch change. Only that note's samples
 * are purged (not the whole window), so any evidence already accumulating for a genuinely new
 * note is preserved rather than thrown away. Pitch is snapped to the nearest chromatic note.
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
    fun onSample(spicePitch: Float?, energy: Float, timestampMs: Long): NoteEvent? {
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
                //purge the note being left either way (not the whole window): decay means it
                //genuinely left the window, and an onset means we can't trust its still-high
                //count as the answer for what comes next, since the window's vote lags behind
                //the onset's transient-time reaction. Purging only this note (rather than
                //everything) keeps any evidence already accumulating for a different, genuinely
                //new note intact, so a pitch-change attack can still be recognised as soon as
                //its own count qualifies, not after a full rebuild from empty.
                purgeNote(current)
                return NoteEvent.Finished(current)
            }
        }

        if (recognisedNote == null && window.size >= minWindowSize) {
            val (candidate, candidateCount) = mostPrevalentNote()
            if (candidate != null && meetsThreshold(candidateCount, entryThresholdPercent)) {
                recognisedNote = candidate
                return NoteEvent.Started(candidate)
            }
        }

        return null
    }

    /** Discards whatever is currently being recognised, without reporting it - for a caller
     * that knows the recent input shouldn't count (e.g. it overlapped the caller's own
     * speaker playback), so a stale recognition can't surface later as a misleading report
     * once it happens to clear. */
    fun reset() {
        recognisedNote = null
        window.clear()
        counts.fill(0)
        countsByFrequency.clear()
        onsetDetector.reset()
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
