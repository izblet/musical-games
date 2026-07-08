package com.example.musicalgames.game.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.musicalgames.game.game_core.creation.Level
import com.example.musicalgames.games.Game
import com.example.musicalgames.main_app.game_options_screen.TaggedLevel

@Dao
interface LevelDao {
    @Insert
    suspend fun insert(level: LevelEntity): Long

    @Query("SELECT COUNT(*) FROM levels")
    suspend fun getLevelCount(): Long

    @Query("SELECT * FROM levels WHERE gameId = :gameId AND isCustom = :isCustom")
    suspend fun getLevelEntities(gameId: Int, isCustom: Boolean): List<LevelEntity>

    @Query("SELECT * FROM levels WHERE gameID = :gameId AND isFavourite = 1")
    suspend fun getFavouriteLevelEntities(gameId: Int) : List<LevelEntity>

    @Query("UPDATE levels SET isFavourite = :newVal WHERE id = :id")
    suspend fun changeFavourite(newVal: Boolean, id: Int)

    @Query("UPDATE levels SET name = :name, description = :description, levelJSON = :levelJSON WHERE id = :id")
    suspend fun updateLevelEntity(id: Int, name: String, description: String, levelJSON: String)

    @Query("DELETE FROM levels WHERE id = :id")
    suspend fun deleteLevel(id: Int)

    suspend fun addAllLevelEntities(entities: List<LevelEntity>) {
        entities.forEach{level -> insert(level)}
    }

    private fun entityToTagged(game: Game, level: LevelEntity) : TaggedLevel {
      return TaggedLevel(
          game,
          level.id,
          level.name,
          level.description,
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

    suspend fun addLevel(
        level: TaggedLevel,
        game: Game
    ): Int {
        val levelEntity = LevelEntity(0,
            game.ordinal,
            level.name,
            level.description,
            MoshiUtil.adapters[game]!!.toJson(level.level),
            isCustom = level.isCustom,
            isFavourite = level.isFavourite)
        return insert(levelEntity).toInt()
    }

    suspend fun updateLevel(id: Int, game: Game, name: String, description: String, level: Level) {
        updateLevelEntity(id, name, description, MoshiUtil.adapters[game]!!.toJson(level))
    }

}