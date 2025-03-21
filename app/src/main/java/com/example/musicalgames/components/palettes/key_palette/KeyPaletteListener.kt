package com.example.musicalgames.components.palettes.key_palette

import com.example.musicalgames.utils.ChromaticNote

interface KeyPaletteListener {
    fun onClicked(note : ChromaticNote)
}