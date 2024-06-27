package com.example.musicalgames.utils

enum class DiatonicNote (val chromaticNote : ChromaticNote) {
    C(ChromaticNote.C),
    D(ChromaticNote.D),
    E(ChromaticNote.E),
    F(ChromaticNote.F),
    G(ChromaticNote.G),
    A(ChromaticNote.A),
    B(ChromaticNote.B);

    fun chromaticAbove() : ChromaticNote {
        val nextIndex = chromaticNote.ordinal+1

        return ChromaticNote.fromDegree(nextIndex%ChromaticNote.valuesSize())
    }
    companion object {
        private val values = values()
        fun fromChromatic(chromaticNote: ChromaticNote): DiatonicNote? {
            return values.find { it.chromaticNote == chromaticNote }
        }
        fun fromDegree(deg: Int):DiatonicNote {
            if(deg<0 || deg> values.size)
                throw Exception("Diatonic note: wrong degree")
            return values[deg]
        }
        fun valuesSize(): Int {
            return values.size
        }
    }
}