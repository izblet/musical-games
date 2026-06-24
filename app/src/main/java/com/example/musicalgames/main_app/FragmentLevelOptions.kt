package com.example.musicalgames.main_app

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.musicalgames.R
import com.example.musicalgames.databinding.FragmentLevelOptionsBinding
import com.example.musicalgames.game.game_core.creation.CustomGameCreator
import com.example.musicalgames.game.game_core.creation.Level
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.GameMap

class FragmentLevelOptions : Fragment() {
    private var _binding: FragmentLevelOptionsBinding? = null
    private val binding get() = _binding!!

    private var saveEditAction: (()->Unit)? = null
    private var discardEditAction: (()->Unit)? = null

    private fun resetEditActions() {
        saveEditAction = null
        discardEditAction = null
    }

    private lateinit var controller: LevelOptionsController
    private lateinit var optionsView: GameplayOptionsView

    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.values.all { it }) {
            attemptStartGame()
        }
    }

    private fun attemptStartGame() {
        val gameplay = optionsView.getGameplay()
        if (gameplay == null) {
            Toast.makeText(context, "Could not create a game with these options, probably an illegal value", Toast.LENGTH_SHORT).show()
            return
        }
        if (controller.hasRequiredPermissions(requireContext(), gameplay)) {
            controller.startGame(gameplay)
        } else {
            requestMultiplePermissions.launch(controller.requiredPermissions(gameplay))
        }
    }

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

    private fun refreshUI() {
        binding.levelTitleText.setText(controller.levelName)
        binding.levelDescriptionText.setText(controller.levelDescription)

        binding.editLevelButton.visibility = if (controller.parametersEditable) View.VISIBLE else View.GONE
        binding.editLevelInfoButton.visibility = if (controller.infoEditable) View.VISIBLE else View.GONE

        //For temporary levels we substitute the title with a "temporary level message"
        //and add an option to make the level permanent
        if(controller.temporaryTitle) {
            binding.temporaryLevelMessage.isVisible = true
            binding.saveLevelButton.isVisible = true
            binding.levelTitleDescContainer.isVisible = false
        } else {
            binding.temporaryLevelMessage.isVisible = false
            binding.saveLevelButton.isVisible = false
            binding.levelTitleDescContainer.isVisible = true
        }

        //Now we will draw the editing sections
        fun clearEdited() {
            //while a section is being edited, the overlay sits on top of everything except that
            //section (brought to front by that section) and blocks taps on the rest of the screen;
            //tapping the overlay itself exits edit mode for whichever section is currently active
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
        if(controller.activeEditSection == LevelOptionsController.EditSection.NONE) {
            return
        }

        //We know something is being edited
        binding.editBlockOverlay.visibility = View.VISIBLE
        binding.editBlockOverlay.bringToFront()

       if(controller.activeEditSection == LevelOptionsController.EditSection.PARAMS) {
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
    private fun setupTemporaryLevel() {
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
                    controller.saveNewLevel(nameInput.text.toString(), descriptionInput.text.toString()) {
                        //the level is now a saved custom level - swap the placeholder for the normal editable row;
                        //temporaryTitle/infoEditable are derived from isCustom, which saveNewLevel already updated
                        refreshUI()
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
    private fun setupParamsEditMode(game: Game, level: Level) {
        val factory = GameMap.createFactory(game)
        val initialView = factory.getCustomCreatorFromLevel(requireContext(), level, null)
        initialView.setEditable(false)
        binding.levelInfoContainer.addView(initialView)
        levelInfoView = initialView

        fun discardParamsEdit() {
            binding.levelInfoContainer.removeAllViews()
            val restoredLevel = controller.discardParamsEdit() ?: level
            val restoredView = factory.getCustomCreatorFromLevel(requireContext(), restoredLevel, null)
            binding.levelInfoContainer.addView(restoredView)
            levelInfoView = restoredView
            refreshUI()
            resetEditActions()
        }

        fun saveParamsEdit() {
            val newLevel = levelInfoView?.getLevel()
            if (newLevel == null) {
                levelInfoView?.highlightMissing()
                return
            }
            controller.saveParamsEdit(newLevel)
            refreshUI()
            rebuildOptionsView()
            resetEditActions()
        }

        binding.editLevelButton.setOnClickListener {
            if (controller.activeEditSection == LevelOptionsController.EditSection.PARAMS) {
                promptSaveChanges()
            } else {
                controller.beginEditingParams()
                refreshUI()
                saveEditAction = ::saveParamsEdit
                discardEditAction = ::discardParamsEdit
            }
        }
    }

    //handles editing of the level's name/description shown in the heading
    private fun setupInfoEditMode() {
        fun discardInfoEdit() {
            controller.cancelInfoEdit()
            refreshUI()
            resetEditActions()
        }

        fun saveInfoEdit() {
            controller.saveInfoEdit(binding.levelTitleText.text.toString(), binding.levelDescriptionText.text.toString())
            refreshUI()
            resetEditActions()
        }

        binding.editLevelInfoButton.setOnClickListener {
            if (controller.activeEditSection == LevelOptionsController.EditSection.INFO) {
                promptSaveChanges()
            } else {
                controller.beginEditingInfo()
                refreshUI()
                saveEditAction = ::saveInfoEdit
                discardEditAction = ::discardInfoEdit
            }
        }
    }

    //rebuilds the input-method/bpm controls from the current level - the available options can
    //change after editing a custom level's params (e.g. flipping a question direction), so this
    //is called both on initial creation and after a params edit is saved
    private fun rebuildOptionsView() {
        binding.gameplayOptionsContainer.removeAllViews()
        val options = controller.game?.let { GameMap.gameplayOptions[it] } ?: emptySet()
        optionsView = GameplayOptionsView(requireContext(), options, controller.level)
        binding.gameplayOptionsContainer.addView(optionsView)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLevelOptionsBinding.inflate(inflater,container,false)

        val viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        controller = LevelOptionsController(viewModel)
        val game = controller.game
        val level = controller.level

        //predefined levels (isCustom == false) can't be edited; temporary levels (isCustom == null) can -
        //refreshUI below already handles the text/visibility for all three cases via `controller`
        if (controller.isCustom == null) {
            setupTemporaryLevel()
        }

        binding.editBlockOverlay.setOnClickListener { promptSaveChanges() }

        if (game != null && level != null) {
            setupParamsEditMode(game, level)
        }

        setupInfoEditMode()

        refreshUI()

        rebuildOptionsView()

        binding.startGameButton.setOnClickListener {
            attemptStartGame()
        }
        return binding.root
    }

}
