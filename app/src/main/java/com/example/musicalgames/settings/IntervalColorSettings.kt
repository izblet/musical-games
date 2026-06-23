package com.example.musicalgames.settings

import com.example.musicalgames.music_model.Interval

/** Per-interval colour coding, used to tint interval names in the Mental Intervals game. */
data class IntervalColorSettings(val colors: Map<Interval, Int> = DEFAULT_COLORS) {
    companion object {
        // matches the colours MentalView.kt's Interval.findColour() has always used
        val DEFAULT_COLORS: Map<Interval, Int> = mapOf(
            Interval.P1 to 0xffffffff.toInt(),
            Interval.m2 to 0xff00f5f8.toInt(),
            Interval.M2 to 0xff04acff.toInt(),
            Interval.m3 to 0xffe68400.toInt(),
            Interval.M3 to 0xffffef00.toInt(),
            Interval.P4 to 0xff76bbe7.toInt(),
            Interval.TT to 0xffdcfb00.toInt(),
            Interval.P5 to 0xff5ac400.toInt(),
            Interval.m6 to 0xffd00051.toInt(),
            Interval.M6 to 0xffb50c00.toInt(),
            Interval.m7 to 0xff814eae.toInt(),
            Interval.M7 to 0xfffc00c2.toInt(),
            Interval.P8 to 0xffffffff.toInt()
        )
    }
}
