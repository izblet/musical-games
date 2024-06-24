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
import androidx.core.content.PermissionChecker.PermissionResult
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.musicalgames.R
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.GameController
import com.example.musicalgames.games.GameListener
import com.example.musicalgames.games.flappy.game_view.FloppyGameView

class GameFragment : Fragment(), GameListener {
    private lateinit var gameController: GameController
    private lateinit var permissionList: Array<String>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_flappy, container, false)

        permissionList = FlappyGameController.permissions

        val startGameButton = rootView.findViewById<Button>(R.id.startGameButton)
        startGameButton.setOnClickListener {
            if (checkPermissions()) {
                startGameButton.visibility = View.GONE
                gameController.startGame(this)
            } else {
                requestMultiplePermissions.launch(permissionList)
            }
        }

        if (!checkPermissions())
            requestMultiplePermissions.launch(permissionList)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO: the name should be somewhere else
        val gameType = arguments?.getString("game_type")

        if(gameType == Game.FLAPPY.name) {
            val viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
            val gameView = FloppyGameView(requireContext())
            val gameContainer: ViewGroup = requireView().findViewById(R.id.game_container)
            gameContainer.addView(gameView)

            gameController = FlappyGameController(gameView)
            gameController.setViewModel(viewModel)
            gameController.initGame(requireContext(), this)
        }
    }

    private fun checkPermissions(): Boolean {
        for(permission in permissionList) {
            val permissionResult = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.RECORD_AUDIO
            )
            if(permissionResult != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private val requestMultiplePermissions =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permission ->
            // Handle permission request result if needed
        }

    override fun onGameEnded() {
        val viewModel = ViewModelProvider(requireActivity()).get(ViewModel::class.java)
        viewModel.score = gameController.getScore()
        gameController.endGame()
        findNavController().navigate(R.id.gameEndedFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        gameController.endGame()
    }
}

