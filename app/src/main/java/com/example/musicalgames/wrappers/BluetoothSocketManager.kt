package com.example.musicalgames.wrappers

import android.bluetooth.BluetoothSocket
import java.io.IOException
import kotlin.jvm.Throws

interface SocketListener {

}
class BluetoothSocketManager {
    fun startListening(bluetoothSocket: BluetoothSocket, onMessage: (Int)->Unit) {
        val inputStream = bluetoothSocket.inputStream
        val buffer = ByteArray(1024)
        var bytes: Int
        val thread = Thread {
            try {
                while (true) {
                    // Read from the input stream
                    bytes = inputStream.read(buffer)
                    // Process the received data
                    if (bytes != -1) {
                        // Interpret the received signal (e.g., start flashing the dot)
                        onMessage(bytes)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle the exception
            }
        }
        thread.start()
    }
    @Throws
    fun sendMessage(bluetoothSocket: BluetoothSocket, message: Int) {
        val outputStream = bluetoothSocket.outputStream
        outputStream.write(message)
    }
}