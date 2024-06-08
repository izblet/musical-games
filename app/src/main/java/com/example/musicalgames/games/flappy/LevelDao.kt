package com.example.musicalgames.games.flappy

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

@Entity(tableName = "flappy_levels")
data class Level(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val minPitch: String,
    val maxPitch: String,
    val name: String
)
@Dao
interface LevelDao {
    @Insert
    suspend fun insert(level: Level)
    @Update
    suspend fun update(level: Level)
    @Query("SELECT * FROM flappy_levels ORDER BY id DESC")
    suspend fun getLevels(): Level?
}