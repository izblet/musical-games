package com.example.musicalgames.settings

import android.content.Context
import androidx.core.content.edit

class EnumColorSettingsRepository(context: Context) {
    private val prefs = context.getSharedPreferences(SettingsStorage.PREFS_NAME, Context.MODE_PRIVATE)

    //note: the defaults here to simplify the functionality of the class and allow for substitutions
    //as a lambda, so that we are always sure to get something
    fun <T : Enum<T>> get(enumClass: Class<T>, defaults: (T)->Int) : Map<T, Int> {
        return enumClass.enumConstants!!.associateWith { el ->
            prefs.getInt(getKeyString(enumClass, el),defaults(el))
        }
    }

    fun <T: Enum<T>> blockEnumColor(enumClass: Class<T>, block: Boolean) {
        prefs.edit {
            putBoolean(getBlockKeyString(enumClass), block)
        }
    }

    fun <T: Enum<T>> isEnumColorBlocked(enumClass: Class<T>) : Boolean {
        return prefs.getBoolean(getBlockKeyString(enumClass), false)
    }

    fun <T: Enum<T>> put(enumClass: Class<T>, values: Map<T,Int>) {
        //kotlin syntactic sugar to edit quicker
        prefs.edit {
            values.forEach { (key, value) -> putInt(getKeyString(enumClass, key), value) }
        }
    }

    private fun <T: Enum<T>> getKeyString(enumClass: Class<T>, element: T):String {
        return "enum_color_${enumClass.name}_${element.name}"
    }
    private fun <T: Enum<T>> getBlockKeyString(enumClass: Class<T>) :String {
        return "enum_block_color_${enumClass.name}"
    }

}