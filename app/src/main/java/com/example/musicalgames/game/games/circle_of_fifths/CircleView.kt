package com.example.musicalgames.game.games.circle_of_fifths

import android.content.Context
import android.view.ViewGroup
import com.example.musicalgames.components.palettes.circle_of_fifths_palette.CircleOfFifthsPalette

class CircleView(context: Context) : ViewGroup(context) {
    private val circle = CircleOfFifthsPalette(context,null)
    private var _viewModel: CircleViewModel? = null
    private val viewModel get() = _viewModel!!

    init{
        addView(circle)
    }
    fun setViewModel(model: CircleViewModel) {
        _viewModel = model
    }
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        circle.layout(l,t,r,b)
    }

}