package com.example.musicalgames.utils.components.palettes.circle_of_fifths_palette

import android.graphics.Color

class CircleOfFifthsPaletteModel {
    var onChange: (() -> Unit)? = null

    var highlightedIndices: List<Int> = listOf()
        set(value) { field = value; onChange?.invoke() }

    var highlightColor: Int = Color.BLUE
        set(value) { field = value; onChange?.invoke() }

    var centerText: String? = null
        set(value) { field = value; onChange?.invoke() }

    //null means the view falls back to its own theme-derived default colour
    var centerTextColor: Int? = null
        set(value) { field = value; onChange?.invoke() }
}
