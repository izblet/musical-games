package com.example.musicalgames.utils.components.keyboard

import com.example.musicalgames.music_model.Note

interface KeyboardListener {
    fun onKeyClicked(key: Note)
}