package com.example.musicalgames.game.games.circle_of_fifths

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.musicalgames.components.palettes.circle_of_fifths_palette.CircleOfFifthsPalette
import com.example.musicalgames.components.palettes.circle_of_fifths_palette.CirclePaletteListener
import com.example.musicalgames.utils.ChromaticNote
import kotlinx.coroutines.launch

class CircleView(context: Context, private val viewModel: CircleViewModel, lifecycleOwner: LifecycleOwner) : FrameLayout(context), CirclePaletteListener {
    private val circle = CircleOfFifthsPalette(context,null)
    private val textView = TextView(context)

    init{
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val dynamicSize = screenWidth * 0.04f

        // Since we calculated this based on pixels, we use COMPLEX_UNIT_PX
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, dynamicSize)

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

        if(textView.parent==null) {
            val params = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            ).apply{gravity=Gravity.CENTER}

            addView(textView,params)
        }

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

    override fun onKeyClicked(root: ChromaticNote, major: Boolean) {
        viewModel.clickCircle(root)
    }

}