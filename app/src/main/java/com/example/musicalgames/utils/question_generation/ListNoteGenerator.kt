package com.example.musicalgames.utils.question_generation

import kotlin.random.Random

class ListNoteGenerator(private val notes: List<Int>, private val random: Random = Random) : NoteGenerator {
    override fun getNoteMidi(minMidi: Int, maxMidi: Int, lastNote: Int?): Int = notes.random(random)
}
