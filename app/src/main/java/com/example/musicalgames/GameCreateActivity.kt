package com.example.musicalgames
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.musicalgames.wrappers.BluetoothServerManager
import com.example.musicalgames.wrappers.ServerEventListener

class GameCreateActivity : AppCompatActivity(), ServerEventListener {

    private lateinit var buttonMakeDiscoverable: Button
    private lateinit var bluetoothServerManager: BluetoothServerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_create)
        // Initialize BluetoothAdapter
        bluetoothServerManager= BluetoothServerManager(this)
        bluetoothServerManager.subscribe(this)

        // Initialize UI elements
        buttonMakeDiscoverable=findViewById(R.id.button_make_discoverable)
        buttonMakeDiscoverable.setOnClickListener{
            bluetoothServerManager.makeDiscoverable()
        }
        var button = findViewById<Button>(R.id.button)

        // Button click listener
        button.setOnClickListener {
            // Send a signal to the other device to start flashing the dot

            bluetoothServerManager.sendMessage(1)
        }
        bluetoothServerManager.startServer()
        enableBluetooth()
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

    override fun onDestroy() {
        super.onDestroy()
        bluetoothServerManager.releaseResources()
    }


    // Method to start flashing the dot
    private fun startFlashingDot() {
        // Implement dot flashing logic here (toggle visibility of dot view)
        var dotView = findViewById<View>(R.id.dotView)
        dotView.visibility = if (dotView.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
    }

    override fun onMessageReceived(message: Int) {
        runOnUiThread{
            startFlashingDot()
        }
    }

    override fun onServerStarted() {
        //TODO("Not yet implemented")
    }

    override fun onServerStartFail(exception: Exception) {
        //TODO("Not yet implemented")
    }

    override fun onClientConnected() {
        //TODO("Not yet implemented")
    }

    override fun onClientDisconnected() {
        //TODO("Not yet implemented")
    }
}
