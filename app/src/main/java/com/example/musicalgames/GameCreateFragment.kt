package com.example.musicalgames
import android.bluetooth.BluetoothDevice
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.musicalgames.viewmodels.MultiplayerViewModel
import com.example.musicalgames.wrappers.BluetoothEventListener
import com.example.musicalgames.wrappers.BluetoothServerManager


class GameCreateFragment : Fragment(), BluetoothEventListener {

    private lateinit var buttonMakeDiscoverable: Button
    private lateinit var viewModel: MultiplayerViewModel
    private lateinit var bluetoothServerManager: BluetoothServerManager
   override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_game_create, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MultiplayerViewModel::class.java)

        //it has to be correct, but you can never be too cautious
        if(viewModel.bluetoothManager!! is BluetoothServerManager) {
            bluetoothServerManager = viewModel.bluetoothManager as BluetoothServerManager
        }
        else {
            //TODO
        }

        // Initialize UI elements
        buttonMakeDiscoverable = view.findViewById(R.id.button_make_discoverable)

        buttonMakeDiscoverable.setOnClickListener {
            bluetoothServerManager.makeDiscoverable()
        }

        val button = view.findViewById<Button>(R.id.button_toggle)

        // Button click listener
        button.setOnClickListener {

            if(bluetoothServerManager.connected()) {
                bluetoothServerManager.bluetoothUnsubscribe()
                requireActivity().requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                findNavController().navigate(R.id.action_gameCreateFragment_to_pianoChaseGameFragment2)
            }
        }
        bluetoothServerManager.bluetoothSubscribe(this)
        bluetoothServerManager.startServer()
        bluetoothServerManager.enableBluetooth()
    }
    private fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    // Method to start flashing the dot
    private fun startFlashingDot() {
            val dotView = requireView().findViewById<View>(R.id.dotView)
            dotView.visibility =
                if (dotView.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
        }

    override fun onMessageReceived(message: Int) {
        //TODO("Not yet implemented")
    }

    override fun onDevicePaired() {
        //TODO("Not yet implemented")
    }

    override fun onConnected() {
        //TODO("Not yet implemented")
    }

    override fun onDisconnected(exception: Exception) {
        //TODO("Not yet implemented")
    }

    override fun onDeviceFound(device: BluetoothDevice?) {
        //TODO("Not yet implemented")
    }

}
