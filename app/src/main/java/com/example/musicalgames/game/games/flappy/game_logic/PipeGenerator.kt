package com.example.musicalgames.game.games.flappy.game_logic

import com.example.musicalgames.utils.question_generation.NoteGenerator
import kotlin.random.Random

class PipeGenerator(val width: Double, val minHoleMidi: Int, val maxHoleMidi: Int, private val noteGenerator: NoteGenerator) {

    fun getPipe(left: Double, bottomMidiCoordinate: Double, topMidiCoordinate: Double) : Pipe {
        val holeMidi = noteGenerator.getNoteMidi(minHoleMidi, maxHoleMidi)
        return Pipe(
            topMidiCoordinate = topMidiCoordinate,
            bottomMidiCoordinate = bottomMidiCoordinate,
            left = left,
            right = left + width,
            holeMidi = holeMidi
        )
    }
}