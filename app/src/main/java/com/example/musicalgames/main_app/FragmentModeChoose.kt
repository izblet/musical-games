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
import com.example.musicalgames.databinding.FragmentModeChooseBinding
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.GameInfo
import com.example.musicalgames.games.GameMap
import com.example.musicalgames.games.GameOption
import com.example.musicalgames.games.flappy.ActivityFlappy as FlappyActivity
import com.example.musicalgames.games.chase.ActivityChase as ChaseActivity

class FragmentModeChoose : Fragment() {

    private var _binding: FragmentModeChooseBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: ViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentModeChooseBinding.inflate(inflater, container, false)
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

        (requireActivity() as? IToolbarTitleUpdater)?.updateToolbarTitle(gameInfo.name)

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
        val intent = Intent(activity, ChaseActivity::class.java).apply {
            putExtra(ChaseActivity.SERVER_EXTRA, create)
        }
        startActivity(intent)
    }
    private fun launchFlappyGame(isArcade: Boolean) {
        val intent = Intent(activity, FlappyActivity::class.java).apply {
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