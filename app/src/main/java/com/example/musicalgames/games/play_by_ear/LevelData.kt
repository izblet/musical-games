package com.example.musicalgames.games.play_by_ear

import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.utils.MusicUtil.midi

object EarPlayLevels {
    val baseLevels: List<PlayEarLevel> = generateLevels()
    private fun generateLevels(): List<PlayEarLevel> {
        return listOf(
            PlayEarLevel(
            1,
                midi("C4"),
                midi("G4"),
                midi("C4"),
                5,
               listOf(midi("C4"), midi("D4"), midi("E4")),
                "name",
                "description"
            )
        )
    }

}

data class PlayEarLevel (
    override val id: Int,
    val minPitch: Int,
    val maxPitch: Int,
    val root: Int,
    val maxSemitoneInterval: Int,
    val keyList: List<Int>,
    override val name: String,
    override val description: String,
): Level(id, name, description)
