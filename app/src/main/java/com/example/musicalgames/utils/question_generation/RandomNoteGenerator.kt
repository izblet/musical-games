package com.example.musicalgames.utils.question_generation

import kotlin.random.Random

class RandomNoteGenerator(private val random: Random) : NoteGenerator {
    constructor(seed: Long) : this(Random(seed))
    constructor() : this(Random.Default)

    override fun getNoteMidi(minMidi: Int, maxMidi: Int, lastNote: Int?): Int {
        return random.nextInt(minMidi, maxMidi + 1)
    }
}
