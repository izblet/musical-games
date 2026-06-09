package com.example.musicalgames.components.palettes

import android.content.Context
import android.util.AttributeSet
import com.example.musicalgames.music_model.ChromaticNote

class PreviewPalette @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : KeyboardBasedPalette(context, attributeSet, defStyle) {
    private var onClickFun : ((ChromaticNote)->Unit)? = null

    fun setOnClickAction(action: ((ChromaticNote)->Unit)) {
        onClickFun = action
    }

    override fun keyLabel(note: ChromaticNote): String {
        return ""
    }

    override fun onClickAction(note: ChromaticNote) {
        onClickFun?.let { it(note) }
    }

}