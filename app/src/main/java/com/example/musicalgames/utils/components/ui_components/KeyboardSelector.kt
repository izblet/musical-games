package com.example.musicalgames.utils.components.ui_components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import com.example.musicalgames.R
import com.example.musicalgames.utils.components.palettes.PreviewPalette
import com.example.musicalgames.utils.components.palettes.key_palette.KeyPaletteListener
import com.example.musicalgames.utils.components.palettes.key_palette.KeyPaletteView
import com.example.musicalgames.music_model.ChromaticNote
import com.google.android.material.bottomsheet.BottomSheetDialog

class KeyboardSelector @JvmOverloads constructor (context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : FrameLayout(context, attributeSet, defStyle){
    private var notePreview : PreviewPalette
    private var mainLayout : FrameLayout
    private var inputDialog: KeyboardDialog

    init {
        LayoutInflater.from(context).inflate(R.layout.keyboard_input, this, true)
        mainLayout = findViewById(R.id.main_layout)
        notePreview = mainLayout.findViewById(R.id.preview)
        notePreview.setGrayedOut()

        inputDialog = KeyboardDialog(context)
        notePreview.setOnClickAction { _ ->
            inputDialog.show(notePreview.getGrayedOut())
        }

    }
    fun getSelected() : Set<ChromaticNote> {
        val chromaticSelected : MutableSet<ChromaticNote> = ChromaticNote.entries.toMutableSet()
        val grayedOut = notePreview.getGrayedOut()
        for (note in grayedOut) {
            chromaticSelected.remove(note)
        }

        return chromaticSelected
    }

    fun clearSelection() {
        notePreview.setGrayedOut()
    }

    fun setSelected(notes: Set<ChromaticNote>) {
        notePreview.setGrayedOutSet(ChromaticNote.entries.toSet() - notes)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        notePreview.isEnabled = enabled
    }

    inner class KeyboardDialog(context: Context) : BottomSheetDialog(context), KeyPaletteListener {
        private var keyboardView: KeyPaletteView

        init {

            setContentView(R.layout.keyboard_bottom_sheet)
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

        override fun onClicked(note: ChromaticNote) {
            if(keyboardView.isGrayedOut(note)) {
                keyboardView.unsetGrayedOut(note)
            } else {
                keyboardView.setGrayedOut(note)
            }
        }
    }
}
