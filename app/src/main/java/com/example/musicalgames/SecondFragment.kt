package com.example.musicalgames

import OptionsAdapter
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicalgames.databinding.FragmentSecondBinding
import com.example.musicalgames.models.Game
import com.example.musicalgames.viewmodels.MainViewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onResume() {
        super.onResume()
        var game = viewModel.chosenGame!!
        (requireActivity() as? IToolbarTitleUpdater)?.updateToolbarTitle(game.name)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        val game: Game = viewModel.chosenGame!!

        (requireActivity() as? IToolbarTitleUpdater)?.updateToolbarTitle(game.name)
        val optionsAdapter = OptionsAdapter(game.options) {
            option->
                if(game.name=="Flappy Bird")
                    launchFlappyGameActivity()
                else launchBluetoothActivity()
        }
        binding.optionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = optionsAdapter
        }
    }
    private fun launchBluetoothActivity() {
        val intent = Intent(activity, MultiplayerActivity::class.java)
        startActivity(intent)
    }
    private fun launchPianoChaseActivity() {
        val intent = Intent(activity, PianoChaseGameFragment::class.java)
        startActivity(intent)
    }
    private fun launchFlappyGameActivity() {
        val intent = Intent(activity, FlappyGameActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}