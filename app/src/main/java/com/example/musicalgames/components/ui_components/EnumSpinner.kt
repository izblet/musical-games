package com.example.musicalgames.components.ui_components

import android.content.Context
import android.content.DialogInterface
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner

class EnumSpinner  @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatSpinner(context, attributeSet, defStyleAttr, MODE_DROPDOWN){
    private var enumSet = false

    // if you find a better idea, go ahead. i want to inflate from xml but i don't want to have to
    // specify the type in order to retrieve the value
    // why? idk, i just don't wanna
    inline fun <reified T: Enum<T>> setEnum() : SpinnerEnumValue<T> {
       return setEnum(enumValues<T>()[0])
    }

    inline fun <reified T: Enum<T>> setEnum(defaultVal : T) : SpinnerEnumValue<T> {
        val values = enumValues<T>()
        return setEnum(values, defaultVal)

    }

    inline fun <reified T: Enum<T>> setEnum(values: Array<T>, defaultVal: T) :SpinnerEnumValue<T> {
        setCompleteOrThrow()
        val adapter = ArrayAdapter(context,
            android.R.layout.simple_spinner_dropdown_item,
            values.map{it.toString()}
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        this.adapter=adapter

        val index = values.indexOf(defaultVal)
        if(index == -1) {
            throw IllegalArgumentException("default element is not it the set of options")
        }

        setSelection(index)

        return SpinnerEnumValue(values)

    }

    fun setCompleteOrThrow() {
        if(enumSet) {
            throw IllegalStateException("the values are already set")
        }
        enumSet = true
    }

    inner class SpinnerEnumValue<T: Enum<T>>(private val values: Array<T>) {
        fun getSelectedValue() : T {
            val selectedIndex = selectedItemPosition
            val selectedValue = values.getOrNull(selectedIndex) ?: throw IllegalStateException("no field in enum for the selection")

            return selectedValue
        }
    }
}