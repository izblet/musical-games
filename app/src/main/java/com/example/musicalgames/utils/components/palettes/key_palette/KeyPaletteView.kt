package com.example.musicalgames.utils.components.palettes.key_palette

import android.content.Context
import android.util.AttributeSet
import com.example.musicalgames.utils.components.palettes.KeyboardBasedPalette
import com.example.musicalgames.music_model.ChromaticNote
import com.example.musicalgames.music_model.display.NoteSpelling
import com.example.musicalgames.music_model.display.SpellingPreference

class KeyPaletteView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : KeyboardBasedPalette(context, attributeSet, defStyle) {
    private var listener: KeyPaletteListener? = null


    fun registerListener(listener: KeyPaletteListener) {
        this.listener = listener
    }

    override fun keyLabel(note: ChromaticNote): String {
        if(note.isDiatonic())
            return NoteSpelling.spell(note, SpellingPreference.SHARPS)
        return "${NoteSpelling.spell(note, SpellingPreference.FLATS)}\n${NoteSpelling.spell(note, SpellingPreference.SHARPS)}"
    }

    override fun onClickAction(note: ChromaticNote) {
        listener?.onClicked(note)
    }

}