package com.example.musicalgames.games.mental_intervals

import com.example.musicalgames.game_activity.Level

object MentalLevels {
    val baseLevels: List<MentalLevel> = generateIntervalLevels()
    val noteLevels: List<MentalLevel> = generateNoteLevels()
    private fun generateIntervalLevels(): List<MentalLevel> {
        val levels = mutableListOf<MentalLevel>()
        //the list is like this 'cause it will be changed
        for (maxSemitones in listOf(2,3,4,5,6,7,8,9,10,11,12)) {
            levels.add(
                MentalLevel(
                    0,
                    maxSemitones,
                    true,
                    "At most $maxSemitones semitones", ""
                )
            )

        }
        return levels
    }
    private fun generateNoteLevels(): List<MentalLevel> {
        val levels = mutableListOf<MentalLevel>()
        //the list is like this 'cause it will be changed
        for (maxSemitones in listOf(2,3,4,5,6,7,8,9,10,11,12)) {
            levels.add(
                MentalLevel(
                    0,
                    maxSemitones,
                    false,
                    "At most $maxSemitones semitones", ""
                )
            )

        }
        return levels
    }

}

data class MentalLevel (
    override val id: Int,
    val maxSemitoneInterval: Int,
    val intervalToNote: Boolean,
    override val name: String,
    override val description: String
): Level(id, name, description)
