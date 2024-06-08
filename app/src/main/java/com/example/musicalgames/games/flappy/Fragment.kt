package com.example.musicalgames.games.flappy
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.musicalgames.R

class Fragment : Fragment(), GameEndListener {
    private lateinit var gameView: FloppyGameView
    private lateinit var gameController: GameController
    private lateinit var pitchRecogniser: PitchRecogniser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_flappy, container, false)
        val viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)

        viewModel.maxRange = "G4"
        viewModel.minRange = "G3"
        val minListenedPitch = "C2"
        val maxListenedPitch = "C6"

        pitchRecogniser = PitchRecogniser(requireContext(),
             minListenedPitch, maxListenedPitch)

        gameView = rootView.findViewById(R.id.gameView)
        gameView.setEndListener(this)
        viewModel.pitchRecogniser = pitchRecogniser
        gameView.setViewModel(viewModel)

        gameController = GameController(gameView)

        val startGameButton = rootView.findViewById<Button>(R.id.startGameButton)
        startGameButton.setOnClickListener {
            if (checkPermissions()) {
                gameController.startGame(this)
                pitchRecogniser.start()
                startGameButton.visibility = View.GONE
            } else {
                requestMultiplePermissions.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
            }
        }

        if (!checkPermissions())
            requestMultiplePermissions.launch(arrayOf(Manifest.permission.RECORD_AUDIO))

        return rootView
    }

    private fun checkPermissions(): Boolean {
        val permissionResult = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.RECORD_AUDIO
        )
        return permissionResult == PackageManager.PERMISSION_GRANTED
    }

    private val requestMultiplePermissions =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permission ->
            // Handle permission request result if needed
        }

    override fun onEndGame() {
        val viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        viewModel.score= gameView.getScore()
        pitchRecogniser.release()
        gameController.endGame()
        findNavController().navigate(R.id.gameEndedFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        pitchRecogniser.release()
        gameController.endGame()
    }
}

