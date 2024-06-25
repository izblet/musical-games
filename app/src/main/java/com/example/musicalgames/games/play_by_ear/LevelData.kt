package com.example.musicalgames.games.play_by_ear

import android.util.Log
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.utils.MusicUtil.frequency
import com.example.musicalgames.utils.MusicUtil.midi
import com.example.musicalgames.utils.MusicUtil.noteName

object EarPlayLevels {
    val baseLevels: List<PlayEarLevel> = generateLevels()
    private fun generateLevels(): List<PlayEarLevel> {
        return listOf(
            PlayEarLevel(
            1,
                midi("C4"),
                midi("C5"),
                midi("C4"),
                3,
                5,
               listOf(midi("C4"), midi("D4"), midi("E4")),
                "C major, C D E, 3 notes",
                ""
            )
        )
    }

}

data class PlayEarLevel (
    override val id: Int,
    val minPitch: Int,
    val maxPitch: Int,
    val root: Int,
    val notesNum: Int,
    val maxSemitoneInterval: Int,
    val keyList: List<Int>,
    override val name: String,
    override val description: String,
): Level(id, name, description)
