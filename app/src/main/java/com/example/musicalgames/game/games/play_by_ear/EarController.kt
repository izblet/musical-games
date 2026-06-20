package com.example.musicalgames.game.games.play_by_ear

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.musicalgames.game.game_core.input.NoteInputSource
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EarController(
    private val viewModel: EarViewModel,
    private val noteInputSource: NoteInputSource
) : GameController {
    //TODO: nothing currently calls gameListener?.onGameEnded() for this game - the previous
    //wiring (relayed through EarView.onWrongAnswer()) was already commented out before this
    //field existed, so Play By Ear has never had a way to end itself. Needs deciding what should
    //end the game (wrong answer? running out of problems? something else?) and wiring
    //gameListener?.onGameEnded() accordingly, the same way FlappyController calls it directly.
    private var gameListener: GameListener? = null

    override fun setViewModel(viewModel: ViewModel) {
        // the view model is provided through the constructor instead
    }

    override fun initGame(context: Context, listener: GameListener) {
        gameListener = listener
    }

    override fun startGame(owner: LifecycleOwner) {
        noteInputSource.start()
        owner.lifecycleScope.launch {
            noteInputSource.noteSelected.collect { viewModel.selectNote(it) }
        }

        viewModel.playRoot()
        owner.lifecycleScope.launch {
            delay(2000)
            viewModel.newProblem()
        }
    }

    override fun pauseGame() {
        //TODO("Not yet implemented")
    }

    override fun endGame() {
        noteInputSource.stop()
    }

    override fun getScore(): Int {
        return viewModel.score
    }

    override fun getEndDescription(): String {
        return "The correct note was ${viewModel.getCorrectNote()}"
    }
}