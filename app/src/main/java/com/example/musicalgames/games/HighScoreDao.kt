package com.example.musicalgames.games;
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity;
import androidx.room.Insert
import androidx.room.PrimaryKey;
import androidx.room.Query
import androidx.room.Transaction

@Entity(tableName = "high_scores")
data class HighScore (
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        @ColumnInfo(name = "gameId") val gameId: String,
        @ColumnInfo(name = "modeId") val modeId: String,
        @ColumnInfo(name = "score") val score: Int
)

@Dao
interface HighScoreDao {
        @Insert
        suspend fun insert(highScore: HighScore)
        @Query("DELETE FROM high_scores WHERE id NOT IN " +
                "(SELECT id FROM high_scores WHERE gameId=:gameId ORDER BY score DESC LIMIT :limit)")
        suspend fun deleteAfter(gameId: String, limit: Int)

        @Query("SELECT * FROM high_scores WHERE gameId =:gameId ORDER BY score DESC")
        suspend fun getHighScores(gameId: String): List<HighScore>
        @Transaction
        suspend fun insertAndTrim(highScore: HighScore) {
                insert(highScore)
                deleteAfter(highScore.gameId, 5)
        }
}