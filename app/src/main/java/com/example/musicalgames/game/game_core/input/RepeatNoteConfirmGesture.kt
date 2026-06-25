package com.example.musicalgames.game.game_core.input

/** What a note event means after [RepeatNoteConfirmGesture] applies the repeat-trigger rule. */
sealed interface NoteGestureEvent<out T> {
    data class NoteSelected<T>(val note: T) : NoteGestureEvent<T>
    object Confirmed : NoteGestureEvent<Nothing>
}

/**
 * Turns two complete, consecutive (start, finish) cycles of a designated "trigger" note
 * (typically a chord/melody's root) into a confirm signal, while passing every other completed
 * note through unchanged - including the *first* complete cycle of the trigger note itself, so
 * it still counts as a normal answer note if it legitimately belongs in the answer. Only a
 * second, immediately-following complete cycle (with nothing else - not even an orphaned or
 * foreign note - in between) is consumed as the confirm gesture instead of a second selection.
 *
 * Driven through [onNoteStarted]/[onNoteFinished] pairs rather than a single "note selected"
 * callback specifically so a note that *finishes* while this gesture is watching but *started*
 * before it was - e.g. the trailing edge of whatever note completed the previous answer,
 * already in flight the moment the caller starts trusting this gesture again - can't be
 * silently counted towards the repeat. With no matching [onNoteStarted] call on record for it,
 * its [onNoteFinished] call is recognised as orphaned and ignored for cycle-counting (still
 * passed through as a normal selection). This holds regardless of how close together the real
 * notes are - it depends on whether a start was actually observed for this exact note, not on
 * timing margins between whichever callers happen to be feeding [onNoteStarted] and
 * [onNoteFinished].
 *
 * Pure and Android/coroutine-free, like [MicrophoneNoteDetector] - the consuming game logic
 * drives it by feeding it note start/finish events one at a time. Generic over the note
 * representation ([T] is Note, ChromaticNote, ...) since the repeat-detection logic only needs
 * equality, not the specific type - reusable across any game that wants "repeat the root to
 * confirm" regardless of input method (onscreen tap or external instrument both flow through
 * the same note stream).
 */
class RepeatNoteConfirmGesture<T> {
    private var triggerNote: T? = null
    //the note currently "open" (started, not yet finished), if any - matched against the
    //corresponding onNoteFinished() call to tell a genuinely-watched cycle apart from an
    //orphaned tail end
    private var pendingStart: T? = null
    //how many complete (start, finish) cycles of the trigger note have been seen back-to-back,
    //with nothing else - not even an orphaned/foreign note - interleaved
    private var completedTriggerCycles = 0

    /** Call whenever the answer-relevant note changes (e.g. a new question/chord starts). */
    fun setTrigger(trigger: T) {
        triggerNote = trigger
        pendingStart = null
        completedTriggerCycles = 0
    }

    /** Call when a note starts - records it as open, to be matched against the corresponding
     * [onNoteFinished] call. */
    fun onNoteStarted(note: T) {
        pendingStart = note
    }

    /** Call when a note finishes. Returns how that completed note should be treated. */
    fun onNoteFinished(note: T): NoteGestureEvent<T> {
        val startedHere = pendingStart == note
        pendingStart = null

        if (!startedHere || triggerNote == null || note != triggerNote) {
            completedTriggerCycles = 0
            return NoteGestureEvent.NoteSelected(note)
        }

        completedTriggerCycles++
        return if (completedTriggerCycles >= 2) {
            completedTriggerCycles = 0
            NoteGestureEvent.Confirmed
        } else {
            NoteGestureEvent.NoteSelected(note)
        }
    }
}
