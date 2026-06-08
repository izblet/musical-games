package com.example.musicalgames.utils

import org.junit.Assert.*
import org.junit.Test

class NoteSpellingTest {

    @Test
    fun `diatonic notes spell the same regardless of preference`() {
        for (preference in SpellingPreference.entries) {
            assertEquals("C", NoteSpelling.spell(ChromaticNote.C, preference))
            assertEquals("F", NoteSpelling.spell(ChromaticNote.F, preference))
        }
    }

    @Test
    fun `sharps preference always uses sharp spellings`() {
        assertEquals("C#", NoteSpelling.spell(ChromaticNote.CxD, SpellingPreference.SHARPS))
        assertEquals("D#", NoteSpelling.spell(ChromaticNote.DxE, SpellingPreference.SHARPS))
        assertEquals("F#", NoteSpelling.spell(ChromaticNote.FxG, SpellingPreference.SHARPS))
        assertEquals("G#", NoteSpelling.spell(ChromaticNote.GxA, SpellingPreference.SHARPS))
        assertEquals("A#", NoteSpelling.spell(ChromaticNote.AxB, SpellingPreference.SHARPS))
    }

    @Test
    fun `flats preference always uses flat spellings`() {
        assertEquals("Db", NoteSpelling.spell(ChromaticNote.CxD, SpellingPreference.FLATS))
        assertEquals("Eb", NoteSpelling.spell(ChromaticNote.DxE, SpellingPreference.FLATS))
        assertEquals("Gb", NoteSpelling.spell(ChromaticNote.FxG, SpellingPreference.FLATS))
        assertEquals("Ab", NoteSpelling.spell(ChromaticNote.GxA, SpellingPreference.FLATS))
        assertEquals("Bb", NoteSpelling.spell(ChromaticNote.AxB, SpellingPreference.FLATS))
    }

    @Test
    fun `mixed preference matches the historical tmpToString choice`() {
        // this is the exact behaviour the old ChromaticNote.tmpToString() produced
        assertEquals("C#", NoteSpelling.spell(ChromaticNote.CxD, SpellingPreference.MIXED))
        assertEquals("Eb", NoteSpelling.spell(ChromaticNote.DxE, SpellingPreference.MIXED))
        assertEquals("F#", NoteSpelling.spell(ChromaticNote.FxG, SpellingPreference.MIXED))
        assertEquals("Ab", NoteSpelling.spell(ChromaticNote.GxA, SpellingPreference.MIXED))
        assertEquals("Bb", NoteSpelling.spell(ChromaticNote.AxB, SpellingPreference.MIXED))
    }

    @Test
    fun `note overload appends the octave`() {
        assertEquals("C#4", NoteSpelling.spell(Note(ChromaticNote.CxD, 4), SpellingPreference.SHARPS))
        assertEquals("Db4", NoteSpelling.spell(Note(ChromaticNote.CxD, 4), SpellingPreference.FLATS))
        assertEquals("C4", NoteSpelling.spell(Note(ChromaticNote.C, 4), SpellingPreference.FLATS))
    }
}
