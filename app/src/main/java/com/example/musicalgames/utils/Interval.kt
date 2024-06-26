package com.example.musicalgames.utils

enum class Interval {
    P1,
    m2,
    M2,
    m3,
    P4,
    TT,
    P5,
    m6,
    M6,
    m7,
    M7,
    P8;
    fun getSemitones(): Int {
        return this.ordinal
    }
    companion object {
        fun fromSemitones(semitones: Int) : Interval {
            return values()[semitones%12]
        }
    }

}