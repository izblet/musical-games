package com.example.musicalgames.game.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.musicalgames.game.games.chords.level_data.ChordsLevels
import com.example.musicalgames.game.games.circle_of_fifths.level_data.CircleLevels
import com.example.musicalgames.games.flappy.FlappyLevels
import com.example.musicalgames.games.mental_intervals.MentalLevels
import com.example.musicalgames.games.play_by_ear.EarPlayLevels
import com.example.musicalgames.main_app.game_options_screen.TaggedLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [LevelEntity::class], version = 1, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {
    abstract fun levelDao(): LevelDao

    companion object {
        @Volatile
        private var INSTANCE: GameDatabase? = null

        fun getInstance(context: Context): GameDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "game_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(populateCallback)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val populateCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                CoroutineScope(Dispatchers.IO).launch {
                    val levels = prepopulateLevels()

                    INSTANCE?.levelDao()?.addAllLevelEntities(levels)

                }
            }

        }

        private fun prepopulateLevels(): List<LevelEntity> {
            val flappyLevels = mapToEntities(FlappyLevels.baseLevels)
            val mentalLevels = mapToEntities(MentalLevels.intervalNoteLevels)
            val earLevels = mapToEntities(EarPlayLevels.baseLevels)
            val circleLevels = mapToEntities(CircleLevels.baseLevels)
            val chordsLevels = mapToEntities(ChordsLevels.baseLevels)

            val allLevels = flappyLevels + mentalLevels + earLevels + circleLevels + chordsLevels
            Log.d("Database", "Generated prepopulateLevels: ${allLevels.size}")

            return allLevels
        }

        private fun mapToEntities(levels: List<TaggedLevel>): List<LevelEntity> {

            return levels.map {
                LevelEntity(
                    id = it.levelId,
                    gameId = it.game.ordinal,
                    name = it.name,
                    description = it.description,
                    levelJSON = MoshiUtil.adapters[it.game]!!.toJson(it.level),
                    isFavourite = it.isFavourite,
                    isCustom = it.isCustom
                )
            }
        }
    }
}