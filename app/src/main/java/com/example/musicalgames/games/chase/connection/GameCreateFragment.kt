package com.example.musicalgames.games.chase.connection
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.musicalgames.R
import com.example.musicalgames.wrappers.bluetooth.BluetoothEventListener
import com.example.musicalgames.wrappers.bluetooth.BluetoothServerManager



class GameCreateFragment : Fragment(), BluetoothEventListener {

    private lateinit var buttonMakeDiscoverable: Button
    private lateinit var viewModel: MultiplayerViewModel
    private lateinit var bluetoothServerManager: BluetoothServerManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MultiplayerViewModel::class.java)
        viewModel.server = true
        bluetoothServerManager = BluetoothServerManager(requireActivity(), requireActivity().activityResultRegistry)

        buttonMakeDiscoverable = view.findViewById(R.id.button_make_discoverable)
        buttonMakeDiscoverable.setOnClickListener {
            bluetoothServerManager.makeDiscoverable()
        }

        bluetoothServerManager.bluetoothSubscribe(this)
        Log.e("Bluetooth", "creating the server")
        bluetoothServerManager.enableBluetooth()
        bluetoothServerManager.startServer()
    }

    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    private fun startGame() {
        if (bluetoothServerManager.connected()) {
            bluetoothServerManager.bluetoothUnsubscribe()
            viewModel.bluetoothManager=bluetoothServerManager
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            findNavController().navigate(R.id.action_gameCreateFragment_to_pianoChaseGameFragment2)
        } else {
            Log.e("Bluetooth", "not starting, manager not connected")
        }
    }

    override fun onMessageReceived(message: Int) {
        // Handle received message
    }

    override fun onDevicePaired() {
        // Handle device paired
    }

    override fun onConnected() {
        Log.e("Bluetooth", "Device connected")
        requireActivity().runOnUiThread {
            startGame()
        }
    }

    override fun onDisconnected(exception: Exception) {
        Log.e("Bluetooth", "Disconnected: ${exception.message}")
    }

    override fun onDeviceFound(device: BluetoothDevice?) {
        // Handle device found
    }
}
