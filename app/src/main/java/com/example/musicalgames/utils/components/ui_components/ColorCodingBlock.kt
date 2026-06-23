package com.example.musicalgames.utils.components.ui_components

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.example.musicalgames.R

/**
 * A titled, lockable block showing a list of <label, colour swatch> pairs sharing a single
 * edit button (unlike [TunableSliderRow], individual items don't get their own lock - tapping
 * a swatch while editing opens a colour picker for just that item, but the whole block commits
 * or discards together). Generic over a String key rather than any specific musical concept
 * (e.g. interval), so the same block can back other colour-coded concepts later.
 */
class ColorCodingBlock @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attributeSet, defStyle), EditableSettingsBlock {

    private val editButton: ImageButton
    private val title: TextView
    private val itemsContainer: LinearLayout

    private var keys: List<String> = emptyList()
    private var itemsPerRow: Int = DEFAULT_ITEMS_PER_ROW
    private var colors: MutableMap<String, Int> = mutableMapOf()
    private var colorsBeforeEdit: Map<String, Int> = emptyMap()
    private var swatches: Map<String, View> = emptyMap()

    private var isEditing = false
    private var onPickColor: ((key: String, current: Int, onPicked: (Int) -> Unit) -> Unit)? = null
    private var onCommit: (Map<String, Int>) -> Unit = {}

    override var onEditRequested: (() -> Unit)? = null
    override var onEditButtonReTapped: (() -> Unit)? = null
    override var onEditEnded: (() -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_color_coding_block, this, true)
        editButton = findViewById(R.id.editButton)
        title = findViewById(R.id.title)
        itemsContainer = findViewById(R.id.itemsContainer)

        editButton.setOnClickListener {
            if (isEditing) {
                onEditButtonReTapped?.invoke()
            } else {
                onEditRequested?.invoke()
            }
        }
    }

    fun configure(
        title: String,
        keys: List<String>,
        itemsPerRow: Int = DEFAULT_ITEMS_PER_ROW,
        onPickColor: (key: String, current: Int, onPicked: (Int) -> Unit) -> Unit,
        onCommit: (Map<String, Int>) -> Unit
    ) {
        this.title.text = title
        this.keys = keys
        this.itemsPerRow = itemsPerRow
        this.onPickColor = onPickColor
        this.onCommit = onCommit
        rebuildItems()
    }

    /** Sets the displayed colours without touching edit state - for initial population. */
    fun setColors(colors: Map<String, Int>) {
        this.colors = colors.toMutableMap()
        keys.forEach { updateSwatch(it) }
    }

    override fun beginEdit() {
        isEditing = true
        colorsBeforeEdit = colors.toMap()
        editButton.isActivated = true
        swatches.values.forEach { it.isEnabled = true }
    }

    override fun confirmEdit() {
        onCommit(colors.toMap())
        endEdit()
    }

    override fun discardEdit() {
        colors = colorsBeforeEdit.toMutableMap()
        keys.forEach { updateSwatch(it) }
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
        keys.chunked(itemsPerRow).forEach { rowKeys ->
            val row = LinearLayout(context).apply {
                orientation = HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            }
            rowKeys.forEach { key -> row.addView(buildPair(row, key, swatchMap)) }
            repeat(itemsPerRow - rowKeys.size) {
                row.addView(View(context), LinearLayout.LayoutParams(0, 0, 1f))
            }
            itemsContainer.addView(row)
        }
        swatches = swatchMap
        keys.forEach { updateSwatch(it) }
    }

    private fun buildPair(row: LinearLayout, key: String, swatchMap: MutableMap<String, View>): View {
        val pairView = LayoutInflater.from(context).inflate(R.layout.view_color_coding_pair, row, false)
        pairView.findViewById<TextView>(R.id.itemLabel).text = key
        val swatch = pairView.findViewById<View>(R.id.colorSwatch)
        // explicit rather than relying solely on the XML android:enabled="false" attribute -
        // that alone left swatches clickable until the first real beginEdit()/endEdit() cycle
        swatch.isEnabled = isEditing
        swatch.setOnClickListener {
            val current = colors[key] ?: Color.WHITE
            onPickColor?.invoke(key, current) { picked ->
                colors[key] = picked
                setSwatchColor(swatch, picked)
            }
        }
        swatchMap[key] = swatch
        return pairView
    }

    private fun updateSwatch(key: String) {
        val swatch = swatches[key] ?: return
        setSwatchColor(swatch, colors[key] ?: Color.WHITE)
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

    companion object {
        const val DEFAULT_ITEMS_PER_ROW = 3
    }
}
