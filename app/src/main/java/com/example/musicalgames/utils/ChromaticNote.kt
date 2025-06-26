package com.example.musicalgames.utils

enum class ChromaticNote {
    C, CxD, D, DxE, E, F, FxG, G, GxA, A, AxB, B;

    //the creation and handling of the note classes is abhorrent
    //this is a temporary fix because I don't have time and I need it right now
    //ideally this should be the behaviour of the default toString function, but we should also provide "toFlatString" and "toSharpString"
    //consider adding something like "scalenote" that takes into account whether a note is flat or sharp
    //in musicutil remove all functions that use strings and don't have to
    //consider operating only on midicodes if you want to be fast (providing only conversions from everything to midicode and from midicode to everything)
    fun isFlat() : Boolean {
        return this in flatNotes
    }
    fun tmpToString(): String {
        if (isFlat()) {
           return this.name[2]+"b"
        }
        else return toString()
    }

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
        return ChromaticNote.fromDegree(semitones%values.size)
    }
    companion object {
        private val flatNotes = setOf(DxE,AxB,GxA)
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