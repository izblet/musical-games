package com.example.musicalgames.games.flappy

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import com.example.musicalgames.games.MusicUtil
const val LEN_INF = -1
object DefaultLevels {
    val baseLevels: List<Level> = generateLevels()

    private fun generateLevels():List<Level> {
        val levels = mutableListOf<Level>()
        for(size in listOf(8, 10, 12)) {
            for (startKey in MusicUtil.getWhiteKeysFrom("F3", 8)) {
                    val startName = MusicUtil.noteName(startKey)
                    val endName = MusicUtil.noteName(MusicUtil.getKeyIntervalFrom(startName, size-1))
                    val levelName = "$startName to $endName"
                    levels.add(
                        Level(id=0, minPitch = startName, keyNum = size, name = levelName, endAfter=20)
                    )
                }
        }
        return levels
    }
}

@Entity(tableName = "flappy_levels")
data class Level(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val minPitch: String,
    val keyNum: Int,
    val name: String,
    val endAfter: Int
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