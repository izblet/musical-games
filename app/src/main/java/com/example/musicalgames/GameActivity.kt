package com.example.musicalgames
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import android.Manifest
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.musicalgames.controllers.BirdController
import com.example.musicalgames.controllers.GameController
import com.example.musicalgames.models.PitchRecogniser
import com.example.musicalgames.views.GameView

class GameActivity : AppCompatActivity() {
    private lateinit var gameView: GameView
    private lateinit var gameController: GameController
    private lateinit var pitchRecogniser: PitchRecogniser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pitchRecogniser = PitchRecogniser(this)
        val birdController = BirdController(pitchRecogniser)

        setContentView(R.layout.activity_game)

        gameView = findViewById(R.id.gameView)
        gameController = GameController(gameView, birdController)

        val button = findViewById<Button>(R.id.startGameButton)
        button.setOnClickListener {
            gameController.startGame()
            pitchRecogniser.startRecording()
            button.visibility = View.GONE
        }

        //TODO:you should disable start button until permissions
        requestMultiplePermissions.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
    }

    private val requestMultiplePermissions =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) { }
    override fun onDestroy() {
        super.onDestroy()
        pitchRecogniser.release()
        gameController.stopGame()
    }
}
