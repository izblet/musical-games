package com.example.musicalgames.game.games.chords

import android.content.Context
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.musicalgames.game.game_core.input.KeyPaletteNoteInputSource
import com.example.musicalgames.utils.components.palettes.key_palette.KeyPaletteView
import com.example.musicalgames.music_model.ChromaticNote
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


class ViewChords(
    context: Context,
    viewModel: ViewModelChords,
    lifecycleOwner: LifecycleOwner,
    noteInputSource: KeyPaletteNoteInputSource
) : ViewGroup(context) {
    private val notePalette = KeyPaletteView(context)
    private val textView = TextView(context).apply {
        text="text"
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f)
    }
    private val keyboardHeightRatio=.5


    init {
        notePalette.registerListener(noteInputSource)

        addView(notePalette)
        addView(textView)

        lifecycleOwner.lifecycleScope.launch {
            viewModel.viewState.collect {
                value->updateView(value)
            }
        }
    }

    private fun updateView(newState: ViewModelChords.ViewState) {
        textView.text = newState.screenMessage
        val grayedOut: MutableSet<ChromaticNote> = mutableSetOf()
        for(note  in ChromaticNote.entries) {
            if (note !in newState.highlightedNotes) {
                grayedOut.add(note)
            }
        }
        notePalette.setGrayedOutSet(grayedOut)
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        val messageHeight = (height * 1) / 10
        textView.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(messageHeight, MeasureSpec.EXACTLY)
        )

        val paletteHeight :Int = (keyboardHeightRatio * height.toFloat()).roundToInt()

        notePalette.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(paletteHeight, MeasureSpec.EXACTLY)
        )

        setMeasuredDimension(width, height)
    }
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val keyPaletteHeight = (height.toFloat() * keyboardHeightRatio).roundToInt()
        notePalette.layout(0, height - keyPaletteHeight, width, height)
        textView.layout(0,0,width,height)
    }
}