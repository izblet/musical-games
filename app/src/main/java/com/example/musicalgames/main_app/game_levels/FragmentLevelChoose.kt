package com.example.musicalgames.main_app.game_levels

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalgames.R
import com.example.musicalgames.databinding.FragmentNewModeChooseBinding
import com.example.musicalgames.game.database.GameDatabase
import com.example.musicalgames.game.database.LevelDao
import com.example.musicalgames.game_activity.GameActivity
import com.example.musicalgames.game.game_core.creation.Level
import com.example.musicalgames.game.game_core.creation.GameFactory
import com.example.musicalgames.games.GameMap
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
    private lateinit var stateTextView: TextView
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
        gameFactory = GameMap.createFactory(viewModel.game!!)

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
            showPredefined()
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
               launchLevel(level.level, level.name, level.description, level.levelId, level.isCustom)
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
        stateTextView = TextView(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            gravity = Gravity.CENTER
            textSize = 18f
            visibility = View.GONE
        }
        content.addView(recyclerView)
        content.addView(stateTextView)

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
                tableCreate.clearSelection()
                viewCreate.findViewById<EditText>(R.id.nameInput).setText("")
                viewCreate.findViewById<EditText>(R.id.descriptionInput).setText("")
                Toast.makeText(context, "Level Created", Toast.LENGTH_SHORT).show()
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
           showPredefined()
        }
    }

    private fun launchLevel(level: Level, name: String? = null, description: String? = null, levelId: Int? = null, isCustom: Boolean? = null) {
        Log.d("level choose", "launchlevel")
        viewModel.chooseLevel(level, name, description, levelId, isCustom)

    }

    private fun updateButtons(newClicked : ImageButton) {
        clickedButton.isSelected = false
        clickedButton = newClicked
        clickedButton.isSelected = true
    }

    private fun showFavourites() {
        showLevelList(
            title = "Favourite",
            cachedList = favouriteList,
            emptyMessage = "Your favourite levels will show up here",
            isStillSelected = { clickedButton == binding.favouritesButton },
            fetch = { levelDao.getFavourites(viewModel.game!!) },
            onResult = { favouriteList = it }
        )
    }
    private fun showPredefined() {
        showLevelList(
            title = "Predefined",
            cachedList = baseList,
            emptyMessage = "No predefined levels",
            isStillSelected = { clickedButton == binding.levelsButton },
            fetch = { levelDao.getLevels(viewModel.game!!, false) },
            onResult = { baseList = it }
        )
    }
    private fun showCustom() {
        showLevelList(
            title = "Custom",
            cachedList = customList,
            emptyMessage = "Your custom levels will show up here",
            isStillSelected = { clickedButton == binding.customButton },
            fetch = { levelDao.getLevels(viewModel.game!!, true) },
            onResult = { customList = it }
        )
    }

    private fun showLevelList(
        title: String,
        cachedList: List<TaggedLevel>,
        emptyMessage: String,
        isStillSelected: () -> Boolean,
        fetch: suspend () -> List<TaggedLevel>,
        onResult: (List<TaggedLevel>) -> Unit
    ) {
        content.removeAllViews()
        content.addView(recyclerView)
        content.addView(stateTextView)
        adapter.setData(cachedList)
        setListState(cachedList, emptyMessage, isLoading = cachedList.isEmpty())
        lifecycleScope.launch {
            val result = fetch()
            onResult(result)
            //this ensures that we don't add data after the user changed their mind
            //should pbbly be made thread-safe, but whatever
            if (isStillSelected()) {
                adapter.setData(result)
                setListState(result, emptyMessage, isLoading = false)
            }
        }
    }

    private fun setListState(list: List<TaggedLevel>, emptyMessage: String, isLoading: Boolean) {
        when {
            isLoading -> {
                stateTextView.text = "Loading..."
                stateTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
            list.isEmpty() -> {
                stateTextView.text = emptyMessage
                stateTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
            else -> {
                stateTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
    }
    private fun showCreate() {
        content.removeAllViews()
        content.addView(viewCreate)

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}