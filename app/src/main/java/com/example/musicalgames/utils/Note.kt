package com.example.musicalgames.utils
import com.example.musicalgames.utils.MusicUtil.midi
import com.example.musicalgames.utils.MusicUtil.noteLetter
import com.example.musicalgames.utils.MusicUtil as MU
data class Note (val name: String, val frequency: Double, val midiCode: Int, val noteChromatic: ChromaticNote, val octave: Int) {
    companion object {
        fun enumName(noteString: String): String {
            val letter = noteString[0].toString()
            if(noteString.contains("b")) {
                val prevLetter = noteLetter(midi(noteString)-1)
                return prevLetter+'x'+letter
            }
            else if(noteString.contains("#")) {
                val nextLetter = noteLetter(midi(noteString)+1)
                return letter+'x'+nextLetter
            }
            return letter
        }

    }
    constructor(note: DiatonicNote, octave: Int) :
            this(note.toString()+octave)
    constructor(frequency: Double) : this(midi(frequency))
    constructor(midi: Int) :
            this(MU.noteName(midi),
                MU.frequency(midi),
                midi,
                ChromaticNote.valueOf(enumName(MU.noteName(midi))),
                MU.noteoctave(MU.noteName(midi))
                )
    constructor(note: String) : this(midi(note))
}