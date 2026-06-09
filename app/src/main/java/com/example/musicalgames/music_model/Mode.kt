package com.example.musicalgames.music_model

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