package com.example.musicalgames
import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import com.example.musicalgames.controllers.GameController
import com.example.musicalgames.views.GameView

class GameActivity : Activity() {
    private lateinit var gameView: GameView
    private lateinit var gameController: GameController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        gameView = GameView(this)
        setContentView(gameView)
        gameController = GameController(gameView)
        gameController.startGame()
        Toast.makeText(this, "game created", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        gameController.stopGame()
    }
}
