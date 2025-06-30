package com.example.musicalgames.game.games.circle_of_fifths

import com.example.musicalgames.game_activity.Level
import kotlinx.parcelize.Parcelize

@Parcelize
data class CircleLevel(
    val positionToName: Boolean,
    val minor: Boolean,
    val major: Boolean
) : Level()