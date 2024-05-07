package com.example.musicalgames
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.musicalgames.controllers.BirdController
import com.example.musicalgames.controllers.GameController
import com.example.musicalgames.models.PitchRecogniser
import com.example.musicalgames.views.GameEndListener
import com.example.musicalgames.views.FloppyGameView

class FlappyGameActivity : AppCompatActivity(), GameEndListener {
    private lateinit var gameView: FloppyGameView
    private lateinit var viewModel: ViewModel
    private lateinit var gameController: GameController
    private lateinit var pitchRecogniser: PitchRecogniser
    private var endListener: GameEndListener? =null
    fun setEndListener(listener: GameEndListener) {
        endListener=listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pitchRecogniser = PitchRecogniser(this)
        val birdController = BirdController(pitchRecogniser)

        setContentView(R.layout.activity_game_floppy)
        gameView = findViewById(R.id.gameView)
        gameView.setEndListener(this)
        gameController = GameController(gameView, birdController)

        val button = findViewById<Button>(R.id.startGameButton)
        button.setOnClickListener {
            if(checkPermissions()) {
                gameController.startGame()
                pitchRecogniser.start()
                button.visibility = View.GONE
            }
            else
                requestMultiplePermissions.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
        }

        //TODO:you should disable start button until permissions
        if(!checkPermissions())
            requestMultiplePermissions.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
    }

    private fun checkPermissions(): Boolean {
        val permissionResult = ContextCompat.checkSelfPermission(
            this, Manifest.permission.RECORD_AUDIO
        )
        return permissionResult == PackageManager.PERMISSION_GRANTED
    }

    private val requestMultiplePermissions =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) { permission->{}}

    override fun onPause() {
        super.onPause()
        onEndGame()
    }
    override fun onEndGame() {
        Toast.makeText(this, "collision", Toast.LENGTH_SHORT).show()
        gameController.stopGame()
        Handler().postDelayed({
            finish()
        }, 1000)
    }
    override fun onDestroy() {
        super.onDestroy()
        pitchRecogniser.release()
        gameController.stopGame()
    }
}
