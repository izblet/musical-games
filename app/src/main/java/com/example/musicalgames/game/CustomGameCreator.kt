package com.example.musicalgames.games

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.example.musicalgames.R
import com.example.musicalgames.game_activity.Level

abstract class CustomGameCreator(context :Context, private val createLevelAction: (Level)->Unit, attrSet: AttributeSet?) : TableLayout(context, attrSet) {
    abstract fun getLevel(): Level?
    abstract fun highlightMissing()
    abstract fun clearSelection()

    init {
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
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

    private fun styleSelector(selector : View) {
        //some of the parameters unnecessary for now but will probably be used later
        selector.apply {
            layoutParams = TableRow.LayoutParams(0, LayoutParams.MATCH_PARENT)
                .apply {
                    column = 2
                    span = 1
                    weight = 1f
                }
        }
    }
    private fun styleLabel(label : TextView) {
        label.apply {
            layoutParams = TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT).apply {
               column =1
               span =1
               weight = 0f
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

        val lab = TextView(context, null, 0, R.style.customLabelStyle).apply {
            text = label
        }

        lab.text = label
        styleLabel(lab)
        row.addView(lab)

        styleSelector(selector)
        row.addView(selector)

        return row
    }

    protected fun addNewRow(label: String, selector : View) : TableRow {
        val row = makeRow(label, selector)
        addView(row)
        return row
    }

}