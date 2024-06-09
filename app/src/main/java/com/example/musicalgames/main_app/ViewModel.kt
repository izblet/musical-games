package com.example.musicalgames.main_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.musicalgames.games.GameDatabase
import com.example.musicalgames.games.HighScore
import com.example.musicalgames.games.HighScoreDao

class ViewModel(application: Application): AndroidViewModel(application) {
    var game: Game? = null
    private val highScoreDao: HighScoreDao = GameDatabase.getDatabase(application).highScoreDao()

    suspend fun getHighScores(gameId: Int): List<HighScore> {
        return highScoreDao.getHighScores(gameId)
    }
}