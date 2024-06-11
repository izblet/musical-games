package com.example.musicalgames.wrappers.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultRegistry
import androidx.core.content.ContextCompat.getSystemService
import java.io.IOException
import java.util.UUID


class BluetoothServerManager(context: Context, activityResultRegistry: ActivityResultRegistry) :
    BluetoothConnectionManager(context, activityResultRegistry) {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    @Volatile private var bluetoothSocket: BluetoothSocket? = null
    private var serverListener: BluetoothEventListener? = null
    private val socketManager = BluetoothSocketManager()

    init {
        val bluetoothManager = getSystemService(context, BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager!!.adapter
    }

    override fun bluetoothSubscribe(listener: BluetoothEventListener) {
        this.serverListener = listener
    }

    override fun bluetoothUnsubscribe() {
        this.serverListener = null
    }

    @Synchronized
    override fun connected(): Boolean {
        return (bluetoothSocket != null && bluetoothSocket!!.isConnected)
    }

    @SuppressLint("MissingPermission")
    fun startServer() {
        val serverThread = Thread {
            try {
                val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // SPP UUID
                val serverSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("BluetoothServer", uuid)

                bluetoothSocket = serverSocket.accept()
                synchronized(this) {
                    if(bluetoothSocket!=null) {
                        serverListener?.onConnected()
                        socketManager.startListening(bluetoothSocket!!,
                            { message ->
                                serverListener?.onMessageReceived(message)
                            },
                            { e ->
                                Log.e("Bluetooth", "Disconnected: ${e.message}")
                                serverListener?.onDisconnected(e)
                            })
                    }
                }
            } catch (e: IOException) {
                serverListener?.onDisconnected(e)
            } catch (e: Exception) {
                Log.e("Bluetooth", "Exception occurred: ${e.message}")
                serverListener?.onDisconnected(e)
            }
        }
        serverThread.start()
    }

    fun makeDiscoverable() {
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 60)
        }
        context.startActivity(discoverableIntent)
    }

    @Synchronized
    override fun sendMessage(message: Int) {
        if (bluetoothSocket != null) {
            socketManager.sendMessage(bluetoothSocket!!, message)
        } else {
            Log.e("Bluetooth", "cannot send message, socket is null")
        }
    }

    override fun releaseResources() {
        synchronized(this) {
            try {
                bluetoothSocket?.close()
            } catch (e: IOException) {
                // Handle the exception, if necessary
            } finally {
                bluetoothSocket = null
            }
        }
    }
}
