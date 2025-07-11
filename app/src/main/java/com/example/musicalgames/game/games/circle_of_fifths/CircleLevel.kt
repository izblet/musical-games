package com.example.musicalgames.game.games.circle_of_fifths

import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.utils.DiatonicNote
import kotlinx.parcelize.Parcelize

@Parcelize
data class CircleLevel(
    val positionToName: Boolean,
    val modeRootInCMajor: DiatonicNote
) : Level()