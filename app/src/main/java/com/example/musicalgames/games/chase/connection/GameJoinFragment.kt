package com.example.musicalgames.games.chase.connection
import android.bluetooth.BluetoothDevice
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalgames.R
import com.example.musicalgames.games.chase.ViewModel
import com.example.musicalgames.wrappers.bluetooth.BluetoothClientManager
import com.example.musicalgames.wrappers.bluetooth.BluetoothEventListener


class GameJoinFragment : Fragment(), BluetoothEventListener {
    private lateinit var viewModel: ViewModel
    private lateinit var buttonDeviceSearch: Button
    private lateinit var deviceAdapter: DeviceAdapter
    private lateinit var bluetooth: BluetoothClientManager
    private val discoveredDevices = mutableListOf<BluetoothDevice>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game_join, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ViewModel::class.java]

        bluetooth = BluetoothClientManager(requireActivity(), requireActivity().activityResultRegistry)
        bluetooth.bluetoothSubscribe(this)
        bluetooth.enableBluetooth()

        val recyclerViewDevices = view.findViewById<RecyclerView>(R.id.recyclerViewDevices)

        deviceAdapter = DeviceAdapter(discoveredDevices) { device ->
            if(!bluetooth.connected()) {
                toast("connecting...")
                //the following function does not attempt to pair if devices are already paired
                bluetooth.pair(device)
                bluetooth.connectToServer(device)
            }
        }
        recyclerViewDevices.adapter = deviceAdapter
        recyclerViewDevices.layoutManager = LinearLayoutManager(requireContext())

        buttonDeviceSearch = view.findViewById(R.id.button_search_for_devices)
        buttonDeviceSearch.setOnClickListener {
            toast("starting discovery...")
            discoverDevices()
        }

    }

    private fun discoverDevices() {
        discoveredDevices.clear()

        if (!bluetooth.checkPermissions())
            bluetooth.enableBluetooth()

        bluetooth.discover()
    }
    private fun startGame() {
        if(bluetooth.connected())
        {
            bluetooth.bluetoothUnsubscribe()
            viewModel.server=false
            viewModel.bluetoothManager = bluetooth
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            findNavController().navigate(R.id.action_gameJoinFragment_to_gameFragment2)
        }
    }

    override fun onDeviceFound(device: BluetoothDevice?) {
        device?.let {
            if (!discoveredDevices.contains(it)) {
                discoveredDevices.add(it)
                deviceAdapter.notifyDataSetChanged()
            }
        }
    }
    override fun onConnected() {
        requireActivity().runOnUiThread {
            startGame()
        }
    }

    override fun onDisconnected(exception: Exception) {
        requireActivity().runOnUiThread {
            toast("could not connect, try again")
        }
        Log.e("Bluetooth", "could not connect $exception")
    }


    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    override fun onDevicePaired() {
        //TODO: Implement
    }

    override fun onMessageReceived(message: Int) {
        //TODO: could be used in the future
    }

}

