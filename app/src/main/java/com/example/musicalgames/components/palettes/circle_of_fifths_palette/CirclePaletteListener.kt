package com.example.musicalgames.components.palettes.circle_of_fifths_palette

import com.example.musicalgames.utils.ChromaticNote

interface CirclePaletteListener {
    fun onKeyClicked(root: ChromaticNote, major: Boolean)
}