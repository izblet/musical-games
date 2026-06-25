package com.example.musicalgames.game.game_core.input

import com.example.musicalgames.music_model.Note

/** What [MicrophoneNoteDetector] reports for a given sample - a note starting or finishing,
 * never both at once. */
sealed interface NoteEvent {
    val note: Note
    data class Started(override val note: Note) : NoteEvent
    data class Finished(override val note: Note) : NoteEvent
}
