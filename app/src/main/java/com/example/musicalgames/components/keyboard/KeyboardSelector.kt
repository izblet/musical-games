package com.example.musicalgames.components.keyboard

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import com.example.musicalgames.R
import com.example.musicalgames.utils.Note
import com.google.android.material.bottomsheet.BottomSheetDialog

class KeyboardSelector @JvmOverloads constructor (context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : FrameLayout(context, attributeSet, defStyle){
    private val minNote = Note("C4")
    private val maxNote = Note("B4")
    private var notePreview :KeyboardView
    private var mainLayout : FrameLayout
    private var inputDialog:KeyboardDialog

    init {
        LayoutInflater.from(context).inflate(R.layout.keyboard_input, this, true)
        mainLayout = findViewById(R.id.main_layout)
        notePreview = findViewById(R.id.preview)
        notePreview.setRange(minNote, maxNote)
        notePreview.setGrayedOut()
        notePreview.setOutlined(false)
        notePreview.setDisabled(true)

        inputDialog = KeyboardDialog(context)
        setOnClickListener {
            inputDialog.show(notePreview.getGrayedOut())
        }
    }

    inner class KeyboardDialog(context: Context) : BottomSheetDialog(context), KeyboardListener {
        private var keyboardView: KeyboardView
        private val minNote = Note("C4")
        private val maxNote = Note("B4")

        init {
            setContentView(R.layout.keyboard_bottom_sheet)
            keyboardView = findViewById(R.id.keyboard_view)
                ?: throw NullPointerException("keyboardView can't be found")
            keyboardView.setRange(minNote, maxNote)
            keyboardView.setGrayedOut()
            keyboardView.registerListener(this)
            val confirmButton = findViewById<Button>(R.id.confirm_button)
                ?: throw NullPointerException("confirm button not found")
            confirmButton.setOnClickListener {
                onConfirm()
            }
        }
        fun show(grayedOutSet: Set<Int>) {
            keyboardView.setGrayedOutSet(grayedOutSet)
            show()
        }

        override fun onKeyClicked(key: Note) {
            if (keyboardView.isGrayedOut(key.midiCode)) {
                keyboardView.unsetGrayedOut(key.midiCode)
            } else {
                keyboardView.setGrayedOut(key.midiCode)
            }
        }

        fun onConfirm() {
            val notes: MutableSet<Int> = mutableSetOf()
            for (i in minNote.midiCode until maxNote.midiCode + 1) {
                if (keyboardView.isGrayedOut(i)) {
                    notes.add(i)
                }
            }
            notePreview.setGrayedOutSet(notes)
            dismiss()
        }
    }
}
