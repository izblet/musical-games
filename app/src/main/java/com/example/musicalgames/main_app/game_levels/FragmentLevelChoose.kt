package com.example.musicalgames.main_app.game_levels

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalgames.IToolbarTitleUpdater
import com.example.musicalgames.R
import com.example.musicalgames.databinding.FragmentNewModeChooseBinding
import com.example.musicalgames.game.database.GameDatabase
import com.example.musicalgames.game.database.LevelDao
import com.example.musicalgames.game_activity.GameActivity
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.game.game_core.GameFactory
import com.example.musicalgames.game.games.circle_of_fifths.creation.CircleGameFactory
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.GameInfo
import com.example.musicalgames.games.GameMap
import com.example.musicalgames.games.GamePackage
import com.example.musicalgames.main_app.MainViewModel
import kotlinx.coroutines.launch
import java.io.Console

class FragmentLevelChoose : Fragment() {

    private var _binding: FragmentNewModeChooseBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    private lateinit var clickedButton: ImageButton
    private lateinit var buttonList: List<ImageButton>
    private lateinit var gameFactory: GameFactory
    private lateinit var adapter: AdapterLevelList
    private lateinit var recyclerView: RecyclerView
    private lateinit var content: FrameLayout
    private lateinit var viewCreate: ConstraintLayout

    private var customList: List<TaggedLevel> = listOf()
    private var baseList: List<TaggedLevel> = listOf()
    private var favouriteList: List<TaggedLevel> = listOf()
    private lateinit var levelDao: LevelDao

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val db = GameDatabase.getInstance(requireContext())
        levelDao = db.levelDao()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewModeChooseBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        val gameInfo: GameInfo = GameMap.gameInfos[viewModel.game!!]!!
        gameFactory = gameInfo.gameFactoryProvider()

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
            override fun onItemClick(level: TaggedLevel) {
               launchLevel(level.level)
            }

            override fun onBinClick(level: TaggedLevel) {
               lifecycleScope.launch {
                   levelDao.deleteLevel(level.levelId)
                   refresh()
               }
            }

            override fun onFavouriteClick(level: TaggedLevel) {
                lifecycleScope.launch {
                    levelDao.changeFavourite(!level.isFavourite, level.levelId)
                    refresh()
                }
            }
        })

        recyclerView = RecyclerView(requireContext()).apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@FragmentLevelChoose.adapter
        }
        content.addView(recyclerView)

        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )

        viewCreate = LayoutInflater.from(requireContext()).inflate(R.layout.view_create_game,content, false) as ConstraintLayout

        val tableCreate = gameFactory.getCustomCreator(requireContext(), ::launchLevel, null)
        tableCreate.layoutParams = layoutParams //I don't remember what it does, but okay
        viewCreate.findViewById<FrameLayout>(R.id.level_creator_container).addView(tableCreate)
        viewCreate.findViewById<Button>(R.id.playButton).setOnClickListener {
            val level = tableCreate.getLevel()
            if (level != null) {
                launchLevel(level)
            }
        }

        viewCreate.findViewById<Button>(R.id.saveButton).setOnClickListener {
            val level = tableCreate.getLevel()
            if(level != null) {
                val name = viewCreate.findViewById<EditText>(R.id.nameInput).text.toString()
                val description = viewCreate.findViewById<EditText>(R.id.descriptionInput).text.toString()
                val taggedLevel = TaggedLevel(viewModel.game!!, 0, name, description, level, isFavourite = false, isCustom = true)
                lifecycleScope.launch {
                    levelDao.addLevel(taggedLevel, viewModel.game!!)
                }
            }
        }

        binding.favouritesButton.isSelected = true
        showFavourites()

        return binding.root
    }
    private fun refresh() {
        if(clickedButton == binding.favouritesButton) {
            showFavourites()
        } else if(clickedButton == binding.customButton){
            showCustom()
        } else if(clickedButton == binding.levelsButton) {
           showLevels()
        }
    }

    private fun launchLevel(level: Level) {
        Log.d("level choose", "launchlevel")
        viewModel.chooseLevel(level)

    }

    private fun updateButtons(newClicked : ImageButton) {
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

    private fun showFavourites() {
        content.removeAllViews()
        content.addView(recyclerView)
        binding.pageTitle.text = "Favourite"
        adapter.setData(favouriteList)
        lifecycleScope.launch {
            favouriteList = levelDao.getFavourites(viewModel.game!!)
            //this ensures that we don't add data after the user changed their mind
            //should pbbly be made thread-safe, but whatever
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
            baseList = levelDao.getLevels(viewModel.game!!, false)
            //TODO: this is of course temporary, but maybe consider putting the level retrieval back into this function
            //nvm, just realised that all levels have to be in the database for <3 to work
            if(viewModel.game!! ==Game.CIRCLE) {
                baseList = CircleGameFactory().getLevels(GamePackage.PREDEFINED, requireContext())
            }
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
            customList = levelDao.getLevels(viewModel.game!!, true)
            if(clickedButton==binding.customButton)
                adapter.setData(customList)
        }
    }
    private fun showCreate() {
        content.removeAllViews()
        content.addView(viewCreate)
        binding.pageTitle.text = "Create"

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}