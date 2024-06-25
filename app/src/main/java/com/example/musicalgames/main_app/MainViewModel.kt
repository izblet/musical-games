package com.example.musicalgames.main_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.GameDatabase
import com.example.musicalgames.games.GameOption
import com.example.musicalgames.games.HighScore
import com.example.musicalgames.games.HighScoreDao

class MainViewModel(application: Application): AndroidViewModel(application) {
    var game: Game? = null
    var gameOptions: List<GameOption>? = null
    private val highScoreDao: HighScoreDao = GameDatabase.getDatabase(application).highScoreDao()

    suspend fun getHighScores(gameId: Int, modeId: GameOption): List<HighScore> {
        return highScoreDao.getHighScores(gameId, modeId.toString())
    }
}