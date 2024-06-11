package com.example.musicalgames.games.chase.connection
import android.view.ViewGroup
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.musicalgames.R
import com.example.musicalgames.wrappers.bluetooth.BluetoothClientManager
import com.example.musicalgames.wrappers.bluetooth.BluetoothServerManager


class ModeChooseFragment : Fragment() {
    private lateinit var viewModel: MultiplayerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.activity_bluetooth, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=ViewModelProvider(requireActivity()).get(MultiplayerViewModel::class.java)

        val buttonCreate = view.findViewById<Button>(R.id.button_make_discoverable)
        buttonCreate.setOnClickListener {
            launchCreateActivity()
        }

        val buttonJoin = view.findViewById<Button>(R.id.button_search_for_devices)
        buttonJoin.setOnClickListener {
            launchJoinActivity()
        }

    }

    private fun launchCreateActivity() {
        viewModel.server=true
        viewModel.bluetoothManager= BluetoothServerManager(requireActivity(), requireActivity().activityResultRegistry)
        //findNavController().navigate(R.id.action_modeChooseFragment_to_gameCreateFragment)
    }

    private fun launchJoinActivity() {
        viewModel.server=false
        viewModel.bluetoothManager= BluetoothClientManager(requireActivity(), requireActivity().activityResultRegistry)
        //findNavController().navigate(R.id.action_modeChooseFragment_to_gameJoinFragment)
    }
}
