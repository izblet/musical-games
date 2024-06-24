package com.example.musicalgames.games.flappy
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.example.musicalgames.games.GameController
import com.example.musicalgames.games.GameListener
import com.example.musicalgames.games.flappy.game_view.FloppyGameView
import com.example.musicalgames.wrappers.sound_playing.DefaultSoundPlayerManager

class GameFragment : Fragment(), GameListener {
    private lateinit var gameController: GameController
    private val soundPlayer by lazy { DefaultSoundPlayerManager(requireContext()) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_flappy, container, false)
        val viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)

        val startGameButton = rootView.findViewById<Button>(R.id.startGameButton)
        startGameButton.setOnClickListener {
            if (checkPermissions()) {
                soundPlayer.play(viewModel.minRange)
                val handler = Handler(Looper.getMainLooper())
                //TODO: this is of course temporary - played sounds should be a part of the level class or sth
                //  maybe a field called "resolution" that the boundaries resolve to
                //  or just simply the root
                handler.postDelayed(
                    {soundPlayer.play(viewModel.maxRange)},
                    1000
                )
                handler.postDelayed(
                    {soundPlayer.play("C4")},
                    2000
                )
                handler.postDelayed({
                    gameController.startGame(this)
                }, 3000)
                startGameButton.visibility = View.GONE
            } else {
                requestMultiplePermissions.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
            }
        }


        if (!checkPermissions())
            requestMultiplePermissions.launch(arrayOf(Manifest.permission.RECORD_AUDIO))

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        val gameView = FloppyGameView(requireContext())
        val gameContainer: ViewGroup = requireView().findViewById(R.id.game_container)
        gameContainer.addView(gameView)

        gameView.setEndListener(this)

        gameController = FlappyGameController(gameView)
        gameController.setViewModel(viewModel)
        gameController.initGame(requireContext())
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

    override fun onGameEnded() {
        val viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        viewModel.score= gameController.getScore()
        gameController.endGame()
        findNavController().navigate(R.id.gameEndedFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        gameController.endGame()
    }
}

