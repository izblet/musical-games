package com.example.musicalgames.components.palettes.key_palette

import android.content.Context
import android.util.AttributeSet
import com.example.musicalgames.components.palettes.KeyboardBasedPalette
import com.example.musicalgames.utils.ChromaticNote

class KeyPaletteView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : KeyboardBasedPalette(context, attributeSet, defStyle) {
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