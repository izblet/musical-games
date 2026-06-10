package com.example.musicalgames.components.ui_components

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.example.musicalgames.R

class MultiEnumSpinner @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatButton(context, attributeSet, defStyleAttr) {

    init {
        setBackgroundResource(R.drawable.item_bordered)
        gravity = Gravity.CENTER
    }

    private var enumSet = false

    // internal (not public) so that private members can be accessed from inline functions
    internal inline fun <reified T : Enum<T>> setEnum(noinline displayName: ((T) -> String)? = null): SpinnerEnumValues<T> {
        return setEnum(emptySet(), displayName)
    }

    internal inline fun <reified T : Enum<T>> setEnum(defaultVals: Set<T>, noinline displayName: ((T) -> String)? = null): SpinnerEnumValues<T> {
        return setEnum(enumValues<T>(), defaultVals, displayName)
    }

    internal inline fun <reified T : Enum<T>> setEnum(values: Array<T>, defaultVals: Set<T>, noinline displayName: ((T) -> String)? = null): SpinnerEnumValues<T> {
        if (enumSet) throw IllegalStateException("the values are already set")
        enumSet = true

        val labels = values.map { displayName?.invoke(it) ?: it.toString() }.toTypedArray()
        val selected = BooleanArray(values.size) { values[it] in defaultVals }

        updateButtonText(labels, selected)

        setOnClickListener {
            val tempSelected = selected.copyOf()
            AlertDialog.Builder(context)
                .setMultiChoiceItems(labels, tempSelected) { _, which, isChecked ->
                    tempSelected[which] = isChecked
                }
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    tempSelected.copyInto(selected)
                    updateButtonText(labels, selected)
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }

        return SpinnerEnumValues(values, selected, labels, defaultVals)
    }

    private fun updateButtonText(labels: Array<String>, selected: BooleanArray) {
        val chosen = labels.filterIndexed { i, _ -> selected[i] }
        text = if (chosen.isEmpty()) "None" else chosen.joinToString(", ")
    }

    inner class SpinnerEnumValues<T : Enum<T>>(
        private val values: Array<T>,
        private val selected: BooleanArray,
        private val labels: Array<String>,
        private val defaultVals: Set<T>,
    ) {
        fun getSelectedValues(): Set<T> {
            return values.filterIndexed { i, _ -> selected[i] }.toSet()
        }

        fun resetToDefault() {
            values.forEachIndexed { i, v -> selected[i] = v in defaultVals }
            updateButtonText(labels, selected)
        }

        fun setSelectedValues(selectedValues: Set<T>) {
            values.forEachIndexed { i, v -> selected[i] = v in selectedValues }
            updateButtonText(labels, selected)
        }
    }
}
