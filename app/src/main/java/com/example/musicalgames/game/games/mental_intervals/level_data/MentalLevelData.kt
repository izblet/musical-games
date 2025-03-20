package com.example.musicalgames.games.mental_intervals

import com.example.musicalgames.game.games.mental_intervals.MentalLevel
import com.example.musicalgames.games.Game
import com.example.musicalgames.main_app.game_levels.TaggedLevel
import com.example.musicalgames.utils.DiatonicNote
import com.example.musicalgames.utils.Interval


object MentalLevels {
    val intervalNoteLevels: List<TaggedLevel> = generateIntervalLevels()
    private fun generateIntervalLevels() : List<TaggedLevel> {
        val levels = mutableListOf<TaggedLevel>()
        val intervals = mutableListOf<Interval>()
        for(maxSemitones in listOf(1,2,3,4,5,6,7,8,9,10,11)) {
            intervals.add(Interval.fromSemitones(maxSemitones))
            val level = MentalLevel(
                startingNotes = DiatonicNote.values().map { diatonicNote -> diatonicNote.chromaticNote  },
                intervals = intervals.toList(),
                mode = Type.INTERVAL_NOTE
            )
            levels.add(TaggedLevel(Game.MENTAL_INTERVALS, 0, "at most $maxSemitones semitones", "", level, isFavourite = false, isCustom = false))

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
