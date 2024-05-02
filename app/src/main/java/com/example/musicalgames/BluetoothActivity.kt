package com.example.musicalgames
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
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

class BluetoothActivity : AppCompatActivity() {

    private lateinit var buttonEnableBluetooth: Button
    private lateinit var buttonMakeDiscoverable: Button
    private lateinit var bluetoothAdapter: BluetoothAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)
        // Initialize BluetoothAdapter
        val bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter

        // Initialize UI elements
        buttonEnableBluetooth = findViewById(R.id.button_enable_bluetooth)
        buttonEnableBluetooth.setOnClickListener {
            enableBluetooth()
            discoverDevices()
        }
        buttonMakeDiscoverable=findViewById(R.id.button_make_discoverable)
        buttonMakeDiscoverable.setOnClickListener{
            enableBluetooth()
            makeDiscoverable()
        }
    }

    private fun enableBluetooth() {
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
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        requestMultiplePermissions.launch(permissions)

        if((ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION,)
            != PackageManager.PERMISSION_GRANTED)
            ||(ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION,)
                    != PackageManager.PERMISSION_GRANTED)
            ){
            //return or sth
        }

        // Register BroadcastReceiver for Bluetooth discovery events
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothDevice.ACTION_PAIRING_REQUEST)
        }
        registerReceiver(bluetoothReceiver, filter)

        // Start Bluetooth device discovery
        bluetoothAdapter.startDiscovery()
    }
    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT)
    }

    // BroadcastReceiver for Bluetooth events
    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Handle discovered devices
                    val device: BluetoothDevice? =
                        if(VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)}
                        else {intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)}
                    toast("found a device")
                    // Handle the discovered device

                }
                BluetoothDevice.ACTION_PAIRING_REQUEST -> {
                    // Handle pairing request
                    val device: BluetoothDevice? =
                        if(VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)}
                        else {intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)}

                    val pin: String? = intent.getStringExtra(BluetoothDevice.EXTRA_PAIRING_KEY)
                    // Handle the pairing request
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothReceiver)
    }

    private fun makeDiscoverable() {
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
