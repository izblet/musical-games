package com.example.musicalgames.game.games.flappy

import com.example.musicalgames.game_activity.Level
import kotlinx.parcelize.Parcelize

@Parcelize
data class FlappyLevel (
    override val id: Int,
    val minPitch: Int,
    val maxPitch: Int,
    val root: Int,
    val keyList: List<Int>,
    override val name: String,
    override val description: String,
    val endAfter: Int
): Level(id, name, description)