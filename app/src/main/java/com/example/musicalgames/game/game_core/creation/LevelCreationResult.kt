package com.example.musicalgames.game.game_core.creation

sealed class LevelCreationResult {
    data class Success(val level: Level) : LevelCreationResult()
    data class Failure(val reason: String) : LevelCreationResult()
}
