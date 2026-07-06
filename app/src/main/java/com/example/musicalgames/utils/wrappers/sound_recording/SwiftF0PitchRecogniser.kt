package com.example.musicalgames.utils.wrappers.sound_recording

import android.content.Context
import com.example.musicalgames.R
import com.example.musicalgames.game.game_core.input.PitchSource
import com.example.musicalgames.music_model.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import com.example.musicalgames.music_model.MusicUtil as MU
import kotlin.math.pow

/**
 * [PitchSource] backed by SwiftF0 (https://github.com/lars76/swift-f0) rather than SPICE -
 * same polling shape as [PitchRecogniser], but the model already returns Hz and a 0-1
 * confidence directly (no fixed-size arrays, no bin-calibration decode), so filtering happens
 * in Hz/confidence space before the single surviving value is converted into the same "spice"
 * unit [PitchRecogniser] returns, so downstream code (MicrophoneNoteDetector etc.) doesn't
 * need to know which model produced it.
 */
class SwiftF0PitchRecogniser(
    context: Context,
    minRecognised: String, maxRecognised: String,
    private val energyThreshold: Int,
    private val minConfidence: Float
) : PitchSource {
    private var swiftF0: SwiftF0ModelManager? = null
    private var microphone: MicrophoneManager? = null

    private val minPitchHz = Note.parse(minRecognised)!!.frequency
    private val maxPitchHz = Note.parse(maxRecognised)!!.frequency

    private var updateRateMS = 1000L / 60
    private var isActive = false

    private val recogniserScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var updateJob: Job? = null

    @Volatile
    private var lastPitchSpice: Float? = null
    @Volatile
    private var lastFastEnergy: Float = 0f

    init {
        swiftF0 = SwiftF0ModelManager.fromAssets(context, context.getString(R.string.swiftf0_model))
        microphone = MicrophoneManager()
    }

    companion object {
        //trailing slice of the 200ms buffer used for the fast/local energy reading that feeds
        //onset detection - mirrors PitchRecogniser's own constant of the same name
        private const val FAST_ENERGY_WINDOW_SAMPLES = 320 //20ms @ 16kHz
    }

    override fun getPitch(): Float {
        return lastPitchSpice ?: PitchSource.UNDEFINED
    }

    /** Fast, local energy reading (last ~20ms), for onset detection - distinct from the
     * whole-buffer energy gate inside [recognizePitch], which is intentionally slower/smoother. */
    override fun getEnergy(): Float {
        return lastFastEnergy
    }

    private fun calculateEnergy(audioData: ShortArray): Float {
        var sum = 0.0
        for (sample in audioData) {
            sum += sample.toFloat().pow(2)
        }
        return (sum / audioData.size).toFloat()
    }

    private fun calculateFastEnergy(audioData: ShortArray): Float {
        if (audioData.size <= FAST_ENERGY_WINDOW_SAMPLES) return calculateEnergy(audioData)
        return calculateEnergy(audioData.copyOfRange(audioData.size - FAST_ENERGY_WINDOW_SAMPLES, audioData.size))
    }

    private fun recognizePitch(buffer: ShortArray): Float? {
        if (calculateEnergy(buffer) < energyThreshold)
            return null

        val maxAbsValue = Short.MAX_VALUE.toFloat()
        val audioData: FloatArray = buffer.map { it.toFloat() / maxAbsValue }.toFloatArray()

        val (pitchHz, confidence) = swiftF0?.getPitchAndConfidence(audioData) ?: return null

        val survivors = pitchHz.indices.filter { i ->
            confidence[i] > minConfidence && pitchHz[i] >= minPitchHz && pitchHz[i] <= maxPitchHz
        }
        if (survivors.isEmpty())
            return null

        val meanHz = survivors.sumOf { pitchHz[it].toDouble() } / survivors.size
        return MU.spice(meanHz).toFloat()
    }

    override fun start() {
        if (isActive)
            return
        isActive = true
        require(microphone != null) { "Microphone is not set for pitch recogniser" }

        if (!microphone!!.isRecording()) {
            microphone!!.startRecording()
        }

        updateJob = recogniserScope.launch {
            while (isActive) {
                val buffer = microphone?.getBufferIfFull()
                //fast energy must update every tick - including ticks where pitch comes back
                //null/filtered - since onset detection needs to see the attack that precedes a
                //confident pitch read, not just the ticks where one was found
                lastFastEnergy = buffer?.let(::calculateFastEnergy) ?: 0f
                lastPitchSpice = buffer?.let(::recognizePitch)
                delay(updateRateMS)
            }
        }
    }

    override fun stop() {
        isActive = false
        updateJob?.cancel()
        microphone?.stopRecording()
    }

    override fun release() {
        runBlocking { updateJob?.cancelAndJoin() }
        microphone?.stopRecording()
        swiftF0?.close()
        microphone = null
        swiftF0 = null
        recogniserScope.cancel()
    }
}
