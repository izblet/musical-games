package com.example.musicalgames.games.flappy.level_list

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import com.example.musicalgames.utils.MusicUtil
import com.example.musicalgames.utils.MusicUtil.noteName

const val LEN_INF = -1
const val DELIMITER = ","
object DefaultLevels {
    val baseLevels: List<Level> = generateLevels()

    private fun generateLevels():List<Level> {
        val levels = mutableListOf<Level>()
        val minPitch = "C4"
        val maxPitch = "C5"
        for(size in listOf(3,4,5,6,7)) {
            val notes = MusicUtil.getWhiteKeysFrom("C4", size)
            val lastNote = notes[notes.size-1]
            val name = "C major basic, $size notes"
            val description = "C4 to "+noteName(lastNote)+", white keys, 20 pipes"
            levels.add(
                Level(-1,minPitch, noteName(lastNote), notes, name, description,20)
            )
        }

        return levels
    }
}
data class Level(
    val id: Int,
    val minPitch: String,
    val maxPitch: String,
    val keyList: List<Int>,
    val name: String,
    val description: String,
    val endAfter: Int
)

@Entity(tableName = "flappy_levels")
data class DatabaseLevel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val minPitch: String, //min pitch that is to be displayed
    val maxPitch: String, //max pitch that is to be displayed
    val listOfMidiKeys: String, //list of integers corresponding to a
    val name: String,
    val description: String,
    val endAfter: Int
)

@Dao
interface LevelDao {
    @Insert
    suspend fun insert(level: DatabaseLevel)
    @Update
    suspend fun update(level: DatabaseLevel)
    @Query("SELECT * FROM flappy_levels ORDER BY id DESC")
    suspend fun getDatabaseLevels(): List<DatabaseLevel>?

    private fun DatabaseLevel.toLevel(): Level {
        val listOfInts: List<Int> = listOfMidiKeys.split(DELIMITER).map {it.toInt()}
        return Level(id, minPitch, maxPitch, listOfInts, name, description, endAfter)
    }
    private fun Level.toDatabaseLevel(): DatabaseLevel {
        val stringList = keyList.joinToString(separator = DELIMITER)
        return DatabaseLevel(id, minPitch, maxPitch, stringList, name, description, endAfter)
    }
    /*suspend fun insert(level: Level) {
        insert(level.toDatabaseLevel())
    }
    suspend fun update(level: Level) {
        update(level.toDatabaseLevel())
    }
    suspend fun getLevels() : List<Level>{
        val levels = getDatabaseLevels() ?:
        return listOf()
        return levels.map{ level-> level.toLevel()}
    }
    */

}