package com.example.musicalgames
import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalgames.adapters.DeviceAdapter
import com.example.musicalgames.utils.PermissionsUtil
import com.example.musicalgames.wrappers.BluetoothClientManager
import com.example.musicalgames.wrappers.BluetoothClientListener

class GameJoinActivity : AppCompatActivity(), BluetoothClientListener {

    private lateinit var buttonDeviceSearch: Button
    private lateinit var deviceAdapter: DeviceAdapter
    private lateinit var bluetooth: BluetoothClientManager
    private val discoveredDevices = mutableListOf<BluetoothDevice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_join)

        val registry = activityResultRegistry
        bluetooth = BluetoothClientManager(this, registry)
        bluetooth.registerListener(this)
        val recyclerViewDevices = findViewById<RecyclerView>(R.id.recyclerViewDevices)

        deviceAdapter = DeviceAdapter(discoveredDevices) {
                device ->
            run {
                bluetooth.pair(device)//TODO: this throws if no permissions
                bluetooth.connectToServer(device)
            }
        }
        recyclerViewDevices.adapter=deviceAdapter
        recyclerViewDevices.layoutManager = LinearLayoutManager(this)


        // Initialize UI elements
        buttonDeviceSearch = findViewById(R.id.button_search_for_devices)
        buttonDeviceSearch.setOnClickListener {
            discoverDevices()
        }
        var button = findViewById<Button>(R.id.button)

        // Button click listener
        button.setOnClickListener {
            // Send a signal to the other device to start flashing the dot
            if(bluetooth.connected())
                bluetooth.sendMessage(1)
        }

        bluetooth.enableBluetooth()
    }

    //todo: paired devices should be on a separate list

    override fun onDeviceFound(device: BluetoothDevice?) {
        device?.let {
            if(!discoveredDevices.contains(it)) {
                discoveredDevices.add(it)
                deviceAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDevicePaired() {
        //TODO("Not yet implemented")
    }

    override fun onDeviceConnected() {
        //TODO("Not yet implemented")
    }

    override fun onConnectionFailed(e: Exception) {
        //TODO("Not yet implemented")
    }

    override fun onMessageReceived(message: Int) {
        runOnUiThread {
            startFlashingDot()
        }
    }

    private fun discoverDevices() {
        discoveredDevices.clear()

        if(!bluetooth.checkPermissions())
            bluetooth.enableBluetooth()

        // Register BroadcastReceiver for Bluetooth discovery events
        bluetooth.discover()
    }
    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetooth.destroy()
    }


    // Method to start flashing the dot
    private fun startFlashingDot() {
        // Implement dot flashing logic here (toggle visibility of dot view)
        var dotView = findViewById<View>(R.id.dotView)
        dotView.visibility = if (dotView.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
    }
}
