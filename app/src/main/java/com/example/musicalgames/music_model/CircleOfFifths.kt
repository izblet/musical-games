package com.example.musicalgames.music_model

object CircleOfFifths {
    //the 12 chromatic majorNotes in ascending-fifths order, starting from C
    val majorNotes: List<ChromaticNote> = generateSequence(ChromaticNote.C) { it.transpose(Interval.P5) }
        .take(ChromaticNote.valuesSize())
        .toList()

    fun indexOf(note: ChromaticNote): Int = majorNotes.indexOf(note)

    //position of `note` in the circle as rotated to start at the given mode's tonic
    fun indexOf(note: ChromaticNote, mode: Mode): Int {
        val tonic = DiatonicNote.entries[mode.ordinal].chromaticNote
        return (indexOf(note) - indexOf(tonic) + majorNotes.size) % majorNotes.size
    }

    fun atIndex(index: Int, mode: Mode): ChromaticNote {
        val tonic = DiatonicNote.entries[mode.ordinal].chromaticNote
        return majorNotes[(indexOf(tonic) + index) % majorNotes.size]
    }
}