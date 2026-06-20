package com.example.musicalgames.main_app

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.musicalgames.R
import com.example.musicalgames.databinding.FragmentLevelOptionsBinding
import com.example.musicalgames.game.game_core.GamePlayInstance
import com.example.musicalgames.game.game_core.creation.CustomGameCreator
import com.example.musicalgames.game.game_core.creation.Level
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.GameMap

class FragmentLevelOptions : Fragment() {
    private var _binding: FragmentLevelOptionsBinding? = null
    private val binding get() = _binding!!

    //while a section is being edited, the overlay sits on top of everything except that
    //section (brought to front by that section) and blocks taps on the rest of the screen;
    //tapping the overlay itself exits edit mode for whichever section is currently active
    private var saveEditAction: (()->Unit)? = null
    private var discardEditAction: (()->Unit)? = null

    private fun resetEditActions() {
        saveEditAction = null
        discardEditAction = null
    }

    lateinit var state : LevelOptionsViewModel.UIState

    private var levelInfoView: CustomGameCreator? = null
    private val infoEditTextColors = mutableMapOf<EditText, ColorStateList>()

    //this is so long because of the fact that EditTexts really want to be grayed out when they're disabled
    private fun setInfoEditable(editable: Boolean) {
        for (editText in listOf(binding.levelTitleText, binding.levelDescriptionText)) {
            if (editable) {
                infoEditTextColors[editText]?.let { editText.setTextColor(it) }
            } else {
                val originalColors = infoEditTextColors.getOrPut(editText) { editText.textColors }
                editText.setTextColor(originalColors.getColorForState(intArrayOf(android.R.attr.state_enabled), originalColors.defaultColor))
            }
            editText.isEnabled = editable
        }
    }

    private fun refreshUI(viewModel: MainViewModel) {
        binding.levelTitleText.setText(viewModel.levelName)
        binding.levelDescriptionText.setText(viewModel.levelDescription)

        binding.editLevelButton.visibility = if (state.parametersEditable) View.VISIBLE else View.GONE
        binding.editLevelInfoButton.visibility = if (state.infoEditable) View.VISIBLE else View.GONE

        //For temporary levels we substitute the title with a "temporary level message"
        //and add an option to make the level permanent
        if(state.temporaryTitle) {
            binding.temporaryLevelMessage.isVisible = true
            binding.saveLevelButton.isVisible = true
            binding.levelTitleDescContainer.isVisible = false
        } else {
            binding.temporaryLevelMessage.isVisible = false
            binding.saveLevelButton.isVisible = false
            binding.levelTitleDescContainer.isVisible = true
        }

        //Now we will draw the edition sections
        fun clearEdited() {
            binding.editBlockOverlay.visibility = View.GONE

            binding.editLevelInfoButton.isActivated = false
            binding.editLevelButton.isActivated = false

            //the containers are not clickable
            binding.headingContainer.isClickable = false
            binding.headingContainer.setBackgroundResource(R.drawable.item_bordered)
            binding.levelInfoSection.isClickable = false
            binding.levelInfoContainer.setBackgroundResource(R.drawable.item_bordered)

            levelInfoView?.setEditable(false)
            setInfoEditable(false)
        }

        //before we set the edit, we clear
        clearEdited()

        //Nothing is being edited
        if(state.activeEditSection == LevelOptionsViewModel.EditSection.NONE) {
            return
        }

        //We know something is being edited
        binding.editBlockOverlay.visibility = View.VISIBLE
        binding.editBlockOverlay.bringToFront()

       if(state.activeEditSection == LevelOptionsViewModel.EditSection.PARAMS) {
           binding.editLevelButton.isActivated = true
           binding.levelInfoContainer.setBackgroundResource(R.drawable.item_selected_bordered)
           binding.levelInfoSection.isClickable = true //this is here temporarily, can be moved to initialisation (the "clickable" just so that it is not click-through)
           binding.levelInfoSection.bringToFront()
           levelInfoView?.setEditable(true)
       }
       else {
           binding.editLevelInfoButton.isActivated = true
           binding.headingContainer.setBackgroundResource(R.drawable.item_selected_bordered)
           binding.headingContainer.isClickable = true //tmp
           binding.headingContainer.bringToFront()
           setInfoEditable(true)
       }
    }

