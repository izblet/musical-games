package com.example.musicalgames
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalgames.adapters.DeviceAdapter
import java.io.IOException
import java.util.UUID

class GameCreateActivity : AppCompatActivity() {

    private lateinit var buttonMakeDiscoverable: Button
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothSocket: BluetoothSocket?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_create)
        // Initialize BluetoothAdapter
        val bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter


        // Initialize UI elements
        buttonMakeDiscoverable=findViewById(R.id.button_make_discoverable)
        buttonMakeDiscoverable.setOnClickListener{
            enableBluetooth()
            makeDiscoverable()
        }
    }

    //should be a resource
    private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private fun connectToDevice(device: BluetoothDevice) {
        try {
            // Create a BluetoothSocket using the selected device's MAC address
            bluetoothSocket = device.createRfcommSocketToServiceRecord(SPP_UUID)

            // Connect the BluetoothSocket
            bluetoothSocket?.connect()

            // Connection successful, you can now send/receive data
            toast("Connected to ${device.name}")

            // Start listening for incoming data or handle UI accordingly
            // For example, start listening for button clicks to send signals
        } catch (e: IOException) {
            // Connection failed, handle the exception
            toast("Failed to connect: ${e.message}")
        }
        catch(e: SecurityException) {
            toast("some permissions are not there")
        }
    }
    private fun enableBluetooth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestMultiplePermissions.launch(arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))
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

    private val requestMultiplePermissions =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.d("test006", "${it.key} = ${it.value}")
            }
        }
}
