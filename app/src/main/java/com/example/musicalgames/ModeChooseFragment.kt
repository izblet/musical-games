package com.example.musicalgames
import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController


class ModeChooseFragment : Fragment() {
    private var modeListener: GameTypeListener?=null
    override fun onAttach(context: Context) {
        Toast.makeText(context, "Createdddd", Toast.LENGTH_SHORT).show()
        super.onAttach(context)
        if (context is GameTypeListener) {
            modeListener=context
        } else {
            throw RuntimeException("$context must implement FragmentCommunication")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.activity_bluetooth, container, false)

        val buttonCreate = view.findViewById<Button>(R.id.button_make_discoverable)
        buttonCreate.setOnClickListener {
            launchCreateActivity()
        }

        val buttonJoin = view.findViewById<Button>(R.id.button_search_for_devices)
        buttonJoin.setOnClickListener {
            launchJoinActivity()
        }

        return view
    }

    private fun launchCreateActivity() {
        modeListener!!.modeChosen(true)
        findNavController().navigate(R.id.action_modeChooseFragment_to_gameCreateFragment)
    }

    private fun launchJoinActivity() {
        modeListener!!.modeChosen(false)
        findNavController().navigate(R.id.action_modeChooseFragment_to_gameJoinFragment)
    }
}
