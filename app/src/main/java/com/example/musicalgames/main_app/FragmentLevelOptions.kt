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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.musicalgames.R
import com.example.musicalgames.databinding.FragmentLevelOptionsBinding
import com.example.musicalgames.game.game_core.GamePlayInstance
import com.example.musicalgames.game.game_core.GameplayOptions
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.GameMap

class FragmentLevelOptions : Fragment() {
    private var _binding: FragmentLevelOptionsBinding? = null
    private val binding get() = _binding!!

    //predefined levels are read-only: hide both edit pencils, just show their name/description
    private fun setupPredefinedLevel(viewModel: MainViewModel) {
        binding.editLevelButton.visibility = View.GONE
        binding.editLevelInfoButton.visibility = View.GONE
        binding.levelTitleText.setText(viewModel.levelName)
        binding.levelDescriptionText.setText(viewModel.levelDescription)
    }

    //custom levels show their saved name/description and are editable like everything else
    private fun setupCustomLevel(viewModel: MainViewModel) {
        binding.levelTitleText.setText(viewModel.levelName)
        binding.levelDescriptionText.setText(viewModel.levelDescription)
    }

    //temporary levels have no name/description yet - show a placeholder and a button to persist them
    private fun setupTemporaryLevel(viewModel: MainViewModel) {
        binding.levelTitleDescContainer.visibility = View.GONE
        binding.editLevelInfoButton.visibility = View.GONE
        binding.temporaryLevelMessage.visibility = View.VISIBLE
        binding.saveLevelButton.visibility = View.VISIBLE

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
                        binding.temporaryLevelMessage.visibility = View.GONE
                        binding.saveLevelButton.visibility = View.GONE
                        binding.levelTitleDescContainer.visibility = View.VISIBLE
                        binding.editLevelInfoButton.visibility = View.VISIBLE
                        setupCustomLevel(viewModel)
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
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

        //predefined levels (isCustom == false) can't be edited; temporary levels (isCustom == null) can
        when (viewModel.isCustom) {
            false -> setupPredefinedLevel(viewModel)
            true -> setupCustomLevel(viewModel)
            null -> setupTemporaryLevel(viewModel)
        }

        //while a section is being edited, the overlay sits on top of everything except that
        //section (brought to front below) and blocks taps on the rest of the screen; tapping
        //the overlay itself exits edit mode for whichever section is currently active
        var exitEditMode: (() -> Unit)? = null
        fun setOverlayVisible(visible: Boolean) {
            binding.editBlockOverlay.visibility = if (visible) View.VISIBLE else View.GONE
        }
        binding.editBlockOverlay.setOnClickListener {
            exitEditMode?.invoke()
        }

        if (game != null && level != null) {
            val factory = GameMap.createFactory(game)
            var levelInfoView = factory.getCustomCreatorFromLevel(requireContext(), level, null)
            levelInfoView.setEditable(false)
            binding.levelInfoContainer.addView(levelInfoView)

            //the last-known-good level for this section - "discard" rebuilds the view from this
            var workingLevel: Level = level

            fun exitParamsEditMode() {
                levelInfoView.setEditable(false)
                binding.editLevelButton.isActivated = false
                binding.levelInfoContainer.setBackgroundResource(R.drawable.item_bordered)
                binding.levelInfoSection.isClickable = false
                exitEditMode = null
                setOverlayVisible(false)
            }

            fun discardParamsEdit() {
                binding.levelInfoContainer.removeAllViews()
                levelInfoView = factory.getCustomCreatorFromLevel(requireContext(), workingLevel, null)
                binding.levelInfoContainer.addView(levelInfoView)
                exitParamsEditMode()
            }

            fun saveParamsEdit() {
                val newLevel = levelInfoView.getLevel()
                if (newLevel == null) {
                    levelInfoView.highlightMissing()
                    return
                }
                workingLevel = newLevel
                viewModel.level = newLevel
                if (viewModel.levelId != null) {
                    viewModel.updateLevel()
                }
                exitParamsEditMode()
            }

            //TODO: this is the same as for the other editing field, they should be combined
            fun promptExitParamsEditMode() {
                AlertDialog.Builder(requireContext())
                    .setTitle("Save changes?")
                    .setPositiveButton("Save") { _, _ -> saveParamsEdit() }
                    .setNegativeButton("Discard") { _, _ -> discardParamsEdit() }
                    .show()
            }

            fun enterParamsEditMode() {
                levelInfoView.setEditable(true)
                binding.editLevelButton.isActivated = true
                binding.levelInfoContainer.setBackgroundResource(R.drawable.item_selected_bordered)
                binding.levelInfoSection.bringToFront()
                //absorbs taps on labels/padding within the section so they don't fall through
                //to the overlay underneath and get treated as "tap outside"
                binding.levelInfoSection.isClickable = true
                exitEditMode = ::promptExitParamsEditMode
                setOverlayVisible(true)
            }

            binding.editLevelButton.setOnClickListener {
                if (levelInfoView.editable) promptExitParamsEditMode() else enterParamsEditMode()
            }
        }

        //this is so long because of the fact that EditTexts really want to be grayed out when they're disabled
        val infoEditTextColors = mutableMapOf<EditText, ColorStateList>()
        fun setInfoEditable(editable: Boolean) {
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
        setInfoEditable(false)

        fun exitInfoEditMode() {
            setInfoEditable(false)
            binding.editLevelInfoButton.isActivated = false
            binding.headingContainer.setBackgroundResource(R.drawable.item_bordered)
            binding.headingContainer.isClickable = false
            exitEditMode = null
            setOverlayVisible(false)
        }

        fun discardInfoEdit() {
            binding.levelTitleText.setText(viewModel.levelName)
            binding.levelDescriptionText.setText(viewModel.levelDescription)
            exitInfoEditMode()
        }

        fun saveInfoEdit() {
            viewModel.levelName = binding.levelTitleText.text.toString()
            viewModel.levelDescription = binding.levelDescriptionText.text.toString()
            if (viewModel.levelId != null) {
                viewModel.updateLevel()
            }
            exitInfoEditMode()
        }

        fun promptExitInfoEditMode() {
            AlertDialog.Builder(requireContext())
                .setTitle("Save changes?")
                .setPositiveButton("Save") { _, _ -> saveInfoEdit() }
                .setNegativeButton("Discard") { _, _ -> discardInfoEdit() }
                .show()
        }

        fun enterInfoEditMode() {
            setInfoEditable(true)
            binding.editLevelInfoButton.isActivated = true
            binding.headingContainer.setBackgroundResource(R.drawable.item_selected_bordered)
            binding.headingContainer.bringToFront()
            //absorbs taps on padding within the section so they don't fall through to the
            //overlay underneath and get treated as "tap outside"
            binding.headingContainer.isClickable = true
            exitEditMode = ::promptExitInfoEditMode
            setOverlayVisible(true)
        }

        binding.editLevelInfoButton.setOnClickListener {
            if (binding.levelTitleText.isEnabled) promptExitInfoEditMode() else enterInfoEditMode()
        }

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