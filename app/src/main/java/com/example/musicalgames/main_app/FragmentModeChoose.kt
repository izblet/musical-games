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
import com.example.musicalgames.games.sight_sing.ActivitySightSing
import com.example.musicalgames.games.chase.ActivityChase as ChaseActivity

class FragmentModeChoose : Fragment() {

    private var _binding: FragmentModeChooseBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
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
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        val gameInfo: GameInfo = GameMap.gameInfos[viewModel.game!!]!!

        (requireActivity() as? IToolbarTitleUpdater)?.updateToolbarTitle(gameInfo.name)

        val optionsAdapter = AdapterGameOptions(gameInfo.options) {
            option->
                if(option == GameOption.HIGH_SCORES)
                    launchHighScores()
                else if(viewModel.game == Game.FLAPPY) {
                    launchFlappyGame()
                } else if(viewModel.game == Game.PLAY_BY_EAR) {
                    launchFlappyGame()
                }
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
    private fun launchFlappyGame() {
        findNavController().navigate(R.id.action_SecondFragment_to_fragmentLevelList2)
        /*val intent = Intent(activity, FlappyActivity::class.java).apply {
            putExtra(ARCADE_EXTRA, isArcade)
        }
        startActivity(intent)

         */
    }
    private fun launchSightSingActivity() {
        val intent = Intent(activity, ActivitySightSing::class.java)
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