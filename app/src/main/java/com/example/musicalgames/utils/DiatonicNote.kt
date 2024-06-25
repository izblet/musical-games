package com.example.musicalgames.utils

enum class DiatonicNote (val chromaticNote : ChromaticNote) {
    C(ChromaticNote.C),
    D(ChromaticNote.D),
    E(ChromaticNote.E),
    F(ChromaticNote.F),
    G(ChromaticNote.G),
    A(ChromaticNote.A),
    B(ChromaticNote.B);

    companion object {
        fun fromChromatic(chromaticNote: ChromaticNote): DiatonicNote? {
            return values().find { it.chromaticNote == chromaticNote }
        }
    }
}