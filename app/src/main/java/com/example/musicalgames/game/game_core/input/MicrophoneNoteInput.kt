package com.example.musicalgames.game.game_core.input

import com.example.musicalgames.music_model.Note
import com.example.musicalgames.utils.wrappers.sound_recording.PitchRecogniser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Reads notes played/sung into an external instrument or voice, via a [PitchRecogniser]
 * polled on its own coroutine and turned into discrete note-selected events by a
 * [MicrophoneNoteDetector].
 */
class MicrophoneNoteInput(
    private val pitchRecogniser: PitchRecogniser,
    private val detector: MicrophoneNoteDetector = MicrophoneNoteDetector()
) : NoteInputSource {

    private val pollRateMs = 1000L / 60

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var pollJob: Job? = null

    private val _noteSelected = MutableSharedFlow<Note>()
    override val noteSelected: SharedFlow<Note> = _noteSelected.asSharedFlow()

    override fun start() {
        if (pollJob != null) return
        pitchRecogniser.start()
        pollJob = scope.launch {
            // fixed-rate scheduling: track an absolute next-tick target and delay only the
            // remainder, so getPitch()/onPitchSample()'s own (small) duration doesn't add up
            // on top of pollRateMs each iteration.
            var nextTick = System.currentTimeMillis()
            while (true) {
                val pitch = pitchRecogniser.getPitch().takeIf { it != PitchRecogniser.UNDEFINED }
                val note = detector.onPitchSample(pitch, System.currentTimeMillis())
                // emit() (not tryEmit()) suspends until the collector is ready instead of
                // ever silently dropping an onset - this producer is itself a coroutine, so
                // unlike OnscreenNoteInputSource it can afford to suspend here.
                if (note != null) {
                    _noteSelected.emit(note)
                }

                nextTick += pollRateMs
                val waitMs = nextTick - System.currentTimeMillis()
                if (waitMs > 0) {
                    delay(waitMs)
                } else {
                    // fell behind schedule - slip forward instead of bursting through
                    // however many ticks were missed
                    nextTick = System.currentTimeMillis()
                }
            }
        }
    }

    override fun stop() {
        pollJob?.cancel()
        pollJob = null
        pitchRecogniser.stop()
    }

    //TODO: NOT called from anywhere yet - must be wired into EarController.endGame() once this
    //is actually plugged into the game (input-method-abstraction step 9), the same way
    //FlappyController calls pitchRecogniser.release() directly. Deliberately not part of
    //NoteInputSource itself (OnscreenNoteInputSource has nothing to release).
    fun release() {
        scope.cancel()
        pitchRecogniser.release()
    }
}
