package com.example.musicalgames.main_app

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicalgames.IToolbarTitleUpdater
import com.example.musicalgames.databinding.FragmentSecondBinding
import com.example.musicalgames.games.flappy.GameActivity as FlappyActivity
import com.example.musicalgames.games.chase.GameActivity as ChaseActivity

class FragmentModeChoose : Fragment() {

    // only valid between onCreateView and
    private var _binding: FragmentSecondBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: ViewModel
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
        viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        val game: Game = viewModel.chosenGame!!

        //TODO: there might be an easier way to update the toolbar
        (requireActivity() as? IToolbarTitleUpdater)?.updateToolbarTitle(game.name)

        //TODO quite limited for now, should probably have some actual options there
        val optionsAdapter = AdapterGameOptions(game.options) {
            option->
                if(game.name=="Flappy Bird")
                    launchFlappyGameActivity()
                else launchPianoChaseActivity()
        }
        binding.optionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = optionsAdapter
        }
    }
   private fun launchPianoChaseActivity() {
        val intent = Intent(activity, ChaseActivity::class.java)
        startActivity(intent)
    }
    private fun launchFlappyGameActivity() {
        val intent = Intent(activity, FlappyActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}