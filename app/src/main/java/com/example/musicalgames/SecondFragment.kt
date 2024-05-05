package com.example.musicalgames

import OptionsAdapter
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicalgames.databinding.FragmentSecondBinding
import com.example.musicalgames.models.Game

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val game: Game = arguments?.getParcelable("game") ?: Game("","",0,emptyList())

        val navController = findNavController()
        val destination = navController.currentDestination

        (requireActivity() as? IToolbarTitleUpdater)?.updateToolbarTitle(game.name)
        val optionsAdapter = OptionsAdapter(game.options) {
            option->
                if(game.name=="Flappy Bird")
                    launchFlappyGameActivity()//Toast.makeText(requireContext(), "Clicked on $option", Toast.LENGTH_SHORT).show()
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