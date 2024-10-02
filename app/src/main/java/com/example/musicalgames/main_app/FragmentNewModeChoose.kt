package com.example.musicalgames.main_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicalgames.IToolbarTitleUpdater
import com.example.musicalgames.R
import com.example.musicalgames.databinding.FragmentNewModeChooseBinding
import com.example.musicalgames.games.GameFactory
import com.example.musicalgames.games.GameInfo
import com.example.musicalgames.games.GameMap

class FragmentNewModeChoose : Fragment() {

        private var _binding: FragmentNewModeChooseBinding? = null
        private val binding get() = _binding!!

        private lateinit var viewModel: MainViewModel
        private lateinit var clickedButton: Button
        private lateinit var buttonList: List<Button>

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = FragmentNewModeChooseBinding.inflate(inflater, container, false)

            binding.favouritesButton.setOnClickListener{
                updateButtons(binding.favouritesButton)
                showFavourites()
            }
            binding.createButton.setOnClickListener{
                updateButtons(binding.createButton)
                showCreate()
            }
            binding.levelsButton.setOnClickListener{
                updateButtons(binding.levelsButton)
                showLevels()
            }
            binding.customButton.setOnClickListener{
                updateButtons(binding.customButton)
                showCustom()
            }
            buttonList = listOf(binding.createButton, binding.customButton, binding.favouritesButton, binding.levelsButton)
            clickedButton = binding.favouritesButton

            binding.favouritesButton.isSelected = true
            showFavourites()

            return binding.root
        }

        private fun updateButtons(newClicked : Button) {
            clickedButton.isSelected = false
            clickedButton = newClicked
            clickedButton.isSelected = true
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
            //data for all four screens should be fetched here

        }
        private fun launchGame() {
            //TODO: here we should put the package id into a bundle
            findNavController().navigate(R.id.action_SecondFragment_to_fragmentLevelList2)
        }

        private fun showFavourites() {
            //findNavController().navigate(R.id.action_SecondFragment_to_fragmentHighScore)
        }
        private fun showLevels() {
            /*
            this code will still be used, just not now
            val optionsAdapter = AdapterGameOptions(gameFactory.getPackages()) {
                    option->
                viewModel.pack = option
                launchGame()
            }
            binding.optionsRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = optionsAdapter
            }*/

        }
        private fun showCustom() {

        }
        private fun showCreate() {

        }
        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

}