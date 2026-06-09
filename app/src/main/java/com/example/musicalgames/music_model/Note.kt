package com.example.musicalgames.music_model

import kotlin.math.roundToInt

data class Note(val midiCode: Int) {
    val frequency: Double get() = 440.0 * Math.pow(2.0, (midiCode - 69) / 12.0)
    val octave: Int get() = midiCode / 12 - 1
    val noteChromatic: ChromaticNote get() = ChromaticNote.fromDegree(midiCode % 12)

    constructor(frequency: Double) : this((12 * (Math.log(frequency / 440.0) / Math.log(2.0)) + 69).roundToInt())
    constructor(note: ChromaticNote, octave: Int) : this((octave + 1) * 12 + note.ordinal)
    constructor(note: ChromaticNote, octave: Octave) : this(note, octave.toInt())
    constructor(note: DiatonicNote, octave: Int) : this(note.chromaticNote, octave)

    companion object {
        //the one and only string -> note parsing path: letter, optional accidental, octave
        //(e.g. "C4", "C#4", "Db4", case-insensitive) - handles both sharps and flats
        private val noteNameRegex = Regex("(?i)^([A-G])([#b]?)(-?\\d+)$")

        fun parse(name: String): Note? {
            val match = noteNameRegex.matchEntire(name.trim()) ?: return null
            val (letter, accidental, octaveText) = match.destructured
            val natural = DiatonicNote.valueOf(letter.uppercase()).chromaticNote
            val accidentalShift = when (accidental) {
                "#" -> 1
                "b" -> -1
                else -> 0
            }
            val size = ChromaticNote.valuesSize()
            val degree = (natural.ordinal + accidentalShift + size) % size
            return Note(ChromaticNote.fromDegree(degree), octaveText.toInt())
        }
    }
}
