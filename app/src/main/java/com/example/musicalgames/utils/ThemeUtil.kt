package com.example.musicalgames.utils

import android.content.Context
import android.util.TypedValue

object ThemeUtil {
    fun themeColor(context: Context?, attr: Int): Int {
        //returns something stupid if no context
        val typedValue = TypedValue()
        context?.theme?.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }
}