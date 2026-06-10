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
        if (game != null && level != null) {
            val factory = GameMap.createFactory(game)
            val levelInfoView = factory.getCustomCreatorFromLevel(requireContext(), level, null)
            levelInfoView.setEditable(false)
            binding.levelInfoContainer.addView(levelInfoView)

            binding.editLevelButton.setOnClickListener {
                levelInfoView.setEditable(!levelInfoView.editable)
                binding.editLevelButton.isActivated = levelInfoView.editable
            }
        }

        binding.levelTitleText.setText(viewModel.levelName)
        binding.levelDescriptionText.setText(viewModel.levelDescription)

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

        binding.editLevelInfoButton.setOnClickListener {
            setInfoEditable(!binding.levelTitleText.isEnabled)
            binding.editLevelInfoButton.isActivated = binding.levelTitleText.isEnabled
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