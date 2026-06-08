package com.example.musicalgames.utils

enum class SpellingPreference { SHARPS, FLATS, MIXED }

object NoteSpelling {
    //pitch classes that read more naturally as flats absent a specific key
    //mirrors common notation practice (C#, Eb, F#, Ab, Bb)
    private val mixedFlatNotes = setOf(ChromaticNote.DxE, ChromaticNote.GxA, ChromaticNote.AxB)

    fun spell(note: ChromaticNote, preference: SpellingPreference): String {
        if (note.isDiatonic()) return note.name
        val asSharp = when (preference) {
            SpellingPreference.SHARPS -> true
            SpellingPreference.FLATS -> false
            SpellingPreference.MIXED -> note !in mixedFlatNotes
        }
        return if (asSharp) sharpSpelling(note) else flatSpelling(note)
    }

    fun spell(note: Note, preference: SpellingPreference): String =
        spell(note.noteChromatic, preference) + note.octave

    private fun sharpSpelling(note: ChromaticNote): String {
        val below = ChromaticNote.fromDegree((note.ordinal - 1 + ChromaticNote.valuesSize()) % ChromaticNote.valuesSize())
        return "${DiatonicNote.fromChromatic(below)!!.name}#"
    }

    private fun flatSpelling(note: ChromaticNote): String {
        val above = ChromaticNote.fromDegree((note.ordinal + 1) % ChromaticNote.valuesSize())
        return "${DiatonicNote.fromChromatic(above)!!.name}b"
    }
}
