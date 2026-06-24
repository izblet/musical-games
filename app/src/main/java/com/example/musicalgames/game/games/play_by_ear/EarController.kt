package com.example.musicalgames.game.games.play_by_ear

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.musicalgames.game.game_core.input.MicrophoneNoteInput
import com.example.musicalgames.game.game_core.input.NoteGestureEvent
import com.example.musicalgames.game.game_core.input.NoteInputSource
import com.example.musicalgames.game.game_core.input.RepeatNoteConfirmGesture
import com.example.musicalgames.game_activity.GameController
import com.example.musicalgames.game_activity.GameListener
import com.example.musicalgames.music_model.Note
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EarController(
    private val viewModel: EarViewModel,
    private val noteInputSource: NoteInputSource
) : GameController {
    //confirms "I'm done, move to the next melody" by repeating the root twice in a row - same
    //gesture as Chords' chord-selection confirm, but only consulted once problemFinished() is
    //true (Good!/Wrong! already showing), never during melody answer entry itself
    private val confirmGesture = RepeatNoteConfirmGesture<Note>()
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
        viewModel.rootNote?.let { confirmGesture.setTrigger(it) }
        owner.lifecycleScope.launch {
            noteInputSource.noteSelected.collect { note ->
                val wasFinished = viewModel.problemFinished()
                viewModel.selectNote(note)
                if (wasFinished) {
                    if (confirmGesture.onNote(note) is NoteGestureEvent.Confirmed) {
                        viewModel.newProblem()
                    }
                } else if (viewModel.problemFinished()) {
                    //this note just finished the round (right or wrong) - reset the gesture so
                    //the melody's own last note (which may equal the root) can't count as the
                    //first half of a repeat for the confirm that follows
                    viewModel.rootNote?.let { confirmGesture.setTrigger(it) }
                }
            }
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
        (noteInputSource as? MicrophoneNoteInput)?.release()
    }

    override fun getScore(): Int {
        return viewModel.score
    }

    override fun getEndDescription(): String {
        return "The correct note was ${viewModel.getCorrectNote()}"
    }
}