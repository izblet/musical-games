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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.musicalgames.R
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.GameMap

class GameFragment : Fragment(), GameListener {
    private val args: GameFragmentArgs by navArgs()
    private lateinit var gameController: GameController
    private lateinit var permissionList: Array<String>
    private lateinit var startButton: Button
    private var gameType: Game? = null
    //private var viewModel: AbstractViewModel? = null
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
        val gameType = args.gameType
        this.gameType = Game.valueOf(gameType)

        val gameContainer: ViewGroup = requireView().findViewById(R.id.game_container)
        val gameFactory = GameMap.gameInfos[this.gameType]!!.gameFactory

        permissionList = gameFactory.getPermissions()
        gameController = gameFactory.createGame(requireContext(), requireActivity(), gameContainer, this)

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
        gameController.endGame()

        //TODO: for now it does the same thing for every game, score could be some other value
        val action = GameFragmentDirections.actionFlappyGameFragmentToGameEndedFragment(
            gameType!!.name,
            "Game Ended",
            "Your score: ${gameController.getScore()}",
            "")
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        gameController.endGame()
    }
}

