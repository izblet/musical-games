package com.example.musicalgames.game.game_core.input

/** What a note event means after [RepeatNoteConfirmGesture] applies the repeat-trigger rule. */
sealed interface NoteGestureEvent<out T> {
    data class NoteSelected<T>(val note: T) : NoteGestureEvent<T>
    object Confirmed : NoteGestureEvent<Nothing>
}

/**
 * Turns an immediate repeat of a designated "trigger" note (typically a chord/melody's root)
 * into a confirm signal, while passing every other note through unchanged - including the
 * *first* occurrence of the trigger note itself, so it still counts as a normal answer note if
 * it legitimately belongs in the answer. Only a second, immediately-following occurrence (with
 * nothing else in between) is consumed as the confirm gesture instead of a second selection.
 *
 * Pure and Android/coroutine-free, like [MicrophoneNoteDetector] - the consuming game logic
 * drives it by feeding it one note at a time. Generic over the note representation ([T] is
 * Note, ChromaticNote, ...) since the repeat-detection logic only needs equality, not the
 * specific type - reusable across any game that wants "repeat the root to confirm" regardless
 * of input method (onscreen tap or external instrument both flow through the same note stream).
 */
class RepeatNoteConfirmGesture<T> {
    private var triggerNote: T? = null
    private var lastNote: T? = null

    /** Call whenever the answer-relevant note changes (e.g. a new question/chord starts). */
    fun setTrigger(trigger: T) {
        triggerNote = trigger
        lastNote = null
    }

    /** Feed one incoming note event. */
    fun onNote(note: T): NoteGestureEvent<T> {
        val isConfirm = triggerNote != null && note == triggerNote && lastNote == triggerNote
        lastNote = note
        return if (isConfirm) NoteGestureEvent.Confirmed else NoteGestureEvent.NoteSelected(note)
    }
}
