package com.example.musicalgames.main_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalgames.IToolbarTitleUpdater
import com.example.musicalgames.R
import com.example.musicalgames.databinding.FragmentNewModeChooseBinding
import com.example.musicalgames.game_activity.GameActivity
import com.example.musicalgames.game_activity.GameIntentMaker
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.CustomGameCreator
import com.example.musicalgames.games.GameFactory
import com.example.musicalgames.games.GameInfo
import com.example.musicalgames.games.GameMap
import com.example.musicalgames.games.GamePackage
import kotlinx.coroutines.launch

class FragmentNewModeChoose : Fragment() {

        private var _binding: FragmentNewModeChooseBinding? = null
        private val binding get() = _binding!!

        private lateinit var viewModel: MainViewModel
        private lateinit var clickedButton: Button
        private lateinit var buttonList: List<Button>
        private lateinit var gameFactory: GameFactory
        private lateinit var adapter: AdapterLevelList
        private lateinit var recyclerView: RecyclerView
        private lateinit var content: FrameLayout
        private lateinit var createView: CustomGameCreator

        private var customList: List<Level> = listOf()
        private var baseList: List<Level> = listOf()
        private var favouriteList: List<Level> = listOf()

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = FragmentNewModeChooseBinding.inflate(inflater, container, false)
            viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
            val gameInfo: GameInfo = GameMap.gameInfos[viewModel.game!!]!!
            gameFactory = gameInfo.gameFactory

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

            content = binding.contentFrame

            adapter = AdapterLevelList(favouriteList, object : AdapterLevelList.OnItemClickListener {
                override fun onItemClick(level: Level) {
                   launchLevel(level)
                }
            })

            recyclerView = RecyclerView(requireContext()).apply {
                layoutManager = LinearLayoutManager(requireContext())
                this.adapter = this@FragmentNewModeChoose.adapter
            }
            content.addView(recyclerView)

            createView = gameFactory.getCustomCreator(requireContext(), null)
            binding.favouritesButton.isSelected = true
            showFavourites()

            return binding.root
        }

        private fun launchLevel(level: Level) {
            val intentMaker: GameIntentMaker = gameFactory.getIntentMaker()
            val intent = intentMaker.getIntent(requireActivity(), level)
            if(intent.getStringExtra(GameActivity.ARG_GAME_TYPE) != null) {
                throw Exception("game type argument is already set in intent")
            }
            intent.putExtra(GameActivity.ARG_GAME_TYPE, viewModel.game!!.name)
            startActivity(intent)
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

            val gameInfo: GameInfo = GameMap.gameInfos[viewModel.game!!]!!
            (requireActivity() as? IToolbarTitleUpdater)?.updateToolbarTitle(gameInfo.name)

        }
        private fun launchGame() {
            //TODO: here we should put the package id into a bundle
            findNavController().navigate(R.id.action_SecondFragment_to_fragmentLevelList2)
        }

        private fun showFavourites() {
            content.removeAllViews()
            content.addView(recyclerView)
            binding.pageTitle.text = "Favourite"
            adapter.setData(favouriteList)
            lifecycleScope.launch {
                favouriteList = gameFactory.getLevels(GamePackage.FAVOURITE, requireContext())
                if(clickedButton==binding.favouritesButton)
                    adapter.setData(favouriteList)
            }
        }
        private fun showLevels() {
            content.removeAllViews()
            content.addView(recyclerView)
            binding.pageTitle.text = "Predefined"
            adapter.setData(baseList)
            lifecycleScope.launch {
                baseList = gameFactory.getLevels(GamePackage.PREDEFINED, requireContext())
                if(clickedButton==binding.levelsButton)
                    adapter.setData(baseList)
            }

        }
        private fun showCustom() {
            content.removeAllViews()
            content.addView(recyclerView)
            binding.pageTitle.text = "Custom"
            adapter.setData(customList)
            lifecycleScope.launch {
                customList = gameFactory.getLevels(GamePackage.CUSTOM, requireContext())
                if(clickedButton==binding.customButton)
                    adapter.setData(customList)
            }
        }
        private fun showCreate() {
            content.removeAllViews()
            content.addView(createView)
            binding.pageTitle.text = "Create"

        }
        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    class AdapterLevelList(private var levelList: List<Level>, private val onItemClickListener: OnItemClickListener) :
        RecyclerView.Adapter<AdapterLevelList.LevelViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game, parent, false)
            return LevelViewHolder(view)
        }

        override fun onBindViewHolder(holder: LevelViewHolder, position: Int) {
            val level = levelList[position]
            holder.bind(level)
            holder.itemView.setOnClickListener {
                onItemClickListener.onItemClick(level)
            }
        }

        fun setData(newList: List<Level>) {
            levelList = newList
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return levelList.size
        }

        inner class LevelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
            private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
            private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
            fun bind(level: Level) {
                nameTextView.text = level.name
                descriptionTextView.text = level.description
            }
        }
        interface OnItemClickListener {
            fun onItemClick(level: Level)
        }
    }
}