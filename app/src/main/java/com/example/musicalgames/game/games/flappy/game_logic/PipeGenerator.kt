package com.example.musicalgames.game.games.flappy.game_logic

import com.example.musicalgames.utils.question_generation.NoteGenerator
import kotlin.random.Random

class PipeGenerator(val width: Double, val minHoleMidi: Int, val maxHoleMidi: Int, private val noteGenerator: NoteGenerator) {

    fun getPipe(left: Double, bottom: Double, top: Double, minDisplayMidi: Int, maxDisplayMidi: Int) : Pipe {
        val holeMidi = noteGenerator.getNoteMidi(minHoleMidi, maxHoleMidi)
        return Pipe(
            top = top,
            bottom = bottom,
            left = left,
            right = left + width,
            minMidi = minDisplayMidi,
            maxMidi = maxDisplayMidi,
            holeMidi = holeMidi
        )
    }
}