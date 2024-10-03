package com.example.musicalgames.games.mental_intervals

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import com.example.musicalgames.game_activity.Level
import com.example.musicalgames.games.flappy.DELIMITER
import com.example.musicalgames.utils.ChromaticNote
import com.example.musicalgames.utils.DiatonicNote
import com.example.musicalgames.utils.Interval


object MentalLevels {
    val intervalNoteLevels: List<MentalLevel> = generateIntervalLevels()
    private fun generateIntervalLevels() : List<MentalLevel> {
        val levels = mutableListOf<MentalLevel>()
        val intervals = mutableListOf<Interval>()
        for(maxSemitones in listOf(1,2,3,4,5,6,7,8,9,10,11)) {
            intervals.add(Interval.fromSemitones(maxSemitones))
            levels.add(
                MentalLevel(
                    0,
                    DiatonicNote.values().map { diatonicNote -> diatonicNote.chromaticNote  },
                    intervals.toList(),
                    Type.INTERVAL_NOTE,
                    "at most $maxSemitones semitones",
                    ""
                )
            )
        }
        return levels
    }
    /*val noteIntervalLevels: List<MentalLevel> = generateNoteLevels()
    val degreeNoteLevels: List<MentalLevel> = generateDegreeMajorLevels()+generateDegreeMinorLevels()
    private fun generateIntervalLevels(): List<MentalLevel> {
        val levels = mutableListOf<MentalLevel>()
        //the list is like this 'cause it will be changed
        for (maxSemitones in listOf(2,3,4,5,6,7,8,9,10,11)) {
            levels.add(
                MentalLevel(
                    0,
                    maxSemitones,
                    Type.INTERVAL_NOTE,
                    Scale.CHROMATIC,
                    "At most $maxSemitones semitones", ""
                )
            )

        }
        return levels
    }
    private fun generateNoteLevels(): List<MentalLevel> {
        val levels = mutableListOf<MentalLevel>()
        //the list is like this 'cause it will be changed
        for (maxSemitones in listOf(2,3,4,5,6,7,8,9,10,11)) {
            levels.add(
                MentalLevel(
                    0,
                    maxSemitones,
                    Type.NOTE_INTERVAL,
                    Scale.CHROMATIC,
                    "At most $maxSemitones semitones", ""
                )
            )

        }
        return levels
    }
    private fun generateDegreeMajorLevels(): List<MentalLevel> {
        val levels = mutableListOf<MentalLevel>()
        //the list is like this 'cause it will be changed
        val notes = Scale.MAJOR.getDegrees()
        val semitones = notes.map{interval -> interval.getSemitones() }
        for (maxSemitones in semitones.slice(1..<semitones.size)) {
            levels.add(
                MentalLevel(
                    0,
                    maxSemitones,
                    Type.DEGREE_NOTE,
                    Scale.MAJOR,
                    "Major scale, at most $maxSemitones semitones", ""
                )
            )

        }
        return levels
    }
    private fun generateDegreeMinorLevels(): List<MentalLevel> {
        val levels = mutableListOf<MentalLevel>()
        //the list is like this 'cause it will be changed
        val notes = Scale.MINOR.getDegrees()
        val semitones = notes.map{interval -> interval.getSemitones() }
        for (maxSemitones in semitones.slice(1..<semitones.size)) {
            levels.add(
                MentalLevel(
                    0,
                    maxSemitones,
                    Type.DEGREE_NOTE,
                    Scale.MINOR,
                    "Minor scale, at most $maxSemitones semitones", ""
                )
            )

        }
        return levels
    }
    */
}

enum class Type {
    INTERVAL_NOTE,
    NOTE_INTERVAL,
    DEGREE_NOTE
}
data class MentalLevel (
    override val id: Int,
    val startingNotes: List<ChromaticNote>,
    val intervals: List<Interval>,
    val mode: Type,
    override val name: String,
    override val description: String,
): Level(id, name, description)


@Entity(tableName = "mental_levels")
data class MentalDatabaseLevel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startingNotesList: String,
    val intervalsList: String,
    val mode: Type,
    val name: String,
    val description: String,
    val custom: Boolean,
    val favourite: Boolean
)

@Dao
interface MentalLevelDao {
    @Insert
    suspend fun insert(level: MentalDatabaseLevel)
    @Update
    suspend fun update(level: MentalDatabaseLevel)

    @Query("SELECT * FROM mental_levels ORDER BY id DESC")
    suspend fun getDatabaseLevels(): List<MentalDatabaseLevel>?

    @Query("SELECT * FROM mental_levels WHERE favourite = 1")
    suspend fun getFavouriteDatabase(): List<MentalDatabaseLevel>

    suspend fun getFavouriteLevels(): List<Level> {
        return getFavouriteDatabase().map { databaseLevel -> databaseLevel.toLevel() }
    }

    @Query("SELECT * FROM mental_levels WHERE custom = 1")
    suspend fun getCustomDatabase(): List<MentalDatabaseLevel>

    suspend fun getCustomLevels():List<Level> {
        return getCustomDatabase().map { databaseLevel -> databaseLevel.toLevel() }
    }

    @Query("SELECT * FROM mental_levels WHERE custom = 0")
    suspend fun getBaseDatabase(): List<MentalDatabaseLevel>

    suspend fun getBaseLevels():List<Level> {
        return getBaseDatabase().map { databaseLevel -> databaseLevel.toLevel() }
    }


    private fun MentalDatabaseLevel.toLevel(): MentalLevel {
        val startNotes: List<ChromaticNote> = startingNotesList.split(DELIMITER).map { ChromaticNote.valueOf(it) }
        val intervals : List<Interval> = intervalsList.split(DELIMITER).map { Interval.valueOf(it) }
        return MentalLevel(id, startNotes, intervals, mode, name, description)
    }
    private fun MentalLevel.toMentalDatabaseLevel(custom: Boolean, favourite: Boolean): MentalDatabaseLevel {
        val startNoteString = startingNotes.joinToString(separator = DELIMITER)
        val intervalsString = intervals.joinToString(separator = DELIMITER)
        return MentalDatabaseLevel(0, startNoteString, intervalsString, mode, name, description, custom, favourite)
    }
    suspend fun insert(level: MentalLevel, custom: Boolean) {
        insert(level.toMentalDatabaseLevel(custom, false))
    }

    suspend fun update(level: MentalLevel, custom: Boolean, favourite: Boolean) {
        update(level.toMentalDatabaseLevel(custom, favourite))
    }
    suspend fun getLevels() : List<MentalLevel>{
        val levels = getDatabaseLevels() ?:
        return listOf()
        return levels.map{ level-> level.toLevel()}
    }

}