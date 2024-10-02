package com.example.musicalgames.games.mental_intervals

import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.utils.Scale


object MentalLevels {
    val intervalNoteLevels: List<MentalLevel> = generateIntervalLevels()
    val noteIntervalLevels: List<MentalLevel> = generateNoteLevels()
    val degreeNoteLevels: List<MentalLevel> = generateDegreeMajorLevels()+generateDegreeMinorLevels()
    private fun generateIntervalLevels(): List<MentalLevel> {
        val levels = mutableListOf<MentalLevel>()
        //the list is like this 'cause it will be changed
        for (maxSemitones in listOf(2,3,4,5,6,7,8,9,10,11)) {
            levels.add(
                MentalLevel(
                    0,
                    maxSemitones,
                    Type.INTERVAL_NOTE,
                    Scale.CHROMATIC,
                    "At most $maxSemitones semitones", ""
                )
            )

        }
        return levels
    }
    private fun generateNoteLevels(): List<MentalLevel> {
        val levels = mutableListOf<MentalLevel>()
        //the list is like this 'cause it will be changed
        for (maxSemitones in listOf(2,3,4,5,6,7,8,9,10,11)) {
            levels.add(
                MentalLevel(
                    0,
                    maxSemitones,
                    Type.NOTE_INTERVAL,
                    Scale.CHROMATIC,
                    "At most $maxSemitones semitones", ""
                )
            )

        }
        return levels
    }
    private fun generateDegreeMajorLevels(): List<MentalLevel> {
        val levels = mutableListOf<MentalLevel>()
        //the list is like this 'cause it will be changed
        val notes = Scale.MAJOR.getDegrees()
        val semitones = notes.map{interval -> interval.getSemitones() }
        for (maxSemitones in semitones.slice(1..<semitones.size)) {
            levels.add(
                MentalLevel(
                    0,
                    maxSemitones,
                    Type.DEGREE_NOTE,
                    Scale.MAJOR,
                    "Major scale, at most $maxSemitones semitones", ""
                )
            )

        }
        return levels
    }
    private fun generateDegreeMinorLevels(): List<MentalLevel> {
        val levels = mutableListOf<MentalLevel>()
        //the list is like this 'cause it will be changed
        val notes = Scale.MINOR.getDegrees()
        val semitones = notes.map{interval -> interval.getSemitones() }
        for (maxSemitones in semitones.slice(1..<semitones.size)) {
            levels.add(
                MentalLevel(
                    0,
                    maxSemitones,
                    Type.DEGREE_NOTE,
                    Scale.MINOR,
                    "Minor scale, at most $maxSemitones semitones", ""
                )
            )

        }
        return levels
    }

}

enum class Type {
    INTERVAL_NOTE,
    NOTE_INTERVAL,
    DEGREE_NOTE
}
data class MentalLevel (
    override val id: Int,
    val maxSemitoneInterval: Int,
    val mode: Type,
    val scale: Scale,
    override val name: String,
    override val description: String,
): Level(id, name, description)
