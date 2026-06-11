package com.example.musicalgames.utils.question_generation

interface NoteGenerator {
    fun getNoteMidi(minMidi: Int, maxMidi: Int, lastNote: Int? = null): Int
}
