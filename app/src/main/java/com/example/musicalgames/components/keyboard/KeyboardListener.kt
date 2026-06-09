package com.example.musicalgames.components.keyboard

import com.example.musicalgames.music_model.Note

interface KeyboardListener {
    fun onKeyClicked(key: Note)
}