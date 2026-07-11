package com.example.musicalgames.settings

import android.graphics.Color
import androidx.transition.Visibility
import com.example.musicalgames.music_model.Interval
import com.example.musicalgames.music_model.Mode

/** Per-interval colour coding, used to tint interval names in the Mental Intervals game. */
object EnumColorSettings {
    // matches the colours MentalView.kt's Interval.findColour() has always used
    fun defaultColorFor(interval: Interval): Int = when (interval) {
        Interval.P1 -> 0xffffffff.toInt()
        Interval.m2 -> 0xff00f5f8.toInt()
        Interval.M2 -> 0xff04acff.toInt()
        Interval.m3 -> 0xffe68400.toInt()
        Interval.M3 -> 0xffffef00.toInt()
        Interval.P4 -> 0xff76bbe7.toInt()
        Interval.TT -> 0xffdcfb00.toInt()
        Interval.P5 -> 0xff5ac400.toInt()
        Interval.m6 -> 0xffd00051.toInt()
        Interval.M6 -> 0xffb50c00.toInt()
        Interval.m7 -> 0xff814eae.toInt()
        Interval.M7 -> 0xfffc00c2.toInt()
        Interval.P8 -> 0xffffffff.toInt()
    }
    fun defaultColorFor(mode: Mode): Int = when(mode) {
        Mode.IONIAN -> 0xff1a9100.toInt()
        Mode.DORIAN -> 0xff0777ff.toInt()
        Mode.PHRYGIAN -> 0xffffd100.toInt()
        Mode.LYDIAN -> 0xff90ffdc.toInt()
        Mode.MIXOLYDIAN -> 0xff5d00aa.toInt()
        Mode.AEOLIAN -> 0xff900007.toInt()
        Mode.LOCRIAN ->0xff98c700.toInt()
    }
}

