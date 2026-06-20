package com.example.musicalgames.game_activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.musicalgames.R
import com.example.musicalgames.games.Game
import com.example.musicalgames.games.GameMap

class GameFragment : Fragment(), GameListener {
    private val args: GameFragmentArgs by navArgs()
    private lateinit var gameController: GameController
    private var gameType: Game? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO: the name should be somewhere else
        val gameType = args.gameType
        this.gameType = Game.valueOf(gameType)

        val gameContainer: ViewGroup = requireView().findViewById(R.id.game_container)
        val game = this.gameType!!
        val gameFactory = GameMap.createFactory(game)

        //permissions are requested from the level options screen before the game is launched,
        //so they're already granted by the time this fragment is reached
        gameController = gameFactory.createGame(requireContext(), requireActivity(), gameContainer, this)

        Handler(Looper.getMainLooper()).postDelayed({ gameController.startGame(this) }, gameFactory.getStartDelayMs())
    }

    override fun onGameEnded() {
        gameController.endGame()

        //TODO: for now it does the same thing for every game, score could be some other value
        val action = GameFragmentDirections.actionGameFragmentToGameEndedFragment(
            gameType!!.name,
            "Game Ended",
            "Your score: ${gameController.getScore()}",
            gameController.getEndDescription())
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        gameController.endGame()
    }
}
