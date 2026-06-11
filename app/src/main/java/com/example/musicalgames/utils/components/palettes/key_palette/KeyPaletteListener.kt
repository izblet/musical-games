package com.example.musicalgames.utils.components.palettes.key_palette

import com.example.musicalgames.music_model.ChromaticNote

interface KeyPaletteListener {
    fun onClicked(note : ChromaticNote)
}