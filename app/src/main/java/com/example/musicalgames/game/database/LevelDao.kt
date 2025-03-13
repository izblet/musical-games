package com.example.musicalgames.game.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.Game
import com.example.musicalgames.main_app.TaggedLevel

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

    private fun entityToTagged(game: Game, level: LevelEntity) : TaggedLevel {
      return TaggedLevel(
          game,
          level.id,
          MoshiUtil.adapters[game]!!.fromJson(level.levelJSON) as Level,
          isCustom = level.isCustom,
          isFavourite = level.isFavourite
      )
    }
    suspend fun getLevels(game: Game, isCustom: Boolean) : List<TaggedLevel> {
        return getLevelEntities(game.ordinal, isCustom).map{
            entityToTagged(game, it)
        }
    }
    suspend fun getFavourites(game: Game) : List<TaggedLevel> {
        return getFavouriteLevelEntities(game.ordinal).map{
            entityToTagged(game, it)
        }
    }

    suspend fun addLevel(level: Level, game: Game, isCustom: Boolean) {
        val levelEntity = LevelEntity(0,game.ordinal, MoshiUtil.adapters[game]!!.toJson(level), false, isCustom)
        insert(levelEntity)
    }

}