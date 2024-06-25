package com.example.musicalgames.utils
import com.example.musicalgames.utils.MusicUtil.midi
import com.example.musicalgames.utils.MusicUtil as MU
data class Note (val name: String, val frequency: Double, val midiCode: Int, val noteDiatonic: ChromaticNote, val octave: Int) {
    companion object {
        fun removeNumbers(noteString: String): String {
            return noteString.replace(Regex("[0-9]"), "").replace("-","")
        }

    }
    constructor(note: DiatonicNote, octave: Int) :
            this(note.toString()+octave)
    constructor(frequency: Double) : this(midi(frequency))
    constructor(midi: Int) :
            this(MU.noteName(midi),
                MU.frequency(midi),
                midi,
                ChromaticNote.valueOf(removeNumbers(MU.noteName(midi))),
                MU.noteoctave(MU.noteName(midi))
                )
    constructor(note: String) : this(midi(note))
}