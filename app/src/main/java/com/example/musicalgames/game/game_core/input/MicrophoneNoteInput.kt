package com.example.musicalgames.game.game_core.input

import com.example.musicalgames.music_model.Note
import com.example.musicalgames.settings.MicrophoneSettings
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
 * Reads notes played/sung into an external instrument or voice, via a [PitchSource]
 * polled on its own coroutine and turned into discrete [NoteEvent]s by a [MicrophoneNoteDetector]
 * - exposed as two separate streams ([noteStarted]/[noteFinished]) so each caller can pick
 * whichever timing it actually wants, rather than this class deciding for everyone.
 */
class MicrophoneNoteInput(
    private val pitchRecogniser: PitchSource,
    private val detector: MicrophoneNoteDetector,
    private val quietEnergyFloor: Float
) : NoteInputSource {

    companion object {
        private const val POLL_RATE_MS = 1000L / 60
        //how long energy must stay below quietEnergyFloor while muted before resuming - long
        //enough to outlast both the fast energy reading's own ~20ms slice and the (much larger)
        //SPICE pitch buffer's 200ms rolling window, so neither still "remembers" loud reference
        //audio by the time reporting resumes. Not a guess at how long an acoustic tail lasts -
        //it's however long it actually takes, confirmed by watching real energy, not assumed
        private const val QUIET_CONFIRM_MS = 250L

        /** Builds a [MicrophoneNoteInput] whose [MicrophoneNoteDetector] is configured from [settings]. */
        fun withSettings(pitchRecogniser: PitchSource, settings: MicrophoneSettings): MicrophoneNoteInput {
            val onsetDetector = OnsetDetector(
                energyFloor = settings.energyThreshold.toFloat(),
                riseFactor = settings.onsetRiseFactor,
                refractoryMs = settings.onsetRefractoryMs
            )
            val detector = MicrophoneNoteDetector(
                minWindowSize = (settings.windowMs / POLL_RATE_MS
                    * MicrophoneNoteDetector.DEFAULT_MIN_WINDOW_SIZE_PERCENT / 100).toInt(),
                entryThresholdPercent = settings.entryThresholdPercent,
                windowMs = settings.windowMs,
                exitThresholdPercent = settings.exitThresholdPercent,
                onsetDetector = onsetDetector
            )
            return MicrophoneNoteInput(pitchRecogniser, detector, settings.energyThreshold.toFloat())
        }
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var pollJob: Job? = null

    private val _noteStarted = MutableSharedFlow<Note>()
    override val noteStarted: SharedFlow<Note> = _noteStarted.asSharedFlow()
    private val _noteFinished = MutableSharedFlow<Note>()
    override val noteFinished: SharedFlow<Note> = _noteFinished.asSharedFlow()

    @Volatile
    private var muted = false
    //distinguishes "muted, and actively counting down to an auto-resume" from "muted, with no
    //resume armed yet" - a multi-note sequence (e.g. a melody) can have ordinary silent gaps
    //*between* notes that easily exceed QUIET_CONFIRM_MS; the countdown must only start once the
    //caller says the whole sequence is over (unmuteWhenQuiet()), not the instant mute() fires,
    //or an inter-note gap would auto-resume listening mid-sequence
    @Volatile
    private var waitingForQuiet = false
    @Volatile
    private var quietStreakStartMs: Long? = null

    override fun start() {
        if (pollJob != null) return
        pitchRecogniser.start()
        pollJob = scope.launch {
            // fixed-rate scheduling: track an absolute next-tick target and delay only the
            // remainder, so getPitch()/onSample()'s own (small) duration doesn't add up
            // on top of pollRateMs each iteration.
            var nextTick = System.currentTimeMillis()
            while (true) {
                val pitch = pitchRecogniser.getPitch().takeIf { it != PitchSource.UNDEFINED }
                val energy = pitchRecogniser.getEnergy()
                val now = System.currentTimeMillis()

                if (muted) {
                    if (waitingForQuiet) {
                        if (energy < quietEnergyFloor) {
                            val streakStart = quietStreakStartMs ?: now.also { quietStreakStartMs = it }
                            if (now - streakStart >= QUIET_CONFIRM_MS) {
                                muted = false
                                waitingForQuiet = false
                            }
                        } else {
                            quietStreakStartMs = null
                        }
                    }
                    //don't feed the detector at all while muted - nothing should get a chance to
                    //accumulate that could later surface as a misleading report once unmuted
                } else {
                    // emit() suspends until the collector is ready
                    when (val event = detector.onSample(pitch, energy, now)) {
                        is NoteEvent.Started -> _noteStarted.emit(event.note)
                        is NoteEvent.Finished -> _noteFinished.emit(event.note)
                        null -> {}
                    }
                }

                nextTick += POLL_RATE_MS
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

    override fun reset() {
        detector.reset()
    }

    override fun mute() {
        muted = true
        waitingForQuiet = false
        quietStreakStartMs = null
        detector.reset()
    }

    override fun unmuteWhenQuiet() {
        if (!muted) return
        waitingForQuiet = true
        quietStreakStartMs = null
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
