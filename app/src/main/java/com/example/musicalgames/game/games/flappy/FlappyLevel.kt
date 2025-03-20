package com.example.musicalgames.game.games.flappy

import com.example.musicalgames.game_activity.Level
import kotlinx.parcelize.Parcelize

@Parcelize
data class FlappyLevel (
    val minPitch: Int,
    val maxPitch: Int,
    val root: Int,
    val keyList: List<Int>,
    val endAfter: Int
): Level()