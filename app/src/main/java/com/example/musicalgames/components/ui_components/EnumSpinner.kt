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
        setCompleteOrThrow()
        val values = enumValues<T>()
        val adapter = ArrayAdapter(context,
            android.R.layout.simple_spinner_dropdown_item,
            values.map{it.toString()}
            ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        this.adapter=adapter
        return SpinnerEnumValue(values)
    }
    fun setCompleteOrThrow() {
        if(enumSet) {
            throw IllegalStateException("the values are already set")
        }
        enumSet = true
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        Log.d("spinner", "spinner clicked")
        super.onClick(dialog, which)
    }

    inner class SpinnerEnumValue<T: Enum<T>>(private val values: Array<T>) {
        fun getSelectedValue() : T {
            val selectedIndex = selectedItemPosition
            val selectedValue = values.getOrNull(selectedIndex) ?: throw IllegalStateException("no field in enum for the selection")

            return selectedValue
        }
    }
}