package com.example.musicalgames.settings

import android.content.Context
import androidx.core.content.edit

class EnumColorSettingsRepository(context: Context) {
    private val prefs = context.getSharedPreferences(MicrophoneSettingsRepository.PREFS_NAME, Context.MODE_PRIVATE)

    //note: the defaults here to simplify the functionality of the class and allow for substitutions
    //as a lambda, so that we are always sure to get something
    public fun <T : Enum<T>> get(enumClass: Class<T>, defaults: (T)->Int) : Map<T, Int> {
        return enumClass.enumConstants!!.associateWith { el ->
            prefs.getInt(getPreferencesKey(enumClass, el),defaults(el))
        }
    }

    public fun <T: Enum<T>> put(enumClass: Class<T>, values: Map<T,Int>) {
        //kotlin syntactic sugar to edit quicker
        prefs.edit {
            values.forEach { (key, value) -> putInt(getPreferencesKey(enumClass, key), value) }
        }
    }

    fun <T: Enum<T>> getPreferencesKey(enumClass: Class<T>, element: T):String {
        return "enum_color_${enumClass.name}_${element.name}"
    }

}