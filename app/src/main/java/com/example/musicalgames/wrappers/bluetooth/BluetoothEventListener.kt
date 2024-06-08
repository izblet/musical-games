package com.example.musicalgames.wrappers.bluetooth

import android.bluetooth.BluetoothDevice

interface BluetoothEventListener {
    fun onMessageReceived(message:Int)
    fun onDevicePaired()
    fun onConnected()
    fun onDisconnected(exception: Exception)
    fun onDeviceFound(device: BluetoothDevice?)
}