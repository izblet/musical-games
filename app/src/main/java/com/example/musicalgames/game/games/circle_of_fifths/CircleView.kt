package com.example.musicalgames.game.games.circle_of_fifths

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.musicalgames.components.palettes.circle_of_fifths_palette.CircleOfFifthsPalette
import com.example.musicalgames.components.palettes.circle_of_fifths_palette.CirclePaletteListener
import com.example.musicalgames.utils.ChromaticNote
import kotlinx.coroutines.launch

class CircleView(context: Context, private val viewModel: CircleViewModel, lifecycleOwner: LifecycleOwner) : ViewGroup(context), CirclePaletteListener {
    private val circle = CircleOfFifthsPalette(context,null)
    private val textView = TextView(context)

    init{
        circle.registerListener(this)
        lifecycleOwner.lifecycleScope.launch {
            viewModel.viewState.collect{
                value->updateView(value)
            }
        }
    }

    private fun updateView(state: CircleViewState) {
        (circle.parent as? ViewGroup)?.removeView(circle)
        addView(circle)

        if(textView.parent==null)
            addView(textView)

        if(state.highlightedNote==null) {
            circle.setMajorHighlighted(listOf())
        } else {
            circle.setMajorHighlighted(listOf(state.highlightedNote))
        }

        if(state.screenCommandMessage!= null) {

            textView.text = state.screenCommandMessage
        }
        else if(state.question!=null) {
            textView.text = state.question
        }

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        circle.layout(l,t,r,b)
        textView.layout(l,t,r,b)
    }

    override fun onKeyClicked(root: ChromaticNote, major: Boolean) {
        viewModel.clickCircle(root)
    }

}