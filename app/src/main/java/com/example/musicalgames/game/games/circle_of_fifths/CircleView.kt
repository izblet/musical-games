package com.example.musicalgames.game.games.circle_of_fifths

import android.content.Context
import android.view.ViewGroup
import com.example.musicalgames.components.palettes.circle_of_fifths_palette.CircleOfFifthsPalette

class CircleView(context: Context, private val viewModel: CircleViewModel) : ViewGroup(context) {
    private val circle = CircleOfFifthsPalette(context,null)

    init{
        addView(circle)
    }
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        circle.layout(l,t,r,b)
    }

}