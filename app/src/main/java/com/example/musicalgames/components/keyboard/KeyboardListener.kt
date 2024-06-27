package com.example.musicalgames.components.keyboard

import com.example.musicalgames.utils.Note

interface KeyboardListener {
    fun onKeyClicked(key: Note)
}