package com.example.musicalgames.main_app

import android.os.Parcelable
import com.example.musicalgames.R
import kotlinx.parcelize.Parcelize

enum class GameOption {
    HIGH_SCORES,
    ARCADE,
    LEVELS,
    GAME_CREATE,
    GAME_JOIN;
    fun getString(): String {
        return when(this) {
            GAME_JOIN -> "Join Game"
            GAME_CREATE -> "Create Game"
            HIGH_SCORES -> "High Scores"
            LEVELS -> "Levels"
            ARCADE -> "Arcade"
        }
    }
}
enum class Game {
    FLAPPY,
    CHASE
}

@Parcelize
data class GameInfo (
    val id: Int,
    val name: String,
    val description: String,
    val iconResourceId: Int,
    val options: List<GameOption>,
) : Parcelable

object GameMap {
    val gameInfos = mapOf(
        Game.CHASE to
                GameInfo(1,
                "Piano Chase",
                "Multiplayer chase game on a piano",
                R.drawable.default_game_icon,
                listOf(GameOption.HIGH_SCORES, GameOption.GAME_CREATE, GameOption.GAME_JOIN)),
        Game.FLAPPY to
            GameInfo(
                2,
                "Flappy Bird",
                "Flappy bird game controlled with voice",
                R.drawable.default_game_icon,
                listOf(GameOption.HIGH_SCORES, GameOption.ARCADE, GameOption.LEVELS)
            )
    )
}

