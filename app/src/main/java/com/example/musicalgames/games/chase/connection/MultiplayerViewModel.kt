package com.example.musicalgames.games.chase.connection

import androidx.lifecycle.ViewModel
import com.example.musicalgames.wrappers.bluetooth.BluetoothConnectionManager

class MultiplayerViewModel : ViewModel() {
    var server: Boolean? =null
    var bluetoothManager: BluetoothConnectionManager? = null
}