package com.example.musicalgames.games.play_by_ear

import androidx.lifecycle.ViewModel
import com.example.musicalgames.utils.Note
import com.example.musicalgames.wrappers.bluetooth.BluetoothConnectionManager

class ViewModel : ViewModel() {
    var minKey: Note? = null
    var maxKey: Note? = null
    var available: List<Note> = listOf()
    var maxInterval: Int = Int.MAX_VALUE
}