package com.example.musicalgames.games.mental_intervals

import com.example.musicalgames.game_activity.Level


object MentalLevels {
    val intervalNoteLevels: List<MentalLevel> = generateIntervalLevels()
    val noteIntervalLevels: List<MentalLevel> = generateNoteLevels()
    //val degreeNoteLevels: List<MentalLevel>
    private fun generateIntervalLevels(): List<MentalLevel> {
        val levels = mutableListOf<MentalLevel>()
        //the list is like this 'cause it will be changed
        for (maxSemitones in listOf(2,3,4,5,6,7,8,9,10,11,12)) {
            levels.add(
                MentalLevel(
                    0,
                    maxSemitones,
                    Type.INTERVAL_NOTE,
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
                    Type.NOTE_INTERVAL,
                    "At most $maxSemitones semitones", ""
                )
            )

        }
        return levels
    }

}

enum class Type {
    INTERVAL_NOTE,
    NOTE_INTERVAL
}
data class MentalLevel (
    override val id: Int,
    val maxSemitoneInterval: Int,
    val mode: Type,
    override val name: String,
    override val description: String
): Level(id, name, description)
