package com.example.musicalgames.main_app

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.musicalgames.R
import com.example.musicalgames.game.game_core.GamePlayInstance
import com.example.musicalgames.game.game_core.GameplayOptions
import com.example.musicalgames.game.game_core.InputMethod
import com.example.musicalgames.game.game_core.creation.Level

/**
 * Shows whichever of a game's supported [GameplayOptions] have a rendered control today, and
 * assembles the edited values into one [GamePlayInstance]. Same role as `CustomGameCreator`
 * (configure, then hand back a value or null if something's invalid), but deliberately a
 * single class rather than one-per-game or one-per-option: every game that supports a given
 * option wants the exact same control for it, there's no per-game/per-option domain variation
 * here to justify subclassing the way CustomGameCreator's level-editing UI needs to.
 *
 * The controls sit behind a collapsible "Gameplay" summary strip, collapsed by default; this
 * only affects presentation, not what [getGameplay] returns.
 */
class GameplayOptionsView(
    context: Context,
    options: Set<GameplayOptions>,
    level: Level?,
    initial: GamePlayInstance = GamePlayInstance()
) : LinearLayout(context) {

    private var bpmEditText: EditText? = null
    private var inputMethodButtons: Map<InputMethod, RadioButton> = emptyMap()

    private var isExpanded = false
    private var contentContainer: LinearLayout? = null
    private var summaryTextView: TextView? = null
    private var chevronView: ImageView? = null

    init {
        orientation = VERTICAL
        setBackgroundColor(themeColor(R.attr.surfaceChipColor))

        val content = LinearLayout(context).apply {
            orientation = VERTICAL
            setPadding(dp(12), 0, dp(12), 0)
        }

        val rows = mutableListOf<LinearLayout>()
        if (GameplayOptions.BPM in options) {
            rows.add(buildBpmRow(initial))
        }
        if (GameplayOptions.INPUT_METHOD in options && (level == null || level.supportsMicrophoneInput())) {
            rows.add(buildInputMethodRow(initial))
        }
        rows.forEachIndexed { index, row ->
            if (index > 0) content.addView(buildRowDivider())
            content.addView(row)
        }

        if (content.childCount > 0) {
            contentContainer = content
            content.visibility = View.GONE
            addView(buildHeaderRow())
            addView(content)
            updateSummary()
        }
    }

    private fun dp(value: Int): Int =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics).toInt()

    private fun themeColor(attr: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    private fun buildHeaderRow(): LinearLayout {
        val label = TextView(context).apply {
            text = "Gameplay"
            setTextColor(themeColor(R.attr.tertiaryTextColor))
            textSize = 10.5f
            isAllCaps = true
            setTypeface(typeface, Typeface.BOLD)
        }
        summaryTextView = TextView(context).apply {
            setTextColor(themeColor(R.attr.secondaryTextColor))
            textSize = 12.5f
            ellipsize = TextUtils.TruncateAt.END
            maxLines = 1
            setPadding(dp(10), 0, dp(10), 0)
        }
        chevronView = ImageView(context).apply {
            setImageResource(R.drawable.ic_chevron_down)
        }
        val spacer = View(context)
        return LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(11), dp(12), dp(11))
            isClickable = true
            isFocusable = true
            setBackgroundResource(R.drawable.underline_input)
            addView(label)
            addView(spacer, LinearLayout.LayoutParams(0, 0, 1f))
            addView(summaryTextView)
            addView(chevronView, LinearLayout.LayoutParams(dp(20), dp(20)))
            setOnClickListener { toggleExpanded() }
        }
    }

    private fun toggleExpanded() {
        isExpanded = !isExpanded
        contentContainer?.visibility = if (isExpanded) View.VISIBLE else View.GONE
        summaryTextView?.visibility = if (isExpanded) View.GONE else View.VISIBLE
        chevronView?.rotation = if (isExpanded) 180f else 0f
    }

    private fun buildRowDivider(): View = View(context).apply {
        setBackgroundColor(themeColor(R.attr.hairlineColor))
    }.also {
        it.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(1))
    }

    private fun updateSummary() {
        val parts = mutableListOf<String>()
        bpmEditText?.text?.toString()?.let { parts.add("$it bpm") }
        inputMethodButtons.entries.find { it.value.isChecked }?.key?.let {
            parts.add("External instrument: " + if (it == InputMethod.EXTERNAL_INSTRUMENT) "ON" else "OFF")
        }
        summaryTextView?.text = parts.joinToString(" · ")
    }

    private fun buildBpmRow(initial: GamePlayInstance): LinearLayout {
        val label = TextView(context).apply {
            text = "Set bpm (min: ${GamePlayInstance.getMinBpmValue()}, max: ${GamePlayInstance.getMaxBpmValue()}):"
            setTextColor(themeColor(R.attr.secondaryTextColor))
        }
        bpmEditText = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            filters = arrayOf(InputFilter.LengthFilter(3))
            setText(initial.bpm.toString())
            setBackgroundResource(R.drawable.underline_input)
            setTextColor(ContextCompat.getColor(context, R.color.text_primary))
            setTypeface(typeface, Typeface.BOLD)
            gravity = Gravity.END
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) = updateSummary()
            })
        }
        return LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(13), 0, dp(13))
            addView(label, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
            addView(bpmEditText, LinearLayout.LayoutParams(dp(56), LinearLayout.LayoutParams.WRAP_CONTENT))
        }
    }

    private fun buildInputMethodRow(initial: GamePlayInstance): LinearLayout {
        val label = TextView(context).apply {
            text = "External instrument:"
            setTextColor(themeColor(R.attr.secondaryTextColor))
        }
        val group = RadioGroup(context).apply {
            orientation = HORIZONTAL
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.TRANSPARENT)
                setStroke(dp(1), themeColor(R.attr.hairlineColor))
            }
            setOnCheckedChangeListener { _, _ -> updateSummary() }
        }
        val buttons = InputMethod.entries.associateWith { method ->
            RadioButton(context).apply {
                id = generateViewId()
                text = if (method == InputMethod.EXTERNAL_INSTRUMENT) "ON" else "OFF"
                buttonDrawable = null
                textSize = 11.5f
                setPadding(dp(13), dp(5), dp(13), dp(5))
                setBackgroundResource(R.drawable.segment_toggle_background)
                setTextColor(context.getColorStateList(R.color.segment_toggle_text))
            }
        }
        buttons.values.forEach { group.addView(it) }
        group.check(buttons.getValue(initial.inputMethod).id)
        inputMethodButtons = buttons
        return LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(13), 0, dp(13))
            addView(label, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
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
