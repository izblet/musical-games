package com.example.musicalgames.utils.components.ui_components

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.musicalgames.R
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar

/**
 * A titled, lockable block showing a list of <label, colour swatch> pairs sharing a single
 * edit button (unlike [TunableSliderRow], individual items don't get their own lock - tapping
 * a swatch while editing opens a colour picker for just that item, but the whole block commits
 * or discards together). Generic over a String key rather than any specific musical concept
 * (e.g. interval), so the same block can back other colour-coded concepts later.
 */
class ColorCodingChoiceBlock @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attributeSet, defStyle), EditableSettingsBlock {

    private val editButton: ImageButton
    private val colourToggleButton: ImageButton
    private val title: TextView
    private val itemsContainer: LinearLayout

    private var keyNames: Map<String,String> = mapOf()
    private var itemsPerRow: Int = DEFAULT_ITEMS_PER_ROW
    private var currentColors: MutableMap<String, Int> = mutableMapOf()
    private var colorsBeforeEdit: Map<String, Int> = emptyMap()
    private var swatches: Map<String, View> = emptyMap()

    private var isEditing = false
    private var coloursEnabled = true
    private var onCommit: (Map<String, Int>) -> Unit = {}

    /** Called when the user taps the top-right toggle to enable/disable this block's colours
     * (e.g. to let a game fall back to its own colouring instead of the user's chosen palette). */
    var onColoursEnabledChanged: ((Boolean) -> Unit)? = null

    override var onEditRequested: (() -> Unit)? = null
    override var onEditButtonReTapped: (() -> Unit)? = null
    override var onEditEnded: (() -> Unit)? = null


    init {
        LayoutInflater.from(context).inflate(R.layout.view_color_coding_block, this, true)
        editButton = findViewById(R.id.editButton)
        colourToggleButton = findViewById(R.id.colourToggleButton)
        title = findViewById(R.id.title)
        itemsContainer = findViewById(R.id.itemsContainer)

        editButton.setOnClickListener {
            if (isEditing) {
                onEditButtonReTapped?.invoke()
            } else {
                onEditRequested?.invoke()
            }
        }

        colourToggleButton.setOnClickListener {
            coloursEnabled = !coloursEnabled
            applyColoursEnabledState()
            onColoursEnabledChanged?.invoke(coloursEnabled)
        }
    }

    //each element is keyed by a String id, because we're mostly going to be using this class for
    //enums - the keys will be the enum constants' `.name` then, with `keyNames`'s values free to
    //hold a separate, friendlier display name
    fun configure(
        title: String,
        keyNames: Map<String, String>,
        itemsPerRow: Int = DEFAULT_ITEMS_PER_ROW,
        onCommit: (Map<String, Int>) -> Unit
    ) {
        this.title.text = title
        this.keyNames = keyNames
        this.itemsPerRow = itemsPerRow
        this.onCommit = onCommit
        rebuildItems()
    }

    /** Sets the displayed colours without touching edit state - for initial population. */
    fun setColors(colors: Map<String, Int>) {
        this.currentColors = colors.toMutableMap()
        keyNames.forEach { updateSwatch(it.key) }
    }

    /** Sets the enabled/disabled (greyed-out) state without firing [onColoursEnabledChanged] -
     * for initial population from whatever was last persisted. */
    fun setColoursEnabled(enabled: Boolean) {
        coloursEnabled = enabled
        applyColoursEnabledState()
    }

    override fun beginEdit() {
        isEditing = true
        colorsBeforeEdit = currentColors.toMap()
        editButton.isActivated = true
        swatches.values.forEach { it.isEnabled = coloursEnabled }
    }

    override fun confirmEdit() {
        onCommit(currentColors.toMap())
        endEdit()
    }

    override fun discardEdit() {
        currentColors = colorsBeforeEdit.toMutableMap()
        keyNames.forEach { updateSwatch(it.key) }
        endEdit()
    }

    override fun getBoundsOnScreen(outRect: Rect) {
        getGlobalVisibleRect(outRect)
    }

    private fun endEdit() {
        isEditing = false
        editButton.isActivated = false
        swatches.values.forEach { it.isEnabled = false }
        onEditEnded?.invoke()
    }

    /** Greys out the swatches and blocks clicks on them while disabled; re-applied whenever the
     * toggle is flipped (including mid-edit, though that's not really an expected case in practice). */
    private fun applyColoursEnabledState() {
        colourToggleButton.isActivated = !coloursEnabled
        itemsContainer.alpha = if (coloursEnabled) 1f else DISABLED_ALPHA
        swatches.values.forEach { it.isEnabled = isEditing && coloursEnabled }
    }

