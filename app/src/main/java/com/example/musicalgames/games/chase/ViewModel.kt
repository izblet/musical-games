package com.example.musicalgames.games.chase

import androidx.lifecycle.ViewModel
import com.example.musicalgames.wrappers.bluetooth.BluetoothConnectionManager

class ViewModel : ViewModel() {
    var server: Boolean? =null
    var bluetoothManager: BluetoothConnectionManager? = null
    var score = 0
    var opponentScore = 0
}