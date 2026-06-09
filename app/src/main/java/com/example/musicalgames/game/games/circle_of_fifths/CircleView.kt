package com.example.musicalgames.game.games.circle_of_fifths

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.musicalgames.components.palettes.circle_of_fifths_palette.CircleOfFifthsPalette
import com.example.musicalgames.components.palettes.circle_of_fifths_palette.CirclePaletteListener
import kotlinx.coroutines.launch

class CircleView(context: Context, private val viewModel: CircleViewModel, lifecycleOwner: LifecycleOwner) : LinearLayout(context), CirclePaletteListener {
    private val circle = CircleOfFifthsPalette(context, null)
    private val textView = TextView(context)

    init {
        orientation = VERTICAL

        textView.textSize = 40f
        textView.gravity = Gravity.CENTER

        addView(circle, LayoutParams(LayoutParams.MATCH_PARENT, 0, 3f))
        addView(textView, LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f))

        circle.registerListener(this)
        lifecycleOwner.lifecycleScope.launch {
            viewModel.viewState.collect { value -> updateView(value) }
        }
    }

    private fun updateView(state: CircleViewState) {
        circle.setHighlightedIndices(
            if (state.highlightedNote == null) listOf() else listOf(state.highlightedNote)
        )
        textView.text = state.screenCommandMessage ?: state.question ?: ""
    }

    override fun onKeyClicked(index: Int) {
        viewModel.clickCircle(index)
    }

}