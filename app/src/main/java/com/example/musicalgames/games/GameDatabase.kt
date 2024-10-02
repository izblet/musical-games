package com.example.musicalgames.games;

import android.content.Context
import androidx.room.Database;
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.flappy.DatabaseLevel
import com.example.musicalgames.games.flappy.FlappyLevel
import com.example.musicalgames.games.flappy.FlappyLevels
import com.example.musicalgames.games.flappy.LevelDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [DatabaseLevel::class, HighScore::class], version = 2, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {
    abstract fun highScoreDao(): HighScoreDao
    abstract fun flappyLevelDao(): LevelDao

    companion object {
        @Volatile
        private var INSTANCE: GameDatabase? = null

        fun getDatabase(context: Context): GameDatabase {
            return INSTANCE?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "game_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(SeedDatabaseCallback(context))
                    .build()
                INSTANCE=instance
                instance
            }
        }
        private class SeedDatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Insert initial data here
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.flappyLevelDao(), context)
                    }
                }
            }
        }
        suspend fun populateDatabase(levelDao: LevelDao, context: Context) {
            val levels: List<FlappyLevel> = FlappyLevels.baseLevels

            levels.forEach {
                levelDao.insert(it, false)
            }
        }
    }

}
