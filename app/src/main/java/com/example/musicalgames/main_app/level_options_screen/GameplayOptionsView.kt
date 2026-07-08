package com.example.musicalgames.main_app.level_options_screen

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
 * Shows whichever of a game's supported [com.example.musicalgames.game.game_core.GameplayOptions] have a rendered control today, and
 * assembles the edited values into one [com.example.musicalgames.game.game_core.GamePlayInstance]. Same role as `CustomGameCreator`
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

        val content = LinearLayout(context).apply {
            orientation = VERTICAL
            val paddingPx = resources.getDimensionPixelSize(R.dimen.editable_row_padding)
            setPadding(paddingPx, 0, paddingPx, paddingPx)
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
            content.setBackgroundResource(R.drawable.background_block)
            addView(content)
            updateSummary()
        }
    }

    private fun themeColor(attr: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    private fun buildHeaderRow(): LinearLayout {
        val label = TextView(context).apply {
            text = "Gameplay"
            setTextColor(themeColor(R.attr.textTertiaryColor))
            textSize = 10.5f
            isAllCaps = true
            setTypeface(typeface, Typeface.BOLD)
        }
        val elementPaddingPx = resources.getDimensionPixelSize(R.dimen.list_item_padding)
        summaryTextView = TextView(context).apply {
            setTextColor(themeColor(R.attr.textSecondaryColor))
            textSize = 12.5f
            ellipsize = TextUtils.TruncateAt.END
            maxLines = 1
            setPadding(elementPaddingPx, 0, elementPaddingPx, 0)
        }
        chevronView = ImageView(context).apply {
            setImageResource(R.drawable.ic_chevron_down)
        }
        val chevronIconSizePx = resources.getDimensionPixelSize(R.dimen.icon_tiny_size)

        val spacer = View(context)
        return LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(elementPaddingPx, elementPaddingPx, elementPaddingPx, elementPaddingPx)
            isClickable = true
            isFocusable = true
            setBackgroundResource(R.drawable.spinner_chip_background)
            addView(label)
            addView(spacer, LayoutParams(0, 0, 1f))
            addView(summaryTextView)
            addView(chevronView, LayoutParams(chevronIconSizePx, chevronIconSizePx))
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
        val dividerHeightPx = resources.getDimensionPixelSize(R.dimen.divider_height)
        it.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, dividerHeightPx)
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
            setTextColor(themeColor(R.attr.textSecondaryColor))
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
            val paddingPx = resources.getDimensionPixelSize(R.dimen.container_padding)
            setPadding(0, paddingPx, 0, paddingPx)
            addView(label, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))

            val heightPx = resources.getDimensionPixelSize(R.dimen.editable_row_height)
            addView(bpmEditText, LayoutParams(heightPx, LayoutParams.WRAP_CONTENT))
        }
    }

    private fun buildInputMethodRow(initial: GamePlayInstance): LinearLayout {
        val label = TextView(context).apply {
            text = "External instrument:"
            setTextColor(themeColor(R.attr.textSecondaryColor))
        }
        val group = RadioGroup(context).apply {
            orientation = HORIZONTAL
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.TRANSPARENT)
                val dividerHeightPx = resources.getDimensionPixelSize(R.dimen.divider_height)
                setStroke(dividerHeightPx, themeColor(R.attr.hairlineColor))
            }
            setOnCheckedChangeListener { _, _ -> updateSummary() }
        }
        val labelGapTight = resources.getDimensionPixelSize(R.dimen.label_gap_tight)
        val contentPaddingPx = resources.getDimensionPixelSize(R.dimen.container_padding)
        val buttons = InputMethod.entries.associateWith { method ->
            RadioButton(context).apply {
                id = generateViewId()
                text = if (method == InputMethod.EXTERNAL_INSTRUMENT) "ON" else "OFF"
                buttonDrawable = null
                textSize = 11.5f
                val paddingPx = resources.getDimensionPixelSize(R.dimen.container_padding)
                setPadding(paddingPx, labelGapTight, paddingPx, labelGapTight)
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
            setPadding(0, contentPaddingPx, 0, contentPaddingPx)
            addView(label, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))
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