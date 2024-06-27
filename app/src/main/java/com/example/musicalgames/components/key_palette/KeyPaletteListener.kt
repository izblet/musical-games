package com.example.musicalgames.components.key_palette

import com.example.musicalgames.utils.ChromaticNote

interface KeyPaletteListener {
    fun onClicked(note : ChromaticNote)
}