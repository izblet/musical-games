package com.example.musicalgames.components

import com.example.musicalgames.utils.Note

interface KeyboardListener {
    fun onKeyClicked(key: Note)
}