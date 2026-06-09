package com.example.musicalgames.games.mental_intervals

import com.example.musicalgames.game.games.mental_intervals.MentalLevel
import com.example.musicalgames.games.Game
import com.example.musicalgames.main_app.game_levels.TaggedLevel
import com.example.musicalgames.music_model.DiatonicNote
import com.example.musicalgames.music_model.Interval


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

}

enum class Type {
    INTERVAL_NOTE,
    NOTE_INTERVAL;
    override fun toString() : String {
        return if(this == INTERVAL_NOTE) {
            "Interval to note"
        } else {
            "Note to interval"
        }
    }
}
