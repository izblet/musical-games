package com.example.musicalgames.main_app.game_options_screen

import com.example.musicalgames.game.game_core.creation.Level
import com.example.musicalgames.games.Game

data class TaggedLevel(
    val game: Game,
    val levelId: Int,
    val name: String,
    val description: String,
    val level: Level,
    val isFavourite: Boolean,
    val isCustom: Boolean
)