    /**
     * Each row is a horizontal [LinearLayout] of [itemsPerRow] pairs (view_color_coding_pair.xml),
     * every one declared layout_width="0dp"/layout_weight="1" so LinearLayout's own weight system
     * - not any manually computed pixel math - splits the row into exactly equal columns. Inside
     * each pair, the label itself is also weight="1" (not wrap_content), filling the rest of its
     * own already-fixed-width column before the fixed-size swatch; that's what makes the swatch
     * land at the same offset in every row regardless of how wide each row's labels are. A short
     * last row gets an equally-weighted invisible filler per missing slot, so its real pair(s)
     * don't stretch to claim the unclaimed columns.
     */
    private fun rebuildItems() {
        itemsContainer.removeAllViews()
        val swatchMap = mutableMapOf<String, View>()

        keyNames.keys.chunked(itemsPerRow).forEach { rowKeys ->
            val row = LinearLayout(context).apply {
                orientation = HORIZONTAL
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            }
            rowKeys.forEach { key -> row.addView(buildPair(row, key, swatchMap)) }
            repeat(itemsPerRow - rowKeys.size) {
                row.addView(View(context), LinearLayout.LayoutParams(0, 0, 1f))
            }
            itemsContainer.addView(row)
        }
        swatches = swatchMap
        keyNames.forEach { updateSwatch(it.key) }
    }

    private fun buildPair(row: LinearLayout, key: String, swatchMap: MutableMap<String, View>): View {
        val pairView = LayoutInflater.from(context).inflate(R.layout.view_color_coding_pair, row, false)
        pairView.findViewById<TextView>(R.id.itemLabel).text = keyNames[key]
        val swatch = pairView.findViewById<View>(R.id.colorSwatch)
        // explicit rather than relying solely on the XML android:enabled="false" attribute -
        // that alone left swatches clickable until the first real beginEdit()/endEdit() cycle
        swatch.isEnabled = isEditing && coloursEnabled
        swatch.setOnClickListener {
            val current = currentColors[key] ?: Color.WHITE
            showColorPicker(current) {picked->
                currentColors[key] = picked
                setSwatchColor(swatch, picked)
            }

        }
        swatchMap[key] = swatch
        return pairView
    }

    private fun updateSwatch(key: String) {
        val swatch = swatches[key] ?: return
        setSwatchColor(swatch, currentColors[key] ?: Color.WHITE)
    }

    private fun setSwatchColor(swatch: View, color: Int) {
        val drawable = swatch.background as? GradientDrawable ?: GradientDrawable().also {
            it.shape = GradientDrawable.RECTANGLE
            it.cornerRadius = 6f
            it.setStroke(1, Color.GRAY)
            swatch.background = it
        }
        drawable.setColor(color)
    }
    private fun showColorPicker(current: Int, onPicked: (Int) -> Unit) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_color_picker, null)
        val preview = dialogView.findViewById<View>(R.id.colorPreview)
        val colorPickerView = dialogView.findViewById<ColorPickerView>(R.id.colorPickerView)
        val brightnessSlideBar = dialogView.findViewById<BrightnessSlideBar>(R.id.brightnessSlideBar)
        val redSeekBar = dialogView.findViewById<SeekBar>(R.id.redSeekBar)
        val greenSeekBar = dialogView.findViewById<SeekBar>(R.id.greenSeekBar)
        val blueSeekBar = dialogView.findViewById<SeekBar>(R.id.blueSeekBar)

        // the gradient is what we want visible across the whole track, not the seekbar's own
        // default "filled up to thumb" accent-colour look, which would otherwise paint over it
        for (seekBar in listOf(redSeekBar, greenSeekBar, blueSeekBar)) {
            seekBar.progressDrawable = ColorDrawable(Color.TRANSPARENT)
        }

        fun updateRgbGradients() {
            val r = redSeekBar.progress
            val g = greenSeekBar.progress
            val b = blueSeekBar.progress
            redSeekBar.background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(Color.rgb(0, g, b), Color.rgb(255, g, b))
            )
            greenSeekBar.background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(Color.rgb(r, 0, b), Color.rgb(r, 255, b))
            )
            blueSeekBar.background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(Color.rgb(r, g, 0), Color.rgb(r, g, 255))
            )
        }

        fun setRgbSliders(color: Int) {
            redSeekBar.progress = Color.red(color)
            greenSeekBar.progress = Color.green(color)
            blueSeekBar.progress = Color.blue(color)
            updateRgbGradients()
        }

        colorPickerView.attachBrightnessSlider(brightnessSlideBar)
        colorPickerView.setInitialColor(current)
        preview.setBackgroundColor(current)
        setRgbSliders(current)

        colorPickerView.setColorListener(ColorEnvelopeListener { envelope, _ ->
            preview.setBackgroundColor(envelope.color)
            setRgbSliders(envelope.color)
        })

        val rgbListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (!fromUser) return
                val color = Color.rgb(redSeekBar.progress, greenSeekBar.progress, blueSeekBar.progress)
                preview.setBackgroundColor(color)
                updateRgbGradients()
                colorPickerView.setInitialColor(color)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        }
        redSeekBar.setOnSeekBarChangeListener(rgbListener)
        greenSeekBar.setOnSeekBarChangeListener(rgbListener)
        blueSeekBar.setOnSeekBarChangeListener(rgbListener)

        AlertDialog.Builder(context)
            .setTitle("Choose a colour")
            .setView(dialogView)
            .setPositiveButton("Select") { _, _ -> onPicked(colorPickerView.color) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    companion object {
        const val DEFAULT_ITEMS_PER_ROW = 3
        private const val DISABLED_ALPHA = 0.4f
    }
}
