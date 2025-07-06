package com.example.musicalgames.games

import com.example.musicalgames.R
import com.example.musicalgames.game.game_core.GameFactory
import com.example.musicalgames.game.game_core.GameplayOptions
import com.example.musicalgames.game.games.circle_of_fifths.creation.CircleGameFactory
import com.example.musicalgames.game.games.flappy.creation.FlappyGameFactory
import com.example.musicalgames.game.games.mental_intervals.creation.MentalGameFactory
import com.example.musicalgames.game.games.play_by_ear.creation.EarGameFactory

data class GameInfo (
    val id: Int,
    val name: String,
    val description: String,
    val iconResourceId: Int,
    val gameFactoryProvider: ()-> GameFactory,
)

enum class Game {
    FLAPPY,
    PLAY_BY_EAR,
    MENTAL_INTERVALS,
    CIRCLE
}

object GameMap {
    val gameFactories : Map<Game, GameFactory> = Game.entries.associateWith { game->
        when(game) {
            Game.FLAPPY -> FlappyGameFactory()
            Game.PLAY_BY_EAR -> EarGameFactory()
            Game.MENTAL_INTERVALS -> MentalGameFactory()
            Game.CIRCLE -> CircleGameFactory()
        }

    }
    val gameplayOptions: Map<Game, Set<GameplayOptions>> = Game.entries.associateWith { game->
        when(game) {
            Game.FLAPPY -> setOf(GameplayOptions.TYPE, GameplayOptions.BPM, GameplayOptions.DISPLAY_SCORE, GameplayOptions.ESTABLISH_KEY_WITH)
            Game.PLAY_BY_EAR -> setOf(GameplayOptions.TYPE, GameplayOptions.BPM, GameplayOptions.DISPLAY_SCORE, GameplayOptions.ESTABLISH_KEY_WITH)
            Game.MENTAL_INTERVALS -> setOf(GameplayOptions.TYPE, GameplayOptions.BPM, GameplayOptions.DISPLAY_SCORE, GameplayOptions.ESTABLISH_KEY_WITH)
            Game.CIRCLE -> setOf(GameplayOptions.TYPE, GameplayOptions.BPM, GameplayOptions.DISPLAY_SCORE, GameplayOptions.ESTABLISH_KEY_WITH)
        }
    }
    val gameInfos : Map<Game, GameInfo> = Game.entries.associateWith { game ->
        when (game) {
            Game.FLAPPY ->
                GameInfo(
                    2,
                    "Flappy Bird",
                    "Flappy bird game controlled with voice",
                    R.drawable.microphone
                ) { FlappyGameFactory() }

            Game.PLAY_BY_EAR ->
                GameInfo(
                    3,
                    "Play By Ear",
                    "Play back melodies on a piano",
                    R.drawable.ear
                ) { EarGameFactory() }

            Game.MENTAL_INTERVALS ->
                    GameInfo(
                        4,
                        "Note Arithmetic",
                        "Practice relationships between notes",
                        R.drawable.mental
                    ) { MentalGameFactory() }
            Game.CIRCLE ->
                    GameInfo(
                        5,
                        "Circle of Fifths",
                        "Learn the positions of scales on the circle of fifths",
                        R.drawable.clock_icon
                    ) { CircleGameFactory() }
        }
    }
}

