package com.example.musicalgames.utils.components.ui_components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.example.musicalgames.R
import com.google.android.material.slider.Slider
import kotlin.math.roundToInt

/**
 * A labeled [Slider] row that starts locked (slider/+-/reset all disabled) so a stray touch
 * can't change anything. Tapping the edit button unlocks it; from then on, tapping the edit
 * button again - or tapping anywhere outside this row, which the screen detects and forwards
 * here - asks the screen to prompt "save changes?", mirroring FragmentLevelOptions' edit-toggle
 * pattern. Reset commits the default immediately and ends the edit session, same as a save.
 */
class TunableSliderRow @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attributeSet, defStyle) {

    private val label: TextView
    private val valueText: TextView
    private val editButton: ImageButton
    private val resetButton: ImageButton
    private val slider: Slider
    private val decreaseButton: ImageButton
    private val increaseButton: ImageButton

    private var format: (Float) -> String = { it.toString() }
    private var defaultValue: Float = 0f
    private var onCommit: (Float) -> Unit = {}

    private var isEditing = false
    private var valueBeforeEdit = 0f

    /** Fired when the edit button is tapped while not editing - only reachable when no other
     * row is mid-edit, since the screen's overlay blocks every other touch until then. */
    var onEditRequested: (() -> Unit)? = null

    /** Fired when the edit button is tapped again while already editing - same "save changes?"
     * prompt as tapping outside the row. */
    var onEditButtonReTapped: (() -> Unit)? = null

    /** Fired whenever an edit session ends, however it ended (confirmed, discarded, or reset
     * mid-edit) - lets the screen tear down its cross-row overlay/tracking from one place. */
    var onEditEnded: (() -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_tunable_slider_row, this, true)
        label = findViewById(R.id.label)
        valueText = findViewById(R.id.valueText)
        editButton = findViewById(R.id.editButton)
        resetButton = findViewById(R.id.resetButton)
        slider = findViewById(R.id.slider)
        decreaseButton = findViewById(R.id.decreaseButton)
        increaseButton = findViewById(R.id.increaseButton)

        editButton.setOnClickListener {
            if (isEditing) {
                onEditButtonReTapped?.invoke()
            } else {
                onEditRequested?.invoke()
            }
        }

        slider.addOnChangeListener { _, value, _ -> valueText.text = format(value) }

        decreaseButton.setOnClickListener { step(-1) }
        increaseButton.setOnClickListener { step(1) }

        resetButton.setOnClickListener {
            setValue(defaultValue)
            onCommit(defaultValue)
            endEdit()
        }

        // explicit rather than relying solely on the XML android:enabled="false" attributes -
        // those alone left the controls clickable until the first real beginEdit()/endEdit() cycle
        setControlsEnabled(false)
    }

    fun configure(
        label: String,
        valueFrom: Float,
        valueTo: Float,
        stepSize: Float,
        defaultValue: Float,
        format: (Float) -> String,
        onCommit: (Float) -> Unit
    ) {
        this.label.text = label
        slider.valueFrom = valueFrom
        slider.valueTo = valueTo
        slider.stepSize = stepSize
        this.defaultValue = defaultValue
        this.format = format
        this.onCommit = onCommit
    }

    /** Sets the displayed value without touching edit state - for initial population/reset-all. */
    fun setValue(value: Float) {
        val snapped = snapToStep(value)
        slider.value = snapped
        valueText.text = format(snapped)
    }

    fun getValue(): Float = slider.value

    fun beginEdit() {
        isEditing = true
        valueBeforeEdit = slider.value
        editButton.isActivated = true
        setControlsEnabled(true)
    }

    /** Called by the screen when the user picks "Save" in the confirmation prompt. */
    fun confirmEdit() {
        onCommit(slider.value)
        endEdit()
    }

    /** Called by the screen when the user picks "Discard" in the confirmation prompt. */
    fun discardEdit() {
        setValue(valueBeforeEdit)
        endEdit()
    }

    private fun endEdit() {
        isEditing = false
        editButton.isActivated = false
        setControlsEnabled(false)
        onEditEnded?.invoke()
    }

    private fun setControlsEnabled(enabled: Boolean) {
        slider.isEnabled = enabled
        decreaseButton.isEnabled = enabled
        increaseButton.isEnabled = enabled
        resetButton.isEnabled = enabled
    }

    private fun step(direction: Int) {
        slider.value = snapToStep(slider.value + direction * slider.stepSize)
    }

    /** Material's [Slider] requires its value to land exactly on a valueFrom + n*stepSize point. */
    private fun snapToStep(value: Float): Float {
        val from = slider.valueFrom
        val step = slider.stepSize
        val snapped = from + ((value - from) / step).roundToInt() * step
        return snapped.coerceIn(slider.valueFrom, slider.valueTo)
    }
}
