package com.example.musicalgames.game.games.play_by_ear

import android.content.Context
import android.util.Log
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

        //the input source could otherwise pick up our own speaker output during playback and
        //hold onto it (even just recognising it counts, regardless of which stream we listen
        //to) - only to surface later as a misleading report once cleared. Mute as soon as we
        //start talking (the rising edge) so the source stops paying attention to whatever it
        //hears for the whole time, not just cleaning up after - but only arm the auto-resume
        //once we're truly done (the falling edge): playbackActive stays true for an entire
        //multi-note sequence (e.g. a melody), and the ordinary silent gaps *between* its notes
        //would otherwise look like "done" to a countdown armed too early, resuming mid-sequence.
        owner.lifecycleScope.launch {
            var wasPlaybackActive = false
            viewModel.renderState.collect { state ->
                if (state.playbackActive && !wasPlaybackActive) {
                    noteInputSource.mute()
                } else if (!state.playbackActive && wasPlaybackActive) {
                    noteInputSource.unmuteWhenQuiet()
                }
                wasPlaybackActive = state.playbackActive
            }
        }

        owner.lifecycleScope.launch {
            //melody answer entry wants to react as soon as a note is confidently identified,
            //not wait for it to finish (e.g. decay/be superseded) - see NoteInputSource for the
            //distinction. Once the round is already finished, a note starting instead feeds the
            //confirm gesture's start-tracking - critically, NOT the round-completing note itself
            //(its own start happens while the round is still active, in the branch below), so
            //the gesture never thinks it watched that note start, and its later Finished event
            //(handled in the other collector) is correctly recognised as orphaned rather than
            //silently counted as the repeat's first half.
            noteInputSource.noteStarted.collect { note ->
                Log.d("EarConfirmDebug", "STARTED note=$note t=${System.currentTimeMillis()} playbackActive=${viewModel.renderState.value.playbackActive} problemFinished=${viewModel.problemFinished()}")
                if (viewModel.renderState.value.playbackActive) {
                    return@collect
                }

                if (viewModel.problemFinished()) {
                    confirmGesture.onNoteStarted(note)
                    return@collect
                }

                viewModel.selectNote(note)
                if (viewModel.problemFinished()) {
                    //this note just finished the round (right or wrong) - reset the gesture so
                    //the melody's own root-coincident note can't count as part of the repeat
                    Log.d("EarConfirmDebug", "problem just finished -> resetting confirmGesture trigger to rootNote=${viewModel.rootNote}")
                    viewModel.rootNote?.let { confirmGesture.setTrigger(it) }
                }
            }
        }

        owner.lifecycleScope.launch {
            //unlike melody answer entry, the confirm gesture wants a settled, complete note -
            //it's a deliberate "I'm done" signal, not something that should react to a note
            //that's merely been identified but might still resolve into something else
            noteInputSource.noteFinished.collect { note ->
                val playbackActive = viewModel.renderState.value.playbackActive
                val problemFinished = viewModel.problemFinished()
                Log.d("EarConfirmDebug", "FINISHED note=$note t=${System.currentTimeMillis()} playbackActive=$playbackActive problemFinished=$problemFinished")
                if (playbackActive || !problemFinished) {
                    Log.d("EarConfirmDebug", "FINISHED note=$note -> gated out, not fed to confirmGesture")
                    return@collect
                }
                val event = confirmGesture.onNoteFinished(note)
                Log.d("EarConfirmDebug", "FINISHED note=$note -> confirmGesture result=$event")
                if (event is NoteGestureEvent.Confirmed) {
                    viewModel.newProblem()
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
