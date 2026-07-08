package com.example.musicalgames.game.game_core.creation

import android.R
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.example.musicalgames.utils.components.ui_components.IntervalSelector
import com.example.musicalgames.utils.components.ui_components.KeyboardSelector

abstract class CustomGameCreator(context : Context, private val createLevelAction: (Level)->Unit, attrSet: AttributeSet?) : TableLayout(context, attrSet) {
    abstract fun getLevel(): Level?
    abstract fun highlightMissing()
    abstract fun clearSelection()

    private val selectors: MutableList<View> = mutableListOf()

    var editable: Boolean = true
        private set

    init {
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
    }

    private val editTextColors: MutableMap<EditText, ColorStateList> = mutableMapOf()

    fun setEditable(editable: Boolean) {
        this.editable = editable
        for (selector in selectors) {
            //EditText's default disabled style greys out the text, making the value hard to
            //read - keep its normal "enabled" colour even while disabled
            if (selector is EditText) {
                if (editable) {
                    editTextColors[selector]?.let { selector.setTextColor(it) }
                } else {
                    val originalColors = editTextColors.getOrPut(selector) { selector.textColors }
                    selector.setTextColor(originalColors.getColorForState(intArrayOf(R.attr.state_enabled), originalColors.defaultColor))
                }
            }
            selector.isEnabled = editable
        }
    }

    //for creators that build their selectors from an inflated layout instead of makeRow/addNewRow
    protected fun registerSelector(selector: View) {
        selectors.add(selector)
    }

    fun saveLevel() : Boolean {
        val level = getLevel()
        if (level == null) {
            highlightMissing()
            return false
        } else {
            createLevelAction(level)
            return true
        }
    }

    //TableRow.LayoutParams built by hand here need a real pixel height (unlike the
    //wrap_content+minHeight chips used by LinearLayout-row-based creator layouts) - kept as its
    //own dimen rather than read from customInputElementStyle at runtime, since that style's
    //layout_height is intentionally wrap_content, not a fixed dimension
    private val inputElementHeight: Int = run {
        context.resources.getDimensionPixelSize(com.example.musicalgames.R.dimen.editable_row_height)
    }

    //Shared with customLabelStyle/customInputStyle (styles.xml) so the XML-row path (Play By
    //Ear) and this programmatic TableRow path (every other game) align identically.
    private val labelWidth: Int = context.resources.getDimensionPixelSize(com.example.musicalgames.R.dimen.custom_creator_label_width)
    private val labelMargin: Int = context.resources.getDimensionPixelSize(com.example.musicalgames.R.dimen.custom_creator_label_margin)
    private val chipGap: Int = context.resources.getDimensionPixelSize(com.example.musicalgames.R.dimen.custom_creator_chip_gap)
    private val rowGap: Int = context.resources.getDimensionPixelSize(com.example.musicalgames.R.dimen.custom_creator_row_gap)
    private val chipPaddingHorizontal: Int = context.resources.getDimensionPixelSize(com.example.musicalgames.R.dimen.custom_creator_chip_padding_horizontal)
    private val chipPaddingEnd: Int = context.resources.getDimensionPixelSize(com.example.musicalgames.R.dimen.custom_creator_chip_padding_end)
    private val chipPaddingVertical: Int = context.resources.getDimensionPixelSize(com.example.musicalgames.R.dimen.custom_creator_chip_padding_vertical)
    private val previewChipPadding: Int = context.resources.getDimensionPixelSize(com.example.musicalgames.R.dimen.custom_creator_preview_chip_padding)

    private fun styleSelector(selector : View, column: Int = 2) {
        //some of the parameters unnecessary for now but will probably be used later
        selector.apply {
            //Not every selector reliably gets customInputElementStyle applied through its own
            //construction (KeyboardSelector/IntervalSelector/MultiEnumSpinner are built with a
            //plain context in several creators, never picking it up at all) - apply the same
            //chip look here once, so every selector gets it regardless of how it was built.
            setBackgroundResource(com.example.musicalgames.R.drawable.spinner_chip_background)
            if (selector is KeyboardSelector || selector is IntervalSelector) {
                //these draw their own piano/interval preview and should nearly fill the chip,
                //unlike text selectors which need room for a label
                setPadding(previewChipPadding, previewChipPadding, previewChipPadding, previewChipPadding)
            } else {
                setPaddingRelative(chipPaddingHorizontal, chipPaddingVertical, chipPaddingEnd, chipPaddingVertical)
            }
            layoutParams = TableRow.LayoutParams(0, inputElementHeight)
                .apply {
                    this.column = column
                    span = 1
                    weight = 1f
                    if (column > 2) marginStart = chipGap
                }
        }
    }
    private fun styleLabel(label : TextView) {
        label.apply {
            layoutParams = TableRow.LayoutParams(labelWidth, LayoutParams.MATCH_PARENT).apply {
               column =1
               span =1
               weight = 0f
               marginEnd = labelMargin
            }
        }
    }

    protected fun makeRow(label: String, selector: View) : TableRow {
        val row= TableRow(context).apply {
           layoutParams = LayoutParams(
               LayoutParams.MATCH_PARENT,
               LayoutParams.MATCH_PARENT
           )
        }

        val lab = TextView(context, null, 0, com.example.musicalgames.R.style.customLabelStyle).apply {
            text = label
        }

        lab.text = label
        styleLabel(lab)
        row.addView(lab)

        styleSelector(selector)
        row.addView(selector)
        selectors.add(selector)

        return row
    }

    protected fun makeRow(label: String, selector1: View, selector2: View) : TableRow {
        val row= TableRow(context).apply {
           layoutParams = LayoutParams(
               LayoutParams.MATCH_PARENT,
               LayoutParams.MATCH_PARENT
           )
        }

        val lab = TextView(context, null, 0, com.example.musicalgames.R.style.customLabelStyle).apply {
            text = label
        }

        lab.text = label
        styleLabel(lab)
        row.addView(lab)

        styleSelector(selector1, column = 2)
        row.addView(selector1)
        selectors.add(selector1)

        styleSelector(selector2, column = 3)
        row.addView(selector2)
        selectors.add(selector2)

        return row
    }

    protected fun addNewRow(label: String, selector : View) : TableRow {
        val row = makeRow(label, selector)
        if (childCount > 0) (row.layoutParams as LayoutParams).topMargin = rowGap
        addView(row)
        return row
    }

    protected fun addNewRow(label: String, selector1: View, selector2: View) : TableRow {
        val row = makeRow(label, selector1, selector2)
        if (childCount > 0) (row.layoutParams as LayoutParams).topMargin = rowGap
        addView(row)
        return row
    }

}