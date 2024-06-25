package com.example.musicalgames.utils

enum class ChromaticNote {
    C, CxD, D, DxE, E, F, FxG, G, GxA, A, AxB, B;

    override fun toString(): String {
        if(this.name.contains("x")) {
            return this.name[0]+"#"
        }
        return this.name

    }
    companion object {
        private val values = values()
        fun fromString(note: String) : ChromaticNote {
            if(note.contains("#")) {
                val lowerNote = ChromaticNote.valueOf(MusicUtil.noteLetter(note)).ordinal
                return values[(lowerNote+1)%values.size]

            } else if(note.contains('b')) {
                val higherNote = ChromaticNote.valueOf(MusicUtil.noteLetter(note)).ordinal
                return values[(higherNote-1+values.size)%values.size]
            }
            else return ChromaticNote.valueOf(MusicUtil.noteLetter(note))
        }
    }
}