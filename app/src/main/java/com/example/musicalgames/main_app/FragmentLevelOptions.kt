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
        if (viewModel.isCustom == false) {
            binding.editLevelButton.visibility = View.GONE
            binding.editLevelInfoButton.visibility = View.GONE
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
                exitParamsEditMode()
            }

            fun promptExitParamsEditMode() {
                AlertDialog.Builder(requireContext())
                    .setTitle("Unsaved changes")
                    .setMessage("Save or discard your changes to the game parameters?")
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

        if (viewModel.levelId == null) {
            //temporary levels don't have a name/description yet - show a placeholder instead of the editable row
            binding.levelTitleDescContainer.visibility = View.GONE
            binding.editLevelInfoButton.visibility = View.GONE
            binding.temporaryLevelMessage.visibility = View.VISIBLE
        } else {
            binding.levelTitleText.setText(viewModel.levelName)
            binding.levelDescriptionText.setText(viewModel.levelDescription)
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
            exitInfoEditMode()
        }

        fun promptExitInfoEditMode() {
            AlertDialog.Builder(requireContext())
                .setTitle("Unsaved changes")
                .setMessage("Save or discard your changes to the level title and description?")
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