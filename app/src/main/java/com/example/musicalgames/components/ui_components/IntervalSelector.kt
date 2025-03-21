package com.example.musicalgames.components.ui_components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import com.example.musicalgames.R
import com.example.musicalgames.components.palettes.interval_palette.IntervalPaletteView
import com.example.musicalgames.components.keyboard.KeyboardListener
import com.example.musicalgames.components.keyboard.KeyboardView
import com.example.musicalgames.utils.ChromaticNote
import com.example.musicalgames.utils.Interval
import com.example.musicalgames.utils.Note
import com.google.android.material.bottomsheet.BottomSheetDialog

class IntervalSelector @JvmOverloads constructor (context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : FrameLayout(context, attributeSet, defStyle){
   /* private var notePreview : KeyboardView
    private val minNote = Note("C4")
    private val maxNote = Note("B4")
    private var mainLayout : FrameLayout
    private var inputDialog: IntervalDialog

    init {
        LayoutInflater.from(context).inflate(R.layout.keyboard_input, this, true)
        mainLayout = findViewById(R.id.main_layout)
        notePreview = findViewById(R.id.preview)
        notePreview.setGrayedOut()
        notePreview.setOutlined(false)
        notePreview.setDisabled(true)

        inputDialog = IntervalDialog(context)
        setOnClickListener {
            inputDialog.show( notePreview.getGrayedOut()
                .mapTo(mutableSetOf()){it - minNote.midiCode}
            )
        }
    }
    fun getSelected() : Set<Interval> {
        val intervalsSelected : MutableSet<Interval> = Interval.entries.toMutableSet()
        val grayedOut = notePreview.getGrayedOut()
        for (interval in grayedOut) {
            intervalsSelected.remove(Interval.fromSemitones(interval))
        }

        return intervalsSelected
    }

    inner class IntervalDialog(context: Context) : BottomSheetDialog(context), KeyboardListener {
        private var intervalView: IntervalPaletteView

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
        fun show(grayedOutSemitones: Set<Int>) {
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

    */
}
