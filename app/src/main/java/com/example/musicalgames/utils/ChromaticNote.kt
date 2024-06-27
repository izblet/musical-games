package com.example.musicalgames.utils

enum class ChromaticNote {
    C, CxD, D, DxE, E, F, FxG, G, GxA, A, AxB, B;

    override fun toString(): String {
        if(this.isDiatonic()) {
            return this.name
        }
        return this.name[0]+"#"

    }
    fun isDiatonic():Boolean {
        return !this.name.contains('x')
    }
    fun transpose(interval: Interval) : ChromaticNote {
        val semitones = this.ordinal + interval.getSemitones()
        return ChromaticNote.fromDegree(semitones)
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
        fun fromDegree(deg: Int) : ChromaticNote {
            if(deg<0 || deg>=values.size)
                throw Exception("ChromaticNote: degree value is impossible")
            return values[deg]
        }
        fun valuesSize(): Int {
            return values.size
        }
    }
}