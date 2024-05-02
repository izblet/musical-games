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
import android.os.Handler
import android.os.Looper
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

class GameJoinActivity : AppCompatActivity() {

    private lateinit var buttonDeviceSearch: Button
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var deviceAdapter: DeviceAdapter
    private var bluetoothSocket: BluetoothSocket?=null
    private var handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_join)
        // Initialize BluetoothAdapter
        val bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter

        val recyclerViewDevices = findViewById<RecyclerView>(R.id.recyclerViewDevices)
        deviceAdapter = DeviceAdapter(discoveredDevices) {
                device ->
            run {
                initiatePairing(device)
                connectToServer(device)
            }
        }
        recyclerViewDevices.adapter=deviceAdapter
        recyclerViewDevices.layoutManager = LinearLayoutManager(this)


        // Initialize UI elements
        buttonDeviceSearch = findViewById(R.id.button_search_for_devices)
        buttonDeviceSearch.setOnClickListener {
            enableBluetooth()
            discoverDevices()
        }
    }
    private fun connectToServer(serverDevice: BluetoothDevice) {
        val connectThread = Thread {
            try {
                //should be a resource
                val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // SPP UUID
                val socket: BluetoothSocket = serverDevice.createRfcommSocketToServiceRecord(uuid)
                socket.connect()
                handler.post {
                    toast("Connected to server")
                }
                // Send a signal to the server to keep the connection alive
                socket.outputStream.write(0)
            } catch (e: IOException) {
                e.printStackTrace()
                handler.post {
                    toast("Failed to connect to server: ${e.message}")
                }
            } catch (e: SecurityException) {
                toast("Failed to connect to server: ${e.message}")
            }
        }
        connectThread.start()
    }
    //todo: paired devices should be on a separate list
    private fun isDevicePaired(deviceToCheck: BluetoothDevice): Boolean {
        try {
            val pairedDevices = bluetoothAdapter.bondedDevices
            for (device in pairedDevices) {
                if (device.address == deviceToCheck.address) {
                    // Device is already paired
                    return true
                }
            }
        } catch(e: SecurityException) {}
        // Device is not paired
        return false
    }

    private fun initiatePairing(device: BluetoothDevice) {
        try {
            disconnect()
            if(isDevicePaired(device))
                return
            if (device.createBond()) {
                toast("connected")
            } else {
                toast("could not connect")
            }
        } catch(e: SecurityException) {
            toast("security exception on connect")
        }

    }
    private fun disconnect() {
        try {
            bluetoothSocket?.close()
        } catch(e: IOException) {
            toast("could not disconnect")
        }
    }

    private fun enableBluetooth() {
        requestMultiplePermissions.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestMultiplePermissions.launch(arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADMIN))
        }
        else{
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            requestBluetooth.launch(enableBtIntent)
        }
    }
    private fun discoverDevices() {

        discoveredDevices.clear()
        if((ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION,)
                    != PackageManager.PERMISSION_GRANTED)
            ||(ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION,)
                    != PackageManager.PERMISSION_GRANTED)
        ){
            enableBluetooth()
            toast("no permissions for location")
            //return or sth
        }
        else toast("location permitted")

        // Register BroadcastReceiver for Bluetooth discovery events
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothDevice.ACTION_PAIRING_REQUEST)
        }
        registerReceiver(bluetoothReceiver, filter)

        toast("starting discovery")
        // Start Bluetooth device discovery
        bluetoothAdapter.startDiscovery()
    }
    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
    private val discoveredDevices = mutableListOf<BluetoothDevice>()
    // BroadcastReceiver for Bluetooth events
    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    toast("found a device")
                    // Handle discovered devices
                    val device: BluetoothDevice? =
                        if(VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)}
                        else {intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)}

                    device?.let {
                        if(!discoveredDevices.contains(it)) {
                            discoveredDevices.add(it)
                            deviceAdapter.notifyDataSetChanged()
                        }
                    }

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothReceiver)
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
