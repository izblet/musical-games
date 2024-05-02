package com.example.musicalgames

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import java.io.IOException
import java.io.OutputStream
import java.util.*

class BluetoothFragment : Fragment() {

    private lateinit var flashButton: Button

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager = requireActivity().getSystemService(BluetoothManager::class.java)
        bluetoothManager?.adapter
    }
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bluetooth, container, false)
        flashButton = view.findViewById(R.id.flashButton)
        flashButton.setOnClickListener { sendFlashSignal() }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBluetooth()
    }

    private fun setupBluetooth() {
        if (bluetoothAdapter == null) {
            // Bluetooth is not supported on this device
            // Handle accordingly
        } else {
            if (!bluetoothAdapter!!.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                //activity?.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                // Bluetooth is not enabled, prompt user to enable it
                // You can use the same code as shown in the previous example
            } else {
                // Bluetooth is enabled, proceed with device discovery and connection
                // Implement device discovery and connection logic here
            }
        }
    }

    private fun sendFlashSignal() {
        // Implement logic to send flash signal to the other device
        // For example, write to the output stream of the Bluetooth socket
        // to signal the other device to flash the red dot
    }

    private fun connectToDevice(device: BluetoothDevice) {
        // Implement logic to connect to the selected Bluetooth device
        // For example, create a Bluetooth socket and establish a connection
    }

    private fun disconnect() {
        // Implement logic to disconnect from the Bluetooth device
        // For example, close the Bluetooth socket and release resources
    }

    private fun showError(message: String) {
        // Implement logic to display an error message to the user
    }

    private fun showToast(message: String) {
        // Implement logic to display a toast message to the user
    }

    override fun onDestroy() {
        super.onDestroy()
        disconnect()
    }
}
