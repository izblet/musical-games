package com.example.musicalgames.games.flappy

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import com.example.musicalgames.game.games.flappy.FlappyLevel
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.utils.MusicUtil
import com.example.musicalgames.utils.MusicUtil.midi
import com.example.musicalgames.utils.MusicUtil.noteLetter
import com.example.musicalgames.utils.MusicUtil.noteName
import com.example.musicalgames.utils.Note
import com.example.musicalgames.utils.Scale

const val LEN_INF = -1
const val DELIMITER = ","
object FlappyLevels {
    val baseLevels: List<FlappyLevel> = generateMajorLevels()
    val minorLevels: List<FlappyLevel> = generateMinorLevels()
    private fun generateArcadeLevel(notes: List<Int>, root: Int, mode: String): FlappyLevel {
        val minPitch = notes[0]
        val maxPitch = notes[notes.size-1]

        val name = "${noteName(minPitch)} to ${noteName(maxPitch)}, root: ${noteLetter(root)} $mode"
        val description = "Arcade"
        return FlappyLevel(-1, minPitch, maxPitch, root, notes, name, description, LEN_INF)

    }

    private fun generateMajorLevels():List<FlappyLevel> {
        val levels = mutableListOf<FlappyLevel>()

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
    private fun generateMinorLevels():List<FlappyLevel> {
        val levels = mutableListOf<FlappyLevel>()

        val rootNote = Note("A3")

        for(sizeList in listOf(listOf(3,4), listOf(5,6), listOf(7), listOf(8))) {
            //generate levels with notes above root
            for(size in sizeList) {
                val notes = MusicUtil.getScaleNotesFrom(Scale.MINOR, rootNote.noteChromatic, rootNote, size)
                levels.add(generateArcadeLevel(notes.map{it.midiCode}, rootNote.midiCode, "minor"))
            }
            //generate levels with notes below root
            for (size in sizeList) {
                val notes = MusicUtil.getScaleNotesTo(Scale.MINOR, rootNote.noteChromatic, rootNote, size)
                levels.add(generateArcadeLevel(notes.map{it.midiCode}, rootNote.midiCode, "minor"))
            }

            //generate levels that concat notes below and notes above
            for(size in sizeList) {
                var notes = MusicUtil.getScaleNotesTo(Scale.MINOR, rootNote.noteChromatic, rootNote, size)
                notes = notes.slice(0..(notes.size - 2)) + MusicUtil.getScaleNotesFrom(Scale.MINOR, rootNote.noteChromatic, rootNote, size)
                levels.add(generateArcadeLevel(notes.map{it.midiCode}, rootNote.midiCode, "minor"))
            }
        }

        return levels
    }

}

@Entity(tableName = "flappy_levels")
data class FlappyDatabaseLevel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val minPitch: Int, //min pitch that is to be displayed
    val maxPitch: Int, //max pitch that is to be displayed
    val root: Int,
    val listOfMidiKeys: String, //list of integers corresponding to midi notes
    val name: String,
    val description: String,
    val endAfter: Int,
    val custom: Boolean,
    val favourite: Boolean
)

@Dao
interface FlappyLevelDao {
    @Insert
    suspend fun insert(level: FlappyDatabaseLevel)
    @Update
    suspend fun update(level: FlappyDatabaseLevel)

    @Query("SELECT * FROM flappy_levels ORDER BY id DESC")
    suspend fun getDatabaseLevels(): List<FlappyDatabaseLevel>?

    @Query("SELECT * FROM flappy_levels WHERE favourite = 1")
    suspend fun getFavouriteDatabase(): List<FlappyDatabaseLevel>

    suspend fun getFavouriteLevels(): List<Level> {
        return getFavouriteDatabase().map { databaseLevel -> databaseLevel.toLevel() }
    }

    @Query("SELECT * FROM flappy_levels WHERE custom = 1")
    suspend fun getCustomDatabase(): List<FlappyDatabaseLevel>

    suspend fun getCustomLevels():List<Level> {
        return getCustomDatabase().map { databaseLevel -> databaseLevel.toLevel() }
    }

    @Query("SELECT * FROM flappy_levels WHERE custom = 0")
    suspend fun getBaseDatabase(): List<FlappyDatabaseLevel>

    suspend fun getBaseLevels():List<Level> {
        return getBaseDatabase().map { databaseLevel -> databaseLevel.toLevel() }
    }


    private fun FlappyDatabaseLevel.toLevel(): FlappyLevel {
        val listOfInts: List<Int> = listOfMidiKeys.split(DELIMITER).map {it.toInt()}
        return FlappyLevel(id, minPitch, maxPitch, root, listOfInts, name, description, endAfter)
    }
    private fun FlappyLevel.toDatabaseLevel(custom: Boolean, favourite: Boolean): FlappyDatabaseLevel {
        val stringList = keyList.joinToString(separator = DELIMITER)
        return FlappyDatabaseLevel(0, minPitch, maxPitch, root, stringList, name, description, endAfter, custom, favourite)
    }
    suspend fun insert(level: FlappyLevel, custom: Boolean) {
        insert(level.toDatabaseLevel(custom, false))
    }

    suspend fun update(level: FlappyLevel, custom: Boolean, favourite: Boolean) {
        update(level.toDatabaseLevel(custom, favourite))
    }
    suspend fun getLevels() : List<FlappyLevel>{
        val levels = getDatabaseLevels() ?:
        return listOf()
        return levels.map{ level-> level.toLevel()}
    }

}