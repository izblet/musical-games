package com.example.musicalgames.game_activity
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
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.flappy.FlappyGameController
import com.example.musicalgames.games.flappy.FlappyViewModel
import com.example.musicalgames.games.flappy.game_view.FloppyGameView

class GameFragment : Fragment(), GameListener {
    private lateinit var gameController: GameController
    private lateinit var permissionList: Array<String>
    private lateinit var startButton: Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_flappy, container, false)
        startButton = rootView.findViewById(R.id.startGameButton)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO: the name should be somewhere else
        val gameType = arguments?.getString("game_type")

        if(gameType == Game.FLAPPY.name) {
            permissionList = FlappyGameController.permissions
            val viewModel = ViewModelProvider(requireActivity())[FlappyViewModel::class.java]
            val gameView = FloppyGameView(requireContext())
            val gameContainer: ViewGroup = requireView().findViewById(R.id.game_container)
            gameContainer.addView(gameView)

            gameController = FlappyGameController(gameView)
            gameController.setViewModel(viewModel)
            gameController.initGame(requireContext(), this)
        }
        else {
            throw Exception("There is no view for a game with the specified type $gameType")
        }


        if (!checkPermissions())
            requestMultiplePermissions.launch(permissionList)

        startButton.setOnClickListener {
            if (checkPermissions()) {
                startButton.visibility = View.GONE
                gameController.startGame(this)
            } else {
                requestMultiplePermissions.launch(permissionList)
            }
        }
    }

    private fun checkPermissions(): Boolean {
        for(permission in permissionList) {
            val permissionResult = ContextCompat.checkSelfPermission(
                requireContext(), permission
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
            // Handle permission request result
        }

    override fun onGameEnded() {
        val viewModel = ViewModelProvider(requireActivity()).get(FlappyViewModel::class.java)
        viewModel.score = gameController.getScore()
        gameController.endGame()
        findNavController().navigate(R.id.gameEndedFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        gameController.endGame()
    }
}

