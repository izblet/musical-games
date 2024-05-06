package com.example.musicalgames
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalgames.adapters.DeviceAdapter
import com.example.musicalgames.wrappers.BluetoothClientManager
import com.example.musicalgames.wrappers.BluetoothClientListener


class BluetoothGameJoinFragment : Fragment(), BluetoothClientListener, ClientSettable {

    private lateinit var buttonDeviceSearch: Button
    private lateinit var deviceAdapter: DeviceAdapter
    private var bluetooth: BluetoothClientManager? = null
    private val discoveredDevices = mutableListOf<BluetoothDevice>()
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ClientFragmentListener) {
            (context as ClientFragmentListener).onCreated(this)
        } else {
            throw RuntimeException("$context must implement FragmentCommunication")
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_game_join, container, false)

        val recyclerViewDevices = view.findViewById<RecyclerView>(R.id.recyclerViewDevices)

        deviceAdapter = DeviceAdapter(discoveredDevices) { device ->
            bluetooth?.pair(device)
            bluetooth?.connectToServer(device)
        }
        recyclerViewDevices.adapter = deviceAdapter
        recyclerViewDevices.layoutManager = LinearLayoutManager(requireContext())

        // Initialize UI elements
        buttonDeviceSearch = view.findViewById(R.id.button_search_for_devices)
        buttonDeviceSearch.setOnClickListener {
            if (bluetooth != null)
                discoverDevices()
        }

        val button = view.findViewById<Button>(R.id.button)

        // Button click listener
        button.setOnClickListener {
            // Send a signal to the other device to start flashing the dot
            if (bluetooth != null && bluetooth!!.connected())
                bluetooth?.sendMessage(1)
        }

        return view
    }

    override fun onDeviceFound(device: BluetoothDevice?) {
        device?.let {
            if (!discoveredDevices.contains(it)) {
                discoveredDevices.add(it)
                deviceAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDevicePaired() {
        //TODO: Implement if needed
    }

    override fun onDeviceConnected() {
        //TODO: Implement if needed
    }

    override fun onConnectionFailed(e: Exception) {
        //TODO: Implement if needed
    }

    override fun onMessageReceived(message: Int) {
        requireActivity().runOnUiThread {
            startFlashingDot()
        }
    }

    private fun discoverDevices() {
        discoveredDevices.clear()

        if (!bluetooth!!.checkPermissions())
            bluetooth!!.enableBluetooth()

        // Register BroadcastReceiver for Bluetooth discovery events
        bluetooth!!.discover()
    }

    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetooth?.destroy()
    }

    private fun startFlashingDot() {
        val dotView = requireView().findViewById<View>(R.id.dotView)
        dotView.visibility = if (dotView.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
    }

    override fun setConnectionManager(manager: BluetoothClientManager) {
        bluetooth = manager
        bluetooth!!.bluetoothSubscribe(this)
        bluetooth!!.enableBluetooth()
    }
}
