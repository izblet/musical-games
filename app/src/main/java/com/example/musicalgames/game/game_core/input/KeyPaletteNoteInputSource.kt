package com.example.musicalgames.game.game_core.input

import com.example.musicalgames.music_model.ChromaticNote
import com.example.musicalgames.utils.components.palettes.key_palette.KeyPaletteListener
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** Bridges KeyPaletteView's tap callback into the common ChromaticNoteInputSource shape. */
class KeyPaletteNoteInputSource : ChromaticNoteInputSource, KeyPaletteListener {
    // extraBufferCapacity = 1: tryEmit() is called synchronously from a UI touch callback, with
    // no guarantee the collector (an independently-scheduled coroutine) is suspended and ready
    // at that instant - see OnscreenNoteInputSource for the empirical confirmation of this.
    private val _noteSelected = MutableSharedFlow<ChromaticNote>(extraBufferCapacity = 1)
    override val noteSelected: SharedFlow<ChromaticNote> = _noteSelected.asSharedFlow()

    override fun start() {}
    override fun stop() {}

    override fun onClicked(note: ChromaticNote) {
        _noteSelected.tryEmit(note)
    }
}
