package com.example.musicalgames.music_model

import com.example.musicalgames.music_model.display.SpellingPreference

enum class Accidental { SHARP, FLAT }

//sharps: signed count of accidentals in the key signature - positive for sharp keys,
//negative for flat keys, 0 for no accidentals. Range is -7..7 (e.g. 7 = C# major/7 sharps,
//-7 = Cb major/7 flats); accidental/notes are derived once at construction so repeated
//access (e.g. every frame while drawing) doesn't redo the lookup
data class KeySignature(val sharps: Int) {
    init {
        require(sharps in -7..7) { "sharps must be in -7..7, was $sharps" }
    }

    val accidental: Accidental = if (sharps >= 0) Accidental.SHARP else Accidental.FLAT
    val notes: List<ChromaticNote> = if (sharps >= 0) sharpOrder.take(sharps) else flatOrder.take(-sharps)

    companion object {
        //standard order accidentals are added to a key signature
        private val sharpOrder = listOf(
            ChromaticNote.FxG, ChromaticNote.CxD, ChromaticNote.GxA, ChromaticNote.DxE,
            ChromaticNote.AxB, ChromaticNote.F, ChromaticNote.C
        )
        private val flatOrder = listOf(
            ChromaticNote.AxB, ChromaticNote.DxE, ChromaticNote.GxA, ChromaticNote.CxD,
            ChromaticNote.FxG, ChromaticNote.B, ChromaticNote.E
        )

        //the key signature is that of `mode`'s parent major scale. B/Cb, F#/Gb and C#/Db
        //(circle-of-fifths positions 5-7) have valid representations as both sharps and flats
        //within the -7..7 range - `preference` picks between them; elsewhere the range forces
        //a unique representation and `preference` has no effect
        fun forKey(root: ChromaticNote, mode: Mode, preference: SpellingPreference = SpellingPreference.MIXED): KeySignature {
            val degreeFromParent = Scale.MAJOR.getDegrees()[mode.ordinal].getSemitones()
            val parentMajorTonic = root.transpose(Interval.fromSemitones((12 - degreeFromParent) % 12))

            val position = CircleOfFifths.indexOf(parentMajorTonic)
            val sharpCount = position
            val flatCount = position - 12

            val asSharps = when {
                sharpCount > 7 -> false
                flatCount < -7 -> true
                else -> when (preference) {
                    SpellingPreference.SHARPS -> true
                    SpellingPreference.FLATS -> false
                    SpellingPreference.MIXED -> sharpCount <= -flatCount
                }
            }
            return KeySignature(if (asSharps) sharpCount else flatCount)
        }
    }
}
