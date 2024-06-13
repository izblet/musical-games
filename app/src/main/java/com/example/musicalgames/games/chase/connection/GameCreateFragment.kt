package com.example.musicalgames.games.chase.connection
import android.bluetooth.BluetoothDevice
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.musicalgames.R
import com.example.musicalgames.games.chase.ViewModel
import com.example.musicalgames.wrappers.bluetooth.BluetoothEventListener
import com.example.musicalgames.wrappers.bluetooth.BluetoothServerManager



class GameCreateFragment : Fragment(), BluetoothEventListener {

    private lateinit var buttonMakeDiscoverable: Button
    private lateinit var viewModel: ViewModel
    private lateinit var bluetoothServerManager: BluetoothServerManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        bluetoothServerManager = BluetoothServerManager(requireActivity(), requireActivity().activityResultRegistry)

        buttonMakeDiscoverable = view.findViewById(R.id.button_make_discoverable)
        buttonMakeDiscoverable.setOnClickListener {
            bluetoothServerManager.makeDiscoverable()
        }

        bluetoothServerManager.bluetoothSubscribe(this)
        bluetoothServerManager.enableBluetooth()
        bluetoothServerManager.startServer()
    }

    private fun startGame() {
        if (bluetoothServerManager.connected()) {
            bluetoothServerManager.bluetoothUnsubscribe()
            viewModel.server = true
            viewModel.bluetoothManager=bluetoothServerManager
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            requireActivity().runOnUiThread {
                findNavController().navigate(R.id.action_gameCreateFragment_to_gameFragment2)
            }
        } else {
            Log.e("Bluetooth", "not starting, manager not connected")
        }
    }

    override fun onDisconnected(exception: Exception) {
        Log.e("Bluetooth", "Disconnected: ${exception.message}")
    }

    override fun onMessageReceived(message: Int) {
        // Handle received message
    }

    override fun onDevicePaired() {
        // Handle device paired
    }

    override fun onConnected() {
        requireActivity().runOnUiThread {
            startGame()
        }
    }


    override fun onDeviceFound(device: BluetoothDevice?) {
        // Handle device found
    }
}