    //temporary levels have no name/description yet - show a placeholder and a button to persist them;
    //the placeholder/header visibility itself is handled by refreshUI via state.temporaryTitle
    //move this to xml as the rest
    private fun setupTemporaryLevel(viewModel: MainViewModel) {
        binding.saveLevelButton.setOnClickListener {
            val dialogLayout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
            }
            val nameInput = EditText(context).apply {
                hint = "Enter level name"
                inputType = InputType.TYPE_CLASS_TEXT
            }
            val descriptionInput = EditText(context).apply {
                hint = "Enter description"
                inputType = InputType.TYPE_CLASS_TEXT
            }
            dialogLayout.addView(nameInput)
            dialogLayout.addView(descriptionInput)

            AlertDialog.Builder(requireContext())
                .setTitle("Save Level")
                .setView(dialogLayout)
                .setPositiveButton("Save") { _, _ ->
                    viewModel.saveNewLevel(nameInput.text.toString(), descriptionInput.text.toString()) {
                        //the level is now a saved custom level - swap the placeholder for the normal editable row
                        state = state.copy(temporaryTitle = false, infoEditable = true)
                        refreshUI(viewModel)
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }


    private fun promptSaveChanges() {
        AlertDialog.Builder(requireContext())
            .setTitle("Save changes?")
            .setPositiveButton("Save") { _, _ -> saveEditAction?.invoke() }
            .setNegativeButton("Discard") { _, _ -> discardEditAction?.invoke() }
            .show()
    }

    //handles editing of the game-specific level parameters (the custom creator view)
    private fun setupParamsEditMode(viewModel: MainViewModel, game: Game, level: Level) {
        val factory = GameMap.createFactory(game)
        val initialView = factory.getCustomCreatorFromLevel(requireContext(), level, null)
        initialView.setEditable(false)
        binding.levelInfoContainer.addView(initialView)
        levelInfoView = initialView

        //the last-known-good level for this section - "discard" rebuilds the view from this
        var workingLevel: Level = level

        fun discardParamsEdit() {
            binding.levelInfoContainer.removeAllViews()
            val restoredView = factory.getCustomCreatorFromLevel(requireContext(), workingLevel, null)
            binding.levelInfoContainer.addView(restoredView)
            levelInfoView = restoredView
            state = state.copy(activeEditSection = LevelOptionsViewModel.EditSection.NONE)
            refreshUI(viewModel)
            resetEditActions()
        }

        fun saveParamsEdit() {
            val newLevel = levelInfoView?.getLevel()
            if (newLevel == null) {
                levelInfoView?.highlightMissing()
                return
            }
            workingLevel = newLevel
            viewModel.level = newLevel
            if (viewModel.levelId != null) {
                viewModel.updateLevel()
            }
            state = state.copy(activeEditSection = LevelOptionsViewModel.EditSection.NONE)
            refreshUI(viewModel)
            resetEditActions()
        }

        binding.editLevelButton.setOnClickListener {
            if (state.activeEditSection == LevelOptionsViewModel.EditSection.PARAMS) {
                promptSaveChanges()
            } else {
                state = state.copy(activeEditSection = LevelOptionsViewModel.EditSection.PARAMS)
                refreshUI(viewModel)
                saveEditAction = ::saveParamsEdit
                discardEditAction = ::discardParamsEdit
            }
        }
    }

    //handles editing of the level's name/description shown in the heading
    private fun setupInfoEditMode(viewModel: MainViewModel) {
        fun discardInfoEdit() {
            state = state.copy(activeEditSection = LevelOptionsViewModel.EditSection.NONE)
            refreshUI(viewModel)
            resetEditActions()
        }

        fun saveInfoEdit() {
            viewModel.levelName = binding.levelTitleText.text.toString()
            viewModel.levelDescription = binding.levelDescriptionText.text.toString()
            if (viewModel.levelId != null) {
                viewModel.updateLevel()
            }
            state = state.copy(activeEditSection = LevelOptionsViewModel.EditSection.NONE)
            refreshUI(viewModel)
            resetEditActions()
        }

        binding.editLevelInfoButton.setOnClickListener {
            if (state.activeEditSection == LevelOptionsViewModel.EditSection.INFO) {
                promptSaveChanges()
            } else {
                state = state.copy(activeEditSection = LevelOptionsViewModel.EditSection.INFO)
                refreshUI(viewModel)
                saveEditAction = ::saveInfoEdit
                discardEditAction = ::discardInfoEdit
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLevelOptionsBinding.inflate(inflater,container,false)

        val viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        val game = viewModel.game
        val level = viewModel.level
        state = LevelOptionsViewModel.UIState(
            viewModel.isCustom==true,
            viewModel.isCustom==null||viewModel.isCustom!!,
            viewModel.isCustom==null,
            LevelOptionsViewModel.EditSection.NONE)

        //predefined levels (isCustom == false) can't be edited; temporary levels (isCustom == null) can -
        //refreshUI below already handles the text/visibility for all three cases via `state`
        if (viewModel.isCustom == null) {
            setupTemporaryLevel(viewModel)
        }

        binding.editBlockOverlay.setOnClickListener { promptSaveChanges() }

        if (game != null && level != null) {
            setupParamsEditMode(viewModel, game, level)
        }

        setupInfoEditMode(viewModel)

        refreshUI(viewModel)

        //TODO: temporary solution, for now i just want bpm - think about how to implement it properly
        val linearLayout = LinearLayout(context).apply {
            orientation= LinearLayout.HORIZONTAL
            layoutParams =LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        val editText = EditText(context).apply {
            inputType=InputType.TYPE_CLASS_NUMBER
            filters = arrayOf(InputFilter.LengthFilter(3))
        }
        val textView = TextView(context)
        textView.text = "Set bpm (min: ${GamePlayInstance.getMinBpmValue()}, max: ${GamePlayInstance.getMaxBpmValue()}):"


        linearLayout.addView(textView)
        linearLayout.addView(editText)
        binding.gameplayOptionsContainer.addView(linearLayout)

        binding.startGameButton.setOnClickListener{
            val bpm = editText.text.toString().toIntOrNull()

            if(bpm==null) {
                viewModel.playLevel(GamePlayInstance())
            }

            else {
                try {
                    val gameplay = GamePlayInstance(bpm = bpm)
                    viewModel.playLevel(gameplay)
                } catch(e: Exception) {
                    val toast = Toast.makeText(context, "Could not create a game with this bpm, probably illegal value", Toast.LENGTH_SHORT)
                    toast.show()
                }
            }

        }
        return binding.root
    }

}
