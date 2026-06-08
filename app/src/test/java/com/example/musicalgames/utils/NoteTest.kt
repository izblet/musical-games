package com.example.musicalgames.utils

import org.junit.Assert.*
import org.junit.Test

class NoteTest {

    @Test
    fun `parses sharps and flats to the same pitch`() {
        // the old frequency(String) regex accepted "C#4" but rejected "Db4" - both must work and agree
        assertEquals(Note.parse("C#4"), Note.parse("Db4"))
        assertEquals(Note.parse("D#4"), Note.parse("Eb4"))
        assertEquals(Note.parse("A#3"), Note.parse("Bb3"))
    }

    @Test
    fun `parses naturals`() {
        assertEquals(60, Note.parse("C4")!!.midiCode)
        assertEquals(69, Note.parse("A4")!!.midiCode)
        assertEquals(59, Note.parse("B3")!!.midiCode)
    }

    @Test
    fun `is case-insensitive on the letter`() {
        assertEquals(Note.parse("C4"), Note.parse("c4"))
        assertEquals(Note.parse("F#5"), Note.parse("f#5"))
    }

    @Test
    fun `rejects malformed input`() {
        assertNull(Note.parse("H4"))     // not a valid letter
        assertNull(Note.parse("C"))      // missing octave
        assertNull(Note.parse("C##4"))   // double accidental, unsupported
        assertNull(Note.parse(""))
    }

    @Test
    fun `round-trips midi to chromatic note and back`() {
        for (midi in 21..108) {
            val note = Note(midi)
            val rebuilt = Note(note.noteChromatic, note.octave)
            assertEquals(midi, rebuilt.midiCode)
        }
    }

    @Test
    fun `round-trips through every spelling preference`() {
        for (midi in 21..108) {
            val note = Note(midi)
            for (preference in SpellingPreference.entries) {
                val spelled = NoteSpelling.spell(note, preference)
                val reparsed = Note.parse(spelled)
                assertEquals("spelling '$spelled' (midi $midi, $preference) should reparse to the same pitch",
                    note, reparsed)
            }
        }
    }

    @Test
    fun `chromatic note and octave derive consistently from midi`() {
        assertEquals(ChromaticNote.C, Note(60).noteChromatic)
        assertEquals(4, Note(60).octave)
        assertEquals(ChromaticNote.CxD, Note(61).noteChromatic)
        assertEquals(ChromaticNote.B, Note(59).noteChromatic)
        assertEquals(3, Note(59).octave)
    }
}
