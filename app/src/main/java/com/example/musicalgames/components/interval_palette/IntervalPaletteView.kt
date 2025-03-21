package com.example.musicalgames.components.interval_palette

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import com.example.musicalgames.components.ComponentPaints.getBlackFillPaint
import com.example.musicalgames.components.ComponentPaints.getBlackStrokePaint
import com.example.musicalgames.components.ComponentPaints.getBlackTextPaint
import com.example.musicalgames.components.ComponentPaints.getWhiteFillPaint
import com.example.musicalgames.components.ComponentPaints.getWhiteStrokePaint
import com.example.musicalgames.components.ComponentPaints.getWhiteTextPaint
import com.example.musicalgames.components.KeyboardBasedPalette
import com.example.musicalgames.utils.ChromaticNote
import com.example.musicalgames.utils.DiatonicNote
import com.example.musicalgames.utils.Interval
import kotlin.math.floor


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