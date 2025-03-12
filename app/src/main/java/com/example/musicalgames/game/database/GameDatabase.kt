package com.example.musicalgames.game.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.GameInfo
import com.example.musicalgames.games.flappy.FlappyLevels
import com.example.musicalgames.games.mental_intervals.MentalLevels
import com.example.musicalgames.games.play_by_ear.EarPlayLevels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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

        private val populateCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                CoroutineScope(Dispatchers.IO).launch {
                    val levels = prepopulateLevels()

                    INSTANCE?.levelDao()?.addAllLevelEntities(levels)

                }
            }

        }

        private fun prepopulateLevels(): List<LevelEntity> {
            val flappyLevels = mapToEntities(FlappyLevels.baseLevels, Game.FLAPPY)
            val mentalLevels = mapToEntities(MentalLevels.intervalNoteLevels, Game.MENTAL_INTERVALS)
            val earLevels = mapToEntities(EarPlayLevels.baseLevels, Game.PLAY_BY_EAR)

            Log.d(
                "Database",
                "Generated prepopulateLevels: ${flappyLevels.size + mentalLevels.size + earLevels.size}"
            )

            return flappyLevels + mentalLevels + earLevels
        }

        private fun mapToEntities(levels: List<Level>, game: Game): List<LevelEntity> {

            return levels.map {
                LevelEntity(
                    id = 0,
                    gameId = game.ordinal,
                    levelJSON = MoshiUtil.adapters[game]!!.toJson(it),
                    isFavourite = false,
                    isCustom = false
                )
            }
        }
    }
}