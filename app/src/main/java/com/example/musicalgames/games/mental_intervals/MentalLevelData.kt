package com.example.musicalgames.games.mental_intervals

import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.utils.MusicUtil
import com.example.musicalgames.utils.MusicUtil.getWhiteKeysFrom
import com.example.musicalgames.utils.MusicUtil.midi
import com.example.musicalgames.utils.MusicUtil.noteLetter
import com.example.musicalgames.utils.MusicUtil.noteName
import com.example.musicalgames.utils.MusicUtil.noteoctave

object MentalLevels {
    val baseLevels: List<MentalLevel> = generateLevels()
    private fun generateLevels(): List<MentalLevel> {
        val levels = mutableListOf<MentalLevel>()
        //the list is like this 'cause it will be changed
        for (maxSemitones in listOf(2,3,4,5,6,7,8,9,10,11,12)) {
            levels.add(
                MentalLevel(
                    0,
                    maxSemitones,
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
    override val name: String,
    override val description: String
): Level(id, name, description)
