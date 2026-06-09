package com.example.musicalgames.music_model

//suboptimal, but now that i have an enum spinner it is quicker

enum class Octave {
    o1,
    o2,
    o3,
    o4,
    o5,
    o6,
    o7;

    override fun toString(): String {
        return this.name[1].toString()
    }
    fun toInt(): Int {
        return this.ordinal+1
    }
}