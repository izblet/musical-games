package com.example.musicalgames.main_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.GameDatabase
import com.example.musicalgames.games.GamePackage
import com.example.musicalgames.games.HighScoreDao

class MainViewModel(application: Application): AndroidViewModel(application) {
    var game: Game? = null
    //var pack: GamePackage? = null //should be removed, not necessary
   // private val highScoreDao: HighScoreDao = GameDatabase.getDatabase(application).highScoreDao()

    /*suspend fun getHighScores(gameId: Int, modeId: GameOption): List<HighScore> {
        return highScoreDao.getHighScores(gameId, modeId.toString())
    }

     */
}