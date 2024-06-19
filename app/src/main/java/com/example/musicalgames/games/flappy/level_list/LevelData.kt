package com.example.musicalgames.games.flappy.level_list

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import com.example.musicalgames.utils.MusicUtil
import com.example.musicalgames.utils.MusicUtil.midi
import com.example.musicalgames.utils.MusicUtil.noteLetter
import com.example.musicalgames.utils.MusicUtil.noteName

const val LEN_INF = -1
const val DELIMITER = ","
object DefaultLevels {
    val baseLevels: List<Level> = generateMajorLevels()
    private fun generateArcadeLevel(notes: List<Int>, root: Int, mode: String): Level {
        val minPitch = notes[0]
        val maxPitch = notes[notes.size-1]

        val name = "${noteName(minPitch)} to ${noteName(maxPitch)}, root: ${noteLetter(root)} $mode"
        val description = "Arcade"
        return Level(-1, minPitch, maxPitch, root, notes, name, description, LEN_INF)

    }

    private fun generateMajorLevels():List<Level> {
        val levels = mutableListOf<Level>()

        val rootNote = midi("C4")

        for(sizeList in listOf(listOf(3,4), listOf(5,6), listOf(7), listOf(8))) {
            //generate levels with notes above root
            for(size in sizeList) {
                val notes = MusicUtil.getWhiteKeysFrom(rootNote, size)
                levels.add(generateArcadeLevel(notes, rootNote, "major"))
            }
            //generate levels with notes below root
            for (size in sizeList) {
                val notes = MusicUtil.getWhiteKeysTo(rootNote, size)
                levels.add(generateArcadeLevel(notes, rootNote, "major"))
            }

            //generate levels that concat notes below and notes above
            for(size in sizeList) {
                var notes = MusicUtil.getWhiteKeysTo(rootNote, size)
                notes = notes.slice(0..(notes.size - 2)) + MusicUtil.getWhiteKeysFrom(rootNote, size)
                levels.add(generateArcadeLevel(notes, rootNote, "major"))
            }
        }

        return levels
    }
}
data class Package(
    val name:String,
    val description: String,
    val levelList: List<Level>,
)
data class Level(
    val id: Int,
    val minPitch: Int,
    val maxPitch: Int,
    val root: Int,
    val keyList: List<Int>,
    val name: String,
    val description: String,
    val endAfter: Int
)

@Entity(tableName = "flappy_levels")
data class DatabaseLevel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val minPitch: Int, //min pitch that is to be displayed
    val maxPitch: Int, //max pitch that is to be displayed
    val root: Int,
    val listOfMidiKeys: String, //list of integers corresponding to midi notes
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
        return Level(id, minPitch, maxPitch, root, listOfInts, name, description, endAfter)
    }
    private fun Level.toDatabaseLevel(): DatabaseLevel {
        val stringList = keyList.joinToString(separator = DELIMITER)
        return DatabaseLevel(id, minPitch, maxPitch, root, stringList, name, description, endAfter)
    }
    suspend fun insert(level: Level) {
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

}