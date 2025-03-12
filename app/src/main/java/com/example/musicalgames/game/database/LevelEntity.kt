package com.example.musicalgames.game.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "levels")
data class LevelEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val gameId: Int,
    val levelJSON: String,
    val isFavourite: Boolean,
    val isCustom: Boolean
)
