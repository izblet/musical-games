package com.example.musicalgames.components.palettes.key_palette

import android.content.Context
import com.example.musicalgames.components.palettes.KeyboardBasedPalette
import com.example.musicalgames.utils.ChromaticNote

class KeyPaletteView(context: Context) : KeyboardBasedPalette(context) {
    private var listener: KeyPaletteListener? = null


    fun registerListener(listener: KeyPaletteListener) {
        this.listener = listener
    }

    override fun keyLabel(note: ChromaticNote): String {
       return note.toString()
    }

    override fun onClickAction(note: ChromaticNote) {
        listener?.onClicked(note)
    }

}