package com.example.musicalgames.wrappers

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultRegistry
import androidx.core.content.ContextCompat.getSystemService
import java.io.IOException
import java.util.UUID

interface ServerEventListener {
    fun onMessageReceived(message:Int)
    fun onServerStarted()
    fun onServerStartFail(exception: Exception)
    fun onClientConnected()
    fun onClientDisconnected()
}
class BluetoothServerManager (context: Context, activityResultRegistry: ActivityResultRegistry) :
    BluetoothConnectionManager(context, activityResultRegistry), ConnectionSocket {
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothSocket: BluetoothSocket?=null
    private var serverListener: ServerEventListener?=null
    private var socketListener: ConnectionSocketListener?=null
    private val socketManager = BluetoothSocketManager()

    init {
        val bluetoothManager = getSystemService(context, BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager!!.adapter
    }
    fun bluetoothSubscribe(listener: ServerEventListener) {
        this.serverListener=listener
    }
    fun socketSubscribe(listener: ConnectionSocketListener) {
        this.socketListener=listener
    }
    override fun connected(): Boolean {
        return (bluetoothSocket!=null && bluetoothSocket!!.isConnected)
    }
    @SuppressLint("MissingPermission")
    fun startServer() {
        val serverThread = Thread {
            try {
                //this should be a resource
                val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // SPP UUID
                val serverSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("BluetoothServer", uuid)
                bluetoothSocket = serverSocket.accept()
                serverListener?.onClientConnected()
                socketManager.startListening(bluetoothSocket!!) { message ->
                    serverListener?.onMessageReceived(message)
                    socketListener?.onMessage(message)
                }
            } catch (e: Exception) {
               serverListener?.onServerStartFail(e)
            }
        }
        serverThread.start()
    }
    fun makeDiscoverable() {
        //this will be the server side - when you create a game you let people discover it
        //TODO: bluetooth admin permission - currently in enableBluetooth
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 60)
        }
        context.startActivity(discoverableIntent)
    }
    override fun sendMessage(message: Int) {
        socketManager.sendMessage(bluetoothSocket!!, message)
    }
    fun releaseResources() {
        try {
            bluetoothSocket?.close()
        } catch (e: IOException) {
            // Handle the exception, if necessary
        }
    }

}