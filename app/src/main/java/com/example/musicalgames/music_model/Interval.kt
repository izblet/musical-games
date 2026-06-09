package com.example.musicalgames.music_model

enum class Interval {
    P1,
    m2,
    M2,
    m3,
    M3,
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
            return entries[semitones%13]
        }
        fun fromChromaticNotes(firstNote: ChromaticNote, secondNote: ChromaticNote) : Interval {
            val semitones = (12+secondNote.ordinal-firstNote.ordinal)%12
            return fromSemitones(semitones)
        }
    }

}