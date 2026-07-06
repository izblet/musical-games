package com.example.musicalgames.utils.components.ui_components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import com.example.musicalgames.R

//Some creators construct this via a ContextThemeWrapper over customInputElementStyle (a widget
//style, not a full theme - see the TODO on that wrapper) so the chip gets the right
//background/padding. But AppCompatSpinner also builds its dropdown popup from that same
//context's theme, and a widget style doesn't carry the theme attributes (colorAccent, dialog
//styles, etc.) a popup/dialog needs - explicitly passing the *un-wrapped* context's theme as
//popupTheme keeps the chip styling while giving the popup a complete theme to build from.
//Other creators just pass the plain Activity context straight through - Activity is itself a
//ContextThemeWrapper subclass, so it must be excluded here, or this unwraps the Activity down
//to its raw pre-theme base context instead of our own wrapper, crashing the popup.
class EnumSpinner  @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatSpinner(
    context, attributeSet, defStyleAttr, MODE_DROPDOWN,
    (if (context is ContextWrapper && context !is Activity) context.baseContext else context).theme
) {
    private var enumSet = false

    init {
        //customInputElementStyle's own popupBackground item doesn't reliably reach this widget
        //when constructed via the ContextThemeWrapper path (see class doc above) - set directly.
        setPopupBackgroundResource(R.drawable.spinner_popup_background)
    }

    // if you find a better idea, go ahead. i want to inflate from xml but i don't want to have to
    // specify the type in order to retrieve the value
    // why? idk, i just don't wanna
    inline fun <reified T: Enum<T>> setEnum(noinline displayName: ((T) -> String)? = null) : SpinnerEnumValue<T> {
       return setEnum(enumValues<T>()[0], displayName)
    }

    inline fun <reified T: Enum<T>> setEnum(defaultVal : T, noinline displayName: ((T) -> String)? = null) : SpinnerEnumValue<T> {
        val values = enumValues<T>()
        return setEnum(values, defaultVal, displayName)

    }

    inline fun <reified T: Enum<T>> setEnum(values: Array<T>, defaultVal: T, noinline displayName: ((T) -> String)? = null) :SpinnerEnumValue<T> {
        setCompleteOrThrow()
        val adapter = ArrayAdapter(context,
            android.R.layout.simple_spinner_dropdown_item,
            values.map{ displayName?.invoke(it) ?: it.toString() }
        ).apply { setDropDownViewResource(R.layout.spinner_dropdown_item) }
        this.adapter=adapter

        val index = values.indexOf(defaultVal)
        if(index == -1) {
            throw IllegalArgumentException("default element is not it the set of options")
        }

        setSelection(index)

        return SpinnerEnumValue(values, index)

    }

    fun setCompleteOrThrow() {
        if(enumSet) {
            throw IllegalStateException("the values are already set")
        }
        enumSet = true
    }

    inner class SpinnerEnumValue<T: Enum<T>>(private val values: Array<T>, private val defaultIndex: Int) {
        fun getSelectedValue() : T {
            val selectedIndex = selectedItemPosition
            val selectedValue = values.getOrNull(selectedIndex) ?: throw IllegalStateException("no field in enum for the selection")

            return selectedValue
        }

        fun resetToDefault() {
            setSelection(defaultIndex)
        }

        fun setSelectedValue(value: T) {
            val index = values.indexOf(value)
            if(index == -1) {
                throw IllegalArgumentException("value is not in the set of options")
            }
            setSelection(index)
        }
    }
}