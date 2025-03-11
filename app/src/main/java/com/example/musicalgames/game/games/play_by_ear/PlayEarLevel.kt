package com.example.musicalgames.game.games.play_by_ear

import com.example.musicalgames.game_activity.Level
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayEarLevel (
    override val id: Int,
    val minPitch: Int,
    val maxPitch: Int,
    val root: Int,
    val notesNum: Int,
    val maxSemitoneInterval: Int,
    val keyList: List<Int>,
    override val name: String,
    override val description: String
): Level(id, name, description)