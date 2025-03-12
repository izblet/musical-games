package com.example.musicalgames.games

import com.example.musicalgames.R
import com.example.musicalgames.game.game_core.GameFactory
import com.example.musicalgames.game.games.flappy.creation.FlappyGameFactory
import com.example.musicalgames.game.games.mental_intervals.creation.MentalGameFactory
import com.example.musicalgames.game.games.play_by_ear.creation.EarGameFactory
enum class Game {
    FLAPPY,
    PLAY_BY_EAR,
    MENTAL_INTERVALS,
}

data class GameInfo (
    val id: Int,
    val name: String,
    val description: String,
    val iconResourceId: Int,
    val gameFactory: GameFactory,
)

object GameMap {
    val gameInfos = mapOf(
        Game.FLAPPY to
            GameInfo(
                2,
                "Flappy Bird",
                "Flappy bird game controlled with voice",
                R.drawable.microphone,
                FlappyGameFactory()
            ),
        Game.PLAY_BY_EAR to
            GameInfo(3,
                "Play By Ear",
                "Play back melodies on a piano",
                R.drawable.ear,
                EarGameFactory()
            ),
        Game.MENTAL_INTERVALS to
            GameInfo(4,
                "Mental Practice",
                "Practice relationships between notes",
                R.drawable.mental,
                MentalGameFactory()
            )
    )
}

