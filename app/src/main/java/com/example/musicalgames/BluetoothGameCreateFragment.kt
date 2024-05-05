package com.example.musicalgames
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.musicalgames.wrappers.BluetoothServerManager
import com.example.musicalgames.wrappers.ServerEventListener


class BluetoothGameCreateFragment : Fragment(), ServerEventListener, ServerSettable {

    private lateinit var buttonMakeDiscoverable: Button
    private var bluetoothServerManager: BluetoothServerManager? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ServerFragmentListener) {
            (context as ServerFragmentListener).onCreated(this)
        } else {
            throw RuntimeException("$context must implement FragmentCommunication")
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_game_create, container, false)

        // Initialize UI elements
        buttonMakeDiscoverable = view.findViewById(R.id.button_make_discoverable)

        buttonMakeDiscoverable.setOnClickListener {
            bluetoothServerManager?.makeDiscoverable()
        }

        val button = view.findViewById<Button>(R.id.button)

        // Button click listener
        button.setOnClickListener {
            // Send a signal to the other device to start flashing the dot
            if (bluetoothServerManager != null && bluetoothServerManager!!.connected())
                bluetoothServerManager?.sendMessage(1)
        }

        return view
    }

    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothServerManager?.releaseResources()
    }

    // Method to start flashing the dot
    private fun startFlashingDot() {
        // Implement dot flashing logic here (toggle visibility of dot view)
        val dotView = requireView().findViewById<View>(R.id.dotView)
        dotView.visibility = if (dotView.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
    }

    override fun onMessageReceived(message: Int) {
        requireActivity().runOnUiThread {
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

    override fun setConnectionManager(manager: BluetoothServerManager) {
        bluetoothServerManager = manager
        bluetoothServerManager!!.bluetoothSubscribe(this)
        bluetoothServerManager!!.startServer()
        bluetoothServerManager!!.enableBluetooth()
    }
}
