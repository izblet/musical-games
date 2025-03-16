package com.example.musicalgames.components.keyboard

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import com.example.musicalgames.R
import com.example.musicalgames.utils.Note
import com.google.android.material.bottomsheet.BottomSheetDialog

class KeyboardSelectSheet (context: Context, private val onOptionSelected: (String) -> Unit) :
    BottomSheetDialog(context), KeyboardListener {
    private val view: KeyboardView = KeyboardView(context, null)
    init {
        view.setRange(Note("C4"), Note("B4"))
        setContentView(view)
        view.registerListener(this)
    }

    override fun onKeyClicked(key: Note) {
        if(view.isGrayedOut(key.midiCode)) {
            view.unsetGrayedOut(key.midiCode)
        } else {
            view.setGrayedOut(key.midiCode)
        }
    }
}
