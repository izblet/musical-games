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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.musicalgames.R
import com.example.musicalgames.databinding.FragmentLevelOptionsBinding
import com.example.musicalgames.game.game_core.GamePlayInstance
import com.example.musicalgames.game.game_core.GameplayOptions
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
            val levelInfoView = factory.getCustomCreatorFromLevel(requireContext(), level, null)
            levelInfoView.setEditable(false)
            binding.levelInfoContainer.addView(levelInfoView)

            fun toggleParamsEditMode() {
                val editable = !levelInfoView.editable
                levelInfoView.setEditable(editable)
                binding.editLevelButton.isActivated = editable
                binding.levelInfoContainer.setBackgroundResource(
                    if (editable) R.drawable.item_selected_bordered else R.drawable.item_bordered
                )
                if (editable) {
                    binding.levelInfoSection.bringToFront()
                    exitEditMode = ::toggleParamsEditMode
                } else {
                    exitEditMode = null
                }
                setOverlayVisible(editable)
            }
            binding.editLevelButton.setOnClickListener { toggleParamsEditMode() }
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

        fun toggleInfoEditMode() {
            val editable = !binding.levelTitleText.isEnabled
            setInfoEditable(editable)
            binding.editLevelInfoButton.isActivated = editable
            binding.headingContainer.setBackgroundResource(
                if (editable) R.drawable.item_selected_bordered else R.drawable.item_bordered
            )
            if (editable) {
                binding.headingContainer.bringToFront()
                exitEditMode = ::toggleInfoEditMode
            } else {
                exitEditMode = null
            }
            setOverlayVisible(editable)
        }
        binding.editLevelInfoButton.setOnClickListener { toggleInfoEditMode() }

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