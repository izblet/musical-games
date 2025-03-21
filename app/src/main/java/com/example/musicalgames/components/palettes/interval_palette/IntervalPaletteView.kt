package com.example.musicalgames.components.palettes.interval_palette

import android.content.Context
import com.example.musicalgames.components.palettes.KeyboardBasedPalette
import com.example.musicalgames.utils.ChromaticNote
import com.example.musicalgames.utils.Interval


class IntervalPaletteView(context: Context) : KeyboardBasedPalette(context) {
    private var listener: IntervalPaletteListener? = null
    override fun keyLabel(note: ChromaticNote): String {
       val interval = Interval.fromSemitones(note.ordinal)
       return interval.name
    }


    fun registerListener(listener: IntervalPaletteListener) {
        this.listener = listener
    }

    override fun onClickAction(note: ChromaticNote) {
       listener?.onClicked(Interval.fromSemitones(note.ordinal))
    }

}