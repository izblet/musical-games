package com.example.musicalgames.main_app

import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.Game

data class TaggedLevel(
    val game: Game,
    val levelId: Int,
    val level: Level,
    val isFavourite: Boolean,
    val isCustom: Boolean
)
