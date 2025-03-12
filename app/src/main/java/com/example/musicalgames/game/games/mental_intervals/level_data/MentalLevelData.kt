package com.example.musicalgames.games.mental_intervals

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import com.example.musicalgames.game.games.mental_intervals.MentalLevel
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.flappy.DELIMITER
import com.example.musicalgames.utils.ChromaticNote
import com.example.musicalgames.utils.DiatonicNote
import com.example.musicalgames.utils.Interval


object MentalLevels {
    val intervalNoteLevels: List<MentalLevel> = generateIntervalLevels()
    private fun generateIntervalLevels() : List<MentalLevel> {
        val levels = mutableListOf<MentalLevel>()
        val intervals = mutableListOf<Interval>()
        for(maxSemitones in listOf(1,2,3,4,5,6,7,8,9,10,11)) {
            intervals.add(Interval.fromSemitones(maxSemitones))
            levels.add(
                MentalLevel(
                    0,
                    DiatonicNote.values().map { diatonicNote -> diatonicNote.chromaticNote  },
                    intervals.toList(),
                    Type.INTERVAL_NOTE,
                    "at most $maxSemitones semitones",
                    ""
                )
            )
        }
        return levels
    }
    /*val noteIntervalLevels: List<MentalLevel> = generateNoteLevels()
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
    */
}

enum class Type {
    INTERVAL_NOTE,
    NOTE_INTERVAL,
    DEGREE_NOTE
}
