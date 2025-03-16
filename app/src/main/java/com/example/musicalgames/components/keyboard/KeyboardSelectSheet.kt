package com.example.musicalgames.components.keyboard

import android.content.Context
import android.widget.Button
import com.example.musicalgames.R
import com.example.musicalgames.utils.Note
import com.google.android.material.bottomsheet.BottomSheetDialog

class KeyboardSelectSheet (context: Context, private val onConfirmAction: (grayedOut: Set<Int>) -> Unit, grayedOut: Set<Int>) :
    BottomSheetDialog(context), KeyboardListener {
    private var keyboardView: KeyboardView
    private val minNote = Note("C4")
    private val maxNote = Note("B4")
    init {
        setContentView(R.layout.keyboard_input_sheet)
        keyboardView = findViewById(R.id.keyboard_view) ?: throw NullPointerException("keyboardView can't be found")
        keyboardView.setRange(minNote, maxNote)
        keyboardView.setGrayedOutSet(grayedOut)
        keyboardView.registerListener(this)
        val confirmButton = findViewById<Button>(R.id.confirm_button) ?: throw NullPointerException("confirm button not found")
        confirmButton.setOnClickListener{
            onConfirm()
        }
    }

    override fun onKeyClicked(key: Note) {
        if(keyboardView.isGrayedOut(key.midiCode)) {
            keyboardView.unsetGrayedOut(key.midiCode)
        } else {
            keyboardView.setGrayedOut(key.midiCode)
        }
    }
    fun onConfirm() {
        val notes : MutableSet<Int> = mutableSetOf()
        for(i in minNote.midiCode until maxNote.midiCode+1) {
            if(keyboardView.isGrayedOut(i)) {
                notes.add(i)
            }
        }
        onConfirmAction(notes)
        dismiss()
    }
}
