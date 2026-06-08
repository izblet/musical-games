package com.example.musicalgames.games

import com.example.musicalgames.R
import com.example.musicalgames.game.game_core.GameFactory
import com.example.musicalgames.game.game_core.GameplayOptions
import com.example.musicalgames.game.games.chords.GameFactoryChords
import com.example.musicalgames.game.games.circle_of_fifths.creation.CircleGameFactory
import com.example.musicalgames.game.games.flappy.creation.FlappyGameFactory
import com.example.musicalgames.game.games.mental_intervals.creation.MentalGameFactory
import com.example.musicalgames.game.games.play_by_ear.creation.EarGameFactory

data class GameInfo (
    val id: Int,
    val name: String,
    val description: String,
    val iconResourceId: Int,
    val gameplayOptions: Set<GameplayOptions>,
    val gameFactoryProvider: ()-> GameFactory,
)

enum class Game {
    FLAPPY,
    PLAY_BY_EAR,
    MENTAL_INTERVALS,
    CIRCLE,
    CHORDS
}

object GameMap {
    val gameInfos : Map<Game, GameInfo> = Game.entries.associateWith { game ->
        when (game) {
            Game.FLAPPY ->
                GameInfo(
                    2,
                    "Flappy Bird",
                    "Flappy bird game controlled with voice",
                    R.drawable.microphone,
                    setOf(GameplayOptions.TYPE, GameplayOptions.BPM, GameplayOptions.DISPLAY_SCORE, GameplayOptions.ESTABLISH_KEY_WITH)
                ) { FlappyGameFactory() }

            Game.PLAY_BY_EAR ->
                GameInfo(
                    3,
                    "Play By Ear",
                    "Play back melodies on a piano",
                    R.drawable.ear,
                    setOf(GameplayOptions.TYPE, GameplayOptions.BPM, GameplayOptions.DISPLAY_SCORE, GameplayOptions.ESTABLISH_KEY_WITH)
                ) { EarGameFactory() }

            Game.MENTAL_INTERVALS ->
                    GameInfo(
                        4,
                        "Note Arithmetic",
                        "Practice relationships between notes",
                        R.drawable.mental,
                        setOf(GameplayOptions.TYPE, GameplayOptions.BPM, GameplayOptions.DISPLAY_SCORE)
                    ) { MentalGameFactory() }
            Game.CIRCLE ->
                    GameInfo(
                        5,
                        "Circle of Fifths",
                        "Learn the positions of scales on the circle of fifths",
                        R.drawable.clock_icon,
                        setOf(GameplayOptions.TYPE, GameplayOptions.BPM, GameplayOptions.DISPLAY_SCORE)
                    ) { CircleGameFactory() }
            Game.CHORDS ->
                GameInfo(
                    6,
                    "Chords",
                    "Learn the notes for chords",
                    R.drawable.default_game_icon,
                    setOf(GameplayOptions.TYPE, GameplayOptions.BPM, GameplayOptions.DISPLAY_SCORE)
                ){ GameFactoryChords() }
        }
    }

    val gameplayOptions: Map<Game, Set<GameplayOptions>> = gameInfos.mapValues { it.value.gameplayOptions }
}

