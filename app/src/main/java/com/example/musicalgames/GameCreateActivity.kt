package com.example.musicalgames
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.util.UUID

class GameCreateActivity : AppCompatActivity() {

    private lateinit var buttonMakeDiscoverable: Button
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothSocket: BluetoothSocket?=null
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_create)
        // Initialize BluetoothAdapter
        val bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter

        // Initialize UI elements
        buttonMakeDiscoverable=findViewById(R.id.button_make_discoverable)
        buttonMakeDiscoverable.setOnClickListener{
            makeDiscoverable()
        }
        var button = findViewById<Button>(R.id.button)

        // Button click listener
        button.setOnClickListener {
            // Send a signal to the other device to start flashing the dot
            sendSignalToOtherDevice()
        }
        startServer()
        enableBluetooth()
    }
    private fun startServer() {
        val serverThread = Thread {
            try {
                //this should be a resource
                val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // SPP UUID
                val serverSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("BluetoothServer", uuid)
                bluetoothSocket = serverSocket.accept()
                handler.post {
                    toast("Connected to client")
                }
                startListeningForSignals()
                // Perform operations with the connected client
                // For example, start flashing the dot
            } catch (e: IOException) {
                e.printStackTrace()
                handler.post {
                    toast("Server error: ${e.message}")
                }
            } catch (e: SecurityException) {
                handler.post{
                    toast("Server error: ${e.message}")
                }
            }
        }
        serverThread.start()
    }

    private fun enableBluetooth() {
        requestMultiplePermissions.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestMultiplePermissions.launch(arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADMIN))
        }
        else{
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            requestBluetooth.launch(enableBtIntent)
        }
    }
    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun makeDiscoverable() {
        //this will be the server side - when you create a game you let people discover it
        toast("make discoverable")
        //TODO: bluetooth admin permission - currently in enableBluetooth
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 60)
        }
        startActivity(discoverableIntent)
    }


    private var requestBluetooth =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                //granted
            }else{
                //deny
            }
        }

    private val requestMultiplePermissions: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.d("test006", "${it.key} = ${it.value}")
            }
        }


    //this will be in a different class

    // Method to send a signal to the other device
    private fun sendSignalToOtherDevice() {
        val outputStream = bluetoothSocket!!.outputStream
        try {
            // Send a signal to the other device (for example, send a byte indicating the action)
            outputStream.write(1)
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle the exception
        }
    }

    // Method to listen for incoming signals
    private fun startListeningForSignals() {
        val inputStream = bluetoothSocket!!.inputStream
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
                        handler.post {
                            startFlashingDot()
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle the exception
            }
        }
        thread.start()
    }

    // Method to start flashing the dot
    private fun startFlashingDot() {
        // Implement dot flashing logic here (toggle visibility of dot view)
        var dotView = findViewById<View>(R.id.dotView)
        dotView.visibility = if (dotView.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
    }
}
