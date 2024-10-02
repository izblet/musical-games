package com.example.musicalgames.tmp_archive

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
import com.example.musicalgames.games.GameFactory
import com.example.musicalgames.games.GameInfo
import com.example.musicalgames.games.GameMap
import com.example.musicalgames.games.GamePackage
import com.example.musicalgames.main_app.MainViewModel

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
        val gameFactory: GameFactory = gameInfo.gameFactory

        (requireActivity() as? IToolbarTitleUpdater)?.updateToolbarTitle(gameInfo.name)

        val optionsAdapter = AdapterGameOptions(GamePackage.entries) {
            option->
                launchGame()
        }
        binding.optionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = optionsAdapter
        }
    }
    private fun launchGame() {
        //TODO: here we should put the package id into a bundle
        findNavController().navigate(R.id.action_SecondFragment_to_fragmentLevelList2)
    }

    private fun launchHighScores() {
        findNavController().navigate(R.id.action_SecondFragment_to_fragmentHighScore)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}