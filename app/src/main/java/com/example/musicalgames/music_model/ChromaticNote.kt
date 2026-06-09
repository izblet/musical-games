package com.example.musicalgames.music_model

enum class ChromaticNote {
    C, CxD, D, DxE, E, F, FxG, G, GxA, A, AxB, B;

    fun isDiatonic(): Boolean {
        return !this.name.contains('x')
    }
    fun transpose(interval: Interval) : ChromaticNote {
        val semitones = this.ordinal + interval.getSemitones()
        return ChromaticNote.fromDegree(semitones%values.size)
    }
    companion object {
        private val values = values()
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
