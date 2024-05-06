package com.example.musicalgames.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.musicalgames.wrappers.BluetoothConnectionManager

class MultiplayerViewModel : ViewModel() {
    var server: Boolean? =null
    var bluetoothManager: BluetoothConnectionManager? = null
}