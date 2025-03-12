package com.example.musicalgames.game.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.Game
import com.squareup.moshi.Moshi

@Dao
interface LevelDao {
    @Insert
    suspend fun insert(level: LevelEntity)

    @Query("SELECT COUNT(*) FROM levels")
    suspend fun getLevelCount(): Long

    @Query("SELECT * FROM levels WHERE gameId = :gameId AND isCustom = :isCustom")
    suspend fun getLevelEntities(gameId: Int, isCustom: Boolean): List<LevelEntity>

    @Query("SELECT * FROM levels WHERE gameID = :gameId AND isFavourite = 1")
    suspend fun getFavouriteLevelEntities(gameId: Int) : List<LevelEntity>

    suspend fun addAllLevelEntities(entities: List<LevelEntity>) {
        entities.forEach{level -> insert(level)}
    }

    suspend fun getLevels(game: Game, isCustom: Boolean) : List<Level> {
        return getLevelEntities(game.ordinal, isCustom).map{
            MoshiUtil.adapters[game]!!.fromJson(it.levelJSON) as Level
        }
    }
    suspend fun getFavourites(game: Game) : List<Level> {
        return getFavouriteLevelEntities(game.ordinal).map{
            MoshiUtil.adapters[game]!!.fromJson(it.levelJSON) as Level
        }

    }

    suspend fun addLevel(level: Level, game: Game, isCustom: Boolean) {
        val levelEntity = LevelEntity(0,game.ordinal, MoshiUtil.adapters[game]!!.toJson(level), false, isCustom)
        insert(levelEntity)
    }

}