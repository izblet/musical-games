package com.example.musicalgames.main_app

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicalgames.IToolbarTitleUpdater
import com.example.musicalgames.R
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
        val gameInfo: GameInfo = GameMap.gameInfos[viewModel.game!!]!!
        (requireActivity() as? IToolbarTitleUpdater)?.updateToolbarTitle(gameInfo.name)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ViewModel::class.java]

        val gameInfo: GameInfo = GameMap.gameInfos[viewModel.game!!]!!

        //TODO: there might be an easier way to update the toolbar
        (requireActivity() as? IToolbarTitleUpdater)?.updateToolbarTitle(gameInfo.name)

        //TODO quite limited for now, should probably have some actual options there
        val optionsAdapter = AdapterGameOptions(gameInfo.options) {
            option->
                if(option == GameOption.HIGH_SCORES)
                    launchHighScores()
                else if(viewModel.game == Game.FLAPPY) {
                    if(option == GameOption.ARCADE)
                        launchFlappyGame(true)
                    else if(option == GameOption.LEVELS)
                        launchFlappyGame(false)
                }
                else if(option == GameOption.GAME_CREATE)
                    launchPianoChaseActivity(true)
                else
                    launchPianoChaseActivity(false)
        }
        binding.optionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = optionsAdapter
        }
    }
   private fun launchPianoChaseActivity(create: Boolean) {
        val intent = Intent(activity, ChaseActivity::class.java). apply {
            putExtra("create", create)
        }
        startActivity(intent)
    }
    private fun launchFlappyGame(isArcade: Boolean) {
        val intent = Intent(activity, FlappyActivity::class.java). apply {
            putExtra("isArcade", isArcade)
        }
        startActivity(intent)
    }

    private fun launchHighScores() {
        findNavController().navigate(R.id.action_SecondFragment_to_fragmentHighScore)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}