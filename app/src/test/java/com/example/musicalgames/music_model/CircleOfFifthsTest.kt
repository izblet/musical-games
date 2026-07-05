package com.example.musicalgames.music_model

import org.junit.Assert.*
import org.junit.Test

class CircleOfFifthsTest {

    @Test
    fun `majorNotes starts at C and ascends by perfect fifths`() {
        assertEquals(ChromaticNote.C, CircleOfFifths.majorNotes.first())
        for (i in 0 until CircleOfFifths.majorNotes.size - 1) {
            assertEquals(
                CircleOfFifths.majorNotes[i + 1],
                CircleOfFifths.majorNotes[i].transpose(Interval.P5)
            )
        }
    }

    @Test
    fun `indexOf note round-trips with majorNotes`() {
        CircleOfFifths.majorNotes.forEachIndexed { index, note ->
            assertEquals(index, CircleOfFifths.indexOf(note))
        }
    }

    @Test
    fun `indexOf and atIndex are inverses for a given mode`() {
        val modes = listOf(Mode.IONIAN, Mode.MIXOLYDIAN, Mode.AEOLIAN)
        for (mode in modes) {
            for (note in ChromaticNote.entries) {
                val index = CircleOfFifths.indexOf(note, mode)
                assertEquals(note, CircleOfFifths.atIndex(index, mode))
            }
        }
    }

    @Test
    fun `atIndex zero is always the mode's tonic`() {
        for (mode in Mode.entries) {
            val tonic = DiatonicNote.entries[mode.ordinal].chromaticNote
            assertEquals(tonic, CircleOfFifths.atIndex(0, mode))
        }
    }
}
