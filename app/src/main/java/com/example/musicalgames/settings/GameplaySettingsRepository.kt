package com.example.musicalgames.settings

import android.content.Context
import com.example.musicalgames.game.game_core.GamePlayInstance
import com.example.musicalgames.game.game_core.GameplayType
import com.example.musicalgames.game.game_core.InputMethod
import com.example.musicalgames.game.game_core.keyEstablishOption
import com.example.musicalgames.games.Game
import androidx.core.content.edit

class GameplaySettingsRepository(context: Context) {
    private val prefs = context.getSharedPreferences(SettingsStorage.PREFS_NAME, Context.MODE_PRIVATE)

    fun get(game: Game): GamePlayInstance {
        val defaults = GamePlayInstance()
        val bpm = prefs.getInt(keyFor(game, "bpm"), defaults.bpm)
            .takeIf { it in GamePlayInstance.getMinBpmValue()..GamePlayInstance.getMaxBpmValue() }
            ?: defaults.bpm
        return GamePlayInstance(
            type = prefs.getString(keyFor(game, "type"), null)
                ?.let { runCatching { GameplayType.valueOf(it) }.getOrNull() } ?: defaults.type,
            bpm = bpm,
            displayScore = prefs.getBoolean(keyFor(game, "display_score"), defaults.displayScore),
            inputMethod = prefs.getString(keyFor(game, "input_method"), null)
                ?.let { runCatching { InputMethod.valueOf(it) }.getOrNull() } ?: defaults.inputMethod,
            establishKeyWith = prefs.getString(keyFor(game, "establish_key_with"), null)
                ?.let { runCatching { keyEstablishOption.valueOf(it) }.getOrNull() } ?: defaults.establishKeyWith
        )
    }

    fun save(game: Game, gameplay: GamePlayInstance) {
        prefs.edit {
            putString(keyFor(game, "type"), gameplay.type.name)
                .putInt(keyFor(game, "bpm"), gameplay.bpm)
                .putBoolean(keyFor(game, "display_score"), gameplay.displayScore)
                .putString(keyFor(game, "input_method"), gameplay.inputMethod.name)
                .putString(keyFor(game, "establish_key_with"), gameplay.establishKeyWith.name)
        }
    }

    private fun keyFor(game: Game, field: String) = "gameplay_${game.name}_$field"
}
