package com.example.musicalgames.music_model

import com.example.musicalgames.music_model.display.SpellingPreference
import org.junit.Assert.*
import org.junit.Test

class KeySignatureTest {

    @Test
    fun `sharps must be within -7 to 7`() {
        KeySignature(7)
        KeySignature(-7)
        assertThrows(IllegalArgumentException::class.java) { KeySignature(8) }
        assertThrows(IllegalArgumentException::class.java) { KeySignature(-8) }
    }

    @Test
    fun `notes follow the standard sharp and flat orders`() {
        assertEquals(emptyList<ChromaticNote>(), KeySignature(0).notes)
        assertEquals(listOf(ChromaticNote.FxG, ChromaticNote.CxD), KeySignature(2).notes)
        assertEquals(listOf(ChromaticNote.AxB), KeySignature(-1).notes)
        assertEquals(Accidental.SHARP, KeySignature(0).accidental)
        assertEquals(Accidental.SHARP, KeySignature(2).accidental)
        assertEquals(Accidental.FLAT, KeySignature(-1).accidental)
    }

    @Test
    fun `forKey resolves major keys to their standard signatures`() {
        assertEquals(KeySignature(0), KeySignature.forKey(ChromaticNote.C, Mode.IONIAN))
        assertEquals(KeySignature(1), KeySignature.forKey(ChromaticNote.G, Mode.IONIAN))
        assertEquals(KeySignature(2), KeySignature.forKey(ChromaticNote.D, Mode.IONIAN))
        assertEquals(KeySignature(-1), KeySignature.forKey(ChromaticNote.F, Mode.IONIAN))
    }

    @Test
    fun `forKey resolves modes via their parent major scale`() {
        // D dorian, A aeolian, E phrygian and B locrian all share C major's key signature
        assertEquals(KeySignature(0), KeySignature.forKey(ChromaticNote.D, Mode.DORIAN))
        assertEquals(KeySignature(0), KeySignature.forKey(ChromaticNote.A, Mode.AEOLIAN))
        assertEquals(KeySignature(0), KeySignature.forKey(ChromaticNote.E, Mode.PHRYGIAN))
        assertEquals(KeySignature(0), KeySignature.forKey(ChromaticNote.B, Mode.LOCRIAN))
    }

    @Test
    fun `forKey uses spelling preference for enharmonically ambiguous keys`() {
        // F#/Gb major: tied at 6 accidentals either way
        assertEquals(KeySignature(6), KeySignature.forKey(ChromaticNote.FxG, Mode.IONIAN, SpellingPreference.SHARPS))
        assertEquals(KeySignature(-6), KeySignature.forKey(ChromaticNote.FxG, Mode.IONIAN, SpellingPreference.FLATS))
        assertEquals(KeySignature(6), KeySignature.forKey(ChromaticNote.FxG, Mode.IONIAN, SpellingPreference.MIXED))

        // C#/Db major: Db (5 flats) has fewer accidentals than C# (7 sharps)
        assertEquals(KeySignature(7), KeySignature.forKey(ChromaticNote.CxD, Mode.IONIAN, SpellingPreference.SHARPS))
        assertEquals(KeySignature(-5), KeySignature.forKey(ChromaticNote.CxD, Mode.IONIAN, SpellingPreference.FLATS))
        assertEquals(KeySignature(-5), KeySignature.forKey(ChromaticNote.CxD, Mode.IONIAN, SpellingPreference.MIXED))

        // B/Cb major: B (5 sharps) has fewer accidentals than Cb (7 flats)
        assertEquals(KeySignature(5), KeySignature.forKey(ChromaticNote.B, Mode.IONIAN, SpellingPreference.SHARPS))
        assertEquals(KeySignature(-7), KeySignature.forKey(ChromaticNote.B, Mode.IONIAN, SpellingPreference.FLATS))
        assertEquals(KeySignature(5), KeySignature.forKey(ChromaticNote.B, Mode.IONIAN, SpellingPreference.MIXED))
    }

    @Test
    fun `forKey is unaffected by preference outside the ambiguous range`() {
        for (preference in SpellingPreference.entries) {
            assertEquals(KeySignature(0), KeySignature.forKey(ChromaticNote.C, Mode.IONIAN, preference))
            assertEquals(KeySignature(1), KeySignature.forKey(ChromaticNote.G, Mode.IONIAN, preference))
            assertEquals(KeySignature(-1), KeySignature.forKey(ChromaticNote.F, Mode.IONIAN, preference))
        }
    }
}
