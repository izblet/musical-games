package com.example.musicalgames.main_app

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.example.musicalgames.game.game_core.GamePlayInstance
import com.example.musicalgames.game.game_core.GameplayOptions
import com.example.musicalgames.game.game_core.InputMethod

/**
 * Shows whichever of a game's supported [GameplayOptions] have a rendered control today, and
 * assembles the edited values into one [GamePlayInstance]. Same role as `CustomGameCreator`
 * (configure, then hand back a value or null if something's invalid), but deliberately a
 * single class rather than one-per-game or one-per-option: every game that supports a given
 * option wants the exact same control for it, there's no per-game/per-option domain variation
 * here to justify subclassing the way CustomGameCreator's level-editing UI needs to.
 */
class GameplayOptionsView(context: Context, options: Set<GameplayOptions>) : LinearLayout(context) {

    private var bpmEditText: EditText? = null
    private var inputMethodButtons: Map<InputMethod, RadioButton> = emptyMap()

    init {
        orientation = VERTICAL

        if (GameplayOptions.BPM in options) {
            val label = TextView(context).apply {
                text = "Set bpm (min: ${GamePlayInstance.getMinBpmValue()}, max: ${GamePlayInstance.getMaxBpmValue()}):"
            }
            bpmEditText = EditText(context).apply {
                inputType = InputType.TYPE_CLASS_NUMBER
                filters = arrayOf(InputFilter.LengthFilter(3))
            }
            val row = LinearLayout(context).apply {
                orientation = HORIZONTAL
                addView(label)
                addView(bpmEditText)
            }
            addView(row)
        }

        if (GameplayOptions.INPUT_METHOD in options) {
            val label = TextView(context).apply { text = "Input method:" }
            val group = RadioGroup(context).apply { orientation = HORIZONTAL }
            val buttons = InputMethod.entries.associateWith { method ->
                RadioButton(context).apply {
                    id = generateViewId()
                    text = method.toString()
                }
            }
            buttons.values.forEach { group.addView(it) }
            group.check(buttons.getValue(GamePlayInstance().inputMethod).id)
            inputMethodButtons = buttons
            addView(label)
            addView(group)
        }
    }

    /** Returns the assembled GamePlayInstance, or null if some control holds an invalid value. */
    fun getGameplay(): GamePlayInstance? {
        var gameplay = GamePlayInstance()

        bpmEditText?.text?.toString()?.toIntOrNull()?.let { bpm ->
            gameplay = try {
                gameplay.copy(bpm = bpm)
            } catch (e: Exception) {
                return null
            }
        }

        inputMethodButtons.entries.find { it.value.isChecked }?.key?.let { selected ->
            gameplay = gameplay.copy(inputMethod = selected)
        }

        return gameplay
    }
}
