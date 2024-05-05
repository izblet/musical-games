package com.example.musicalgames.models
import com.example.musicalgames.utils.MusicUtil as MU
data class Note (val name: String, val frequency: Double, val midiCode: Int) {
    constructor(frequency: Double) :
        this(MU.notename(frequency), frequency, MU.midi(frequency))
    constructor(midi: Int) : this(MU.notename(midi), MU.frequency(midi), midi)
    constructor(note: String) :
            this(note, MU.frequency(note), MU.midi(note))
}