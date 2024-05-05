package com.example.musicalgames.wrappers

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.activity.result.ActivityResultRegistry
import androidx.core.content.ContextCompat.getSystemService
import com.example.musicalgames.R
import java.io.IOException
import java.util.UUID
import kotlin.jvm.Throws

interface BluetoothClientListener {
    fun onDeviceFound(device: BluetoothDevice?)
    fun onDevicePaired()
    fun onDeviceConnected()
    fun onConnectionFailed(e: Exception)
    fun onMessageReceived(message: Int)
}

class BluetoothClientManager(context: Context, activityResultRegistry: ActivityResultRegistry) :
    BluetoothConnectionManager(context, activityResultRegistry), ConnectionSocket {
    private var bluetoothAdapter: BluetoothAdapter
    private var bluetoothSocket: BluetoothSocket?=null
    private var socketListener: ConnectionSocketListener?=null
    private var bluetoothListener: BluetoothClientListener? = null
    private var socketManager = BluetoothSocketManager()

    override fun connected(): Boolean {
        return (bluetoothSocket!=null && bluetoothSocket!!.isConnected)
    }
    fun bluetoothSubscribe(listener: BluetoothClientListener) {
        this.bluetoothListener=listener
    }
    fun socketSubscribe(listener: ConnectionSocketListener) {
        this.socketListener=listener
    }

    init {
        val bluetoothManager = getSystemService(context,BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager!!.adapter
    }

    override fun sendMessage(i: Int) {
        socketManager.sendMessage(bluetoothSocket!!, i)
    }
    private fun disconnect() {
        bluetoothSocket?.close()
    }
    fun destroy() {
        disconnect()
        context!!.unregisterReceiver(bluetoothReceiver)
    }

    @SuppressLint("MissingPermission")
    @Throws
    fun discover() {
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothDevice.ACTION_PAIRING_REQUEST)
        }
        context!!.registerReceiver(bluetoothReceiver, filter)

        // Start Bluetooth device discovery
        var started = bluetoothAdapter.startDiscovery()
        //TODO: if not started request user to manually turn on location
    }

    @SuppressLint("MissingPermission")
    @Throws
    fun pair(device: BluetoothDevice) {
        disconnect()
        if(isDevicePaired(device))
            return
        if (device.createBond()) {
        } else {
        }
    }
    @SuppressLint("MissingPermission")
    @Throws
    fun connectToServer(serverDevice: BluetoothDevice) {
        val connectThread = Thread {
            try {
                //should be a resource
                val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // SPP UUID
                bluetoothSocket = serverDevice.createRfcommSocketToServiceRecord(uuid)
                bluetoothSocket!!.connect()
                bluetoothListener?.onDeviceConnected()
                // Send a signal to the server to keep the connection alive
                bluetoothSocket!!.outputStream.write(R.integer.CONNECTED)

                socketManager.startListening(bluetoothSocket!!) { message ->
                    bluetoothListener?.onMessageReceived(message)
                    socketListener?.onMessage(message)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                bluetoothListener?.onConnectionFailed(e)
            }
        }
        connectThread.start()
    }

    @SuppressLint("MissingPermission")
    @Throws
    private fun isDevicePaired(deviceToCheck: BluetoothDevice): Boolean {
        val pairedDevices = bluetoothAdapter.bondedDevices
        for (device in pairedDevices) {
            if (device.address == deviceToCheck.address) {
                // Device is already paired
                return true
            }
        }
        // Device is not paired
        return false
    }

    // BroadcastReceiver for Bluetooth events
    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra(
                                BluetoothDevice.EXTRA_DEVICE,
                                BluetoothDevice::class.java
                            )
                        } else {
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        }
                    bluetoothListener?.onDeviceFound(device)

                }
            }
        }
    }
}