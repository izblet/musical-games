package com.example.musicalgames.components.ui_components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import com.example.musicalgames.R
import com.example.musicalgames.components.palettes.interval_palette.IntervalPaletteView
import com.example.musicalgames.components.palettes.PreviewPalette
import com.example.musicalgames.components.palettes.interval_palette.IntervalPaletteListener
import com.example.musicalgames.music_model.ChromaticNote
import com.example.musicalgames.music_model.Interval
import com.google.android.material.bottomsheet.BottomSheetDialog
class IntervalSelector @JvmOverloads constructor (context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : FrameLayout(context, attributeSet, defStyle){
    private var notePreview : PreviewPalette
    private var mainLayout : FrameLayout
    private var inputDialog: IntervalDialog

    init {
        LayoutInflater.from(context).inflate(R.layout.interval_input, this, true)
        mainLayout = findViewById(R.id.main_layout)
        notePreview = mainLayout.findViewById(R.id.preview)
        notePreview.setGrayedOut()

        inputDialog = IntervalDialog(context)
        notePreview.setOnClickAction {
                _ -> inputDialog.show(notePreview.getGrayedOut())
        }
    }
    fun getSelected() : Set<Interval> {
        val chromaticSelected : MutableSet<ChromaticNote> = ChromaticNote.entries.toMutableSet()
        val grayedOut = notePreview.getGrayedOut()
        for (note in grayedOut) {
            chromaticSelected.remove(note)
        }

        return chromaticSelected.mapTo(mutableSetOf()){Interval.fromSemitones(it.ordinal)}
    }
    fun clearSelection() {
        notePreview.setGrayedOut()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        notePreview.isEnabled = enabled
    }

    inner class IntervalDialog(context: Context) : BottomSheetDialog(context), IntervalPaletteListener {
        private var keyboardView: IntervalPaletteView

        init {
            setContentView(R.layout.interval_bottom_sheet)
            keyboardView = findViewById(R.id.keyboard_view)
                ?: throw NullPointerException("keyboardView can't be found")
            keyboardView.setGrayedOut()
            keyboardView.registerListener(this)
            val confirmButton = findViewById<Button>(R.id.confirm_button)
                ?: throw NullPointerException("confirm button not found")
            confirmButton.setOnClickListener {
                onConfirm()
            }
        }
        fun show(grayedOutSet: Set<ChromaticNote>) {
            keyboardView.setGrayedOutSet(grayedOutSet)
            show()
        }

        fun onConfirm() {
            val notes: MutableSet<ChromaticNote> = mutableSetOf()
            for (i in ChromaticNote.entries) {
                if (keyboardView.isGrayedOut(i)) {
                    notes.add(i)
                }
            }
            notePreview.setGrayedOutSet(notes)
            dismiss()
        }

        override fun onClicked(interval: Interval) {
            if(keyboardView.isGrayedOut(interval)) {
                keyboardView.unsetGrayedOut(interval)
            } else {
                keyboardView.setGrayedOut(interval)
            }
        }
    }
}
