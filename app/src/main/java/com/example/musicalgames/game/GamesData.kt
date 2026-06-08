package com.example.musicalgames.games

import com.example.musicalgames.R
import com.example.musicalgames.game.game_core.GameFactory
import com.example.musicalgames.game.game_core.GameplayOptions
import com.example.musicalgames.game.games.chords.GameFactoryChords
import com.example.musicalgames.game.games.chords.LevelChords
import com.example.musicalgames.game.games.circle_of_fifths.CircleLevel
import com.example.musicalgames.game.games.circle_of_fifths.creation.CircleGameFactory
import com.example.musicalgames.game.games.flappy.FlappyLevel
import com.example.musicalgames.game.games.flappy.creation.FlappyGameFactory
import com.example.musicalgames.game.games.mental_intervals.MentalLevel
import com.example.musicalgames.game.games.mental_intervals.creation.MentalGameFactory
import com.example.musicalgames.game.games.play_by_ear.PlayEarLevel
import com.example.musicalgames.game.games.play_by_ear.creation.EarGameFactory
import com.example.musicalgames.game_activity.Level
import kotlin.reflect.KClass

data class GameInfo (
    val id: Int,
    val name: String,
    val description: String,
    val iconResourceId: Int,
)

data class GameSerialization(
    val levelClass: KClass<out Level>,
    val levelTypeTag: String,
)

data class GameRegistration(
    val game: Game,
    val info: GameInfo,
    val gameplayOptions: Set<GameplayOptions>,
    val factoryProvider: () -> GameFactory,
    val serialization: GameSerialization,
)

enum class Game {
    FLAPPY,
    PLAY_BY_EAR,
    MENTAL_INTERVALS,
    CIRCLE,
    CHORDS
}

object GameMap {
    // The single place a new game gets registered: everything about a game lives in
    // one GameRegistration entry, and every map/lookup below is derived from this list.
    val registrations: List<GameRegistration> = listOf(
        GameRegistration(
            game = Game.FLAPPY,
            info = GameInfo(2, "Flappy Bird", "Flappy bird game controlled with voice", R.drawable.microphone),
            gameplayOptions = setOf(GameplayOptions.TYPE, GameplayOptions.BPM, GameplayOptions.DISPLAY_SCORE, GameplayOptions.ESTABLISH_KEY_WITH),
            factoryProvider = { FlappyGameFactory() },
            serialization = GameSerialization(FlappyLevel::class, "flappy"),
        ),
        GameRegistration(
            game = Game.PLAY_BY_EAR,
            info = GameInfo(3, "Play By Ear", "Play back melodies on a piano", R.drawable.ear),
            gameplayOptions = setOf(GameplayOptions.TYPE, GameplayOptions.BPM, GameplayOptions.DISPLAY_SCORE, GameplayOptions.ESTABLISH_KEY_WITH),
            factoryProvider = { EarGameFactory() },
            serialization = GameSerialization(PlayEarLevel::class, "ear"),
        ),
        GameRegistration(
            game = Game.MENTAL_INTERVALS,
            info = GameInfo(4, "Note Arithmetic", "Practice relationships between notes", R.drawable.mental),
            gameplayOptions = setOf(GameplayOptions.TYPE, GameplayOptions.BPM, GameplayOptions.DISPLAY_SCORE),
            factoryProvider = { MentalGameFactory() },
            serialization = GameSerialization(MentalLevel::class, "mental"),
        ),
        GameRegistration(
            game = Game.CIRCLE,
            info = GameInfo(5, "Circle of Fifths", "Learn the positions of scales on the circle of fifths", R.drawable.clock_icon),
            gameplayOptions = setOf(GameplayOptions.TYPE, GameplayOptions.BPM, GameplayOptions.DISPLAY_SCORE),
            factoryProvider = { CircleGameFactory() },
            serialization = GameSerialization(CircleLevel::class, "circle"),
        ),
        GameRegistration(
            game = Game.CHORDS,
            info = GameInfo(6, "Chords", "Learn the notes for chords", R.drawable.default_game_icon),
            gameplayOptions = setOf(GameplayOptions.TYPE, GameplayOptions.BPM, GameplayOptions.DISPLAY_SCORE),
            factoryProvider = { GameFactoryChords() },
            serialization = GameSerialization(LevelChords::class, "chords"),
        ),
    )

    private val byGame: Map<Game, GameRegistration> = registrations.associateBy { it.game }

    init {
        check(byGame.keys == Game.entries.toSet()) {
            "GameMap.registrations is missing entries for: ${Game.entries.toSet() - byGame.keys}"
        }
    }

    val gameInfos: Map<Game, GameInfo> = byGame.mapValues { it.value.info }
    val gameplayOptions: Map<Game, Set<GameplayOptions>> = byGame.mapValues { it.value.gameplayOptions }

    fun createFactory(game: Game): GameFactory = byGame.getValue(game).factoryProvider()
}

