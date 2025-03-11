package com.example.musicalgames.games;

import android.content.Context
import androidx.room.Database;
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.musicalgames.games.flappy.FlappyDatabaseLevel
import com.example.musicalgames.games.flappy.FlappyLevel
import com.example.musicalgames.games.flappy.FlappyLevels
import com.example.musicalgames.games.flappy.FlappyLevelDao
import com.example.musicalgames.games.mental_intervals.MentalDatabaseLevel
import com.example.musicalgames.games.mental_intervals.MentalLevel
import com.example.musicalgames.games.mental_intervals.MentalLevelDao
import com.example.musicalgames.games.mental_intervals.MentalLevels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [FlappyDatabaseLevel::class, MentalDatabaseLevel::class, HighScore::class], version = 2, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {
    abstract fun highScoreDao(): HighScoreDao
    abstract fun flappyLevelDao(): FlappyLevelDao
    abstract fun mentalLevelDao(): MentalLevelDao

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
        ) : Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.flappyLevelDao(), database.mentalLevelDao(), context)

                    }
                }
            }
        }

        suspend fun populateDatabase(flappyLevelDao: FlappyLevelDao, mentalLevelDao: MentalLevelDao, context: Context) {
            val flappyLevels: List<FlappyLevel> = FlappyLevels.baseLevels
            flappyLevels.forEach {
                flappyLevelDao.insert(it, false)
            }

            val mentalLevels: List<MentalLevel> = MentalLevels.intervalNoteLevels
            mentalLevels.forEach {
                mentalLevelDao.insert(it,false)
            }
        }
    }

}
