package com.example.musicalgames.game.game_core.input

import com.example.musicalgames.music_model.Note
import com.example.musicalgames.utils.components.keyboard.KeyboardListener
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** Bridges KeyboardView's tap callback into the common NoteInputSource shape. */
class OnscreenNoteInputSource : NoteInputSource, KeyboardListener {
    // tryEmit() is called synchronously from a UI touch callback, with no guarantee that the
    // collector (an independently-scheduled coroutine) is suspended and ready at that instant -
    // a buffer of 0 (the MutableSharedFlow default) silently drops the tap in that case.
    private val _noteSelected = MutableSharedFlow<Note>(extraBufferCapacity = 1)
    override val noteSelected: SharedFlow<Note> = _noteSelected.asSharedFlow()

    override fun start() {}
    override fun stop() {}

    override fun onKeyClicked(key: Note) {
        _noteSelected.tryEmit(key)
    }
}
