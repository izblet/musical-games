package com.example.musicalgames.games

import android.os.Parcelable
import com.example.musicalgames.R
import kotlinx.parcelize.Parcelize

enum class GameOption {
    HIGH_SCORES,
    ARCADE,
    LEVELS,
    INVERTED,
    GAME_CREATE,
    GAME_JOIN;
    fun getString(): String {
        return when(this) {
            GAME_JOIN -> "Join Game"
            GAME_CREATE -> "Create Game"
            HIGH_SCORES -> "High Scores"
            LEVELS -> "Levels"
            INVERTED -> "Inverted"
            ARCADE -> "Arcade"
        }
    }
}
enum class Game {
    FLAPPY,
    CHASE,
    PLAY_BY_EAR,
    MENTAL_INTERVALS,
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
                listOf(GameOption.GAME_CREATE, GameOption.GAME_JOIN)),
        Game.FLAPPY to
            GameInfo(
                2,
                "Flappy Bird",
                "Flappy bird game controlled with voice",
                R.drawable.microphone,
                listOf(GameOption.LEVELS)
            ),
        Game.PLAY_BY_EAR to
            GameInfo(3,
                "Play By Ear",
                "Play back melodies on a piano",
                R.drawable.ear,
                listOf(GameOption.LEVELS)
            ),
        Game.MENTAL_INTERVALS to
            GameInfo(4,
                "Mental Intervals",
                "Find intervals in your head",
                R.drawable.mental,
                listOf(GameOption.LEVELS, GameOption.INVERTED)
            )

    )
}

