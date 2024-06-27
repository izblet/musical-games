package com.example.musicalgames.games.mental_intervals

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import com.example.musicalgames.components.key_palette.KeyPaletteListener
import com.example.musicalgames.components.key_palette.KeyPaletteView
import com.example.musicalgames.game_activity.ViewModelListener
import kotlin.math.roundToInt

class MentalView(context: Context) : ViewGroup(context), ViewModelListener {

    private var viewModel: MentalViewModel? = null
    private val notePaletteRatio :Float = 2f/3f
    private val keyPaletteView: KeyPaletteView
    private val messageTextView: TextView
    init {
        keyPaletteView = KeyPaletteView(context)
        messageTextView = TextView(context).apply {
            textSize = 20f
            gravity = Gravity.CENTER_HORIZONTAL
            text = ""
        }

        addView(messageTextView)
        addView(keyPaletteView)
    }
    fun setViewModel(viewModel: MentalViewModel) {
        this.viewModel =viewModel
    }
    fun setKeyboardListener(listener: KeyPaletteListener) {
        keyPaletteView.registerListener(listener)
    }

    override fun onDataChanged() { redraw() }
    private fun redraw() {
        messageTextView.text = viewModel?.messageText
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        val messageHeight = (height * 1) / 10
        messageTextView.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(messageHeight, MeasureSpec.EXACTLY)
        )

        val paletteHeight :Int = (notePaletteRatio * height.toFloat()).roundToInt()

        keyPaletteView.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(paletteHeight, MeasureSpec.EXACTLY)
        )
        setMeasuredDimension(width, height)
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        val keyPaletteHeight = (height.toFloat() * notePaletteRatio).roundToInt()
        val messageHeight = (height * 1) / 10

        messageTextView.layout(0, 0, width, messageHeight)
        keyPaletteView.layout(0, height - keyPaletteHeight, width, height)
    }
}
