package com.example.musicalgames.utils
import com.example.musicalgames.utils.MusicUtil as MU
data class Note (val name: String, val frequency: Double, val midiCode: Int) {
    constructor(frequency: Double) :
        this(MU.noteLetter(frequency), frequency, MU.midi(frequency))
    constructor(midi: Int) : this(MU.noteLetter(midi), MU.frequency(midi), midi)
    constructor(note: String) :
            this(note, MU.frequency(note), MU.midi(note))
}