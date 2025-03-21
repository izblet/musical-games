package com.example.musicalgames.components.key_palette

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
import kotlin.math.floor

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