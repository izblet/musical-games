package com.example.musicalgames.utils

enum class Mode {
    IONIAN,
    DORIAN,
    PHRYGIAN,
    LYDIAN,
    MIXOLYDIAN,
    AEOLIAN,
    LOCRIAN;

    companion object {
        fun fromDiatonicNote(note: DiatonicNote): Mode {
            return Mode.entries[note.ordinal]
        }
    }
}