package com.example.musicalgames.games.flappy

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.musicalgames.games.GameDatabase
import com.example.musicalgames.games.HighScore
import com.example.musicalgames.games.HighScoreDao
import com.example.musicalgames.main_app.Game
import com.example.musicalgames.main_app.GameMap
import com.example.musicalgames.main_app.GameOption

class ViewModel(application: Application) : AndroidViewModel(application) {
    private var gameId: Int = GameMap.gameInfos[Game.FLAPPY]!!.id
    var gameOption: GameOption? = null
    var score = 0
    var pitchRecogniser: PitchRecogniser? = null
    var minRange: String = "G3"
    var maxRange: String = "G4"

    private val highScoreDao: HighScoreDao = GameDatabase.getDatabase(application).highScoreDao()

    suspend fun checkHighScore(): Boolean {
        val highScores: List<HighScore> = highScoreDao.getHighScores(gameId,
            gameOption!!.toString()
        )

        highScoreDao.insertAndTrim(HighScore(gameId = gameId, modeId = gameOption!!.toString(), score = score))

        return if (highScores.isNotEmpty())
            score > highScores[0].score

        else true
    }

}