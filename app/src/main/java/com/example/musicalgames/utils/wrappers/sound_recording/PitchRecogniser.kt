package com.example.musicalgames.utils.wrappers.sound_recording

import android.content.Context
import com.example.musicalgames.R
import com.example.musicalgames.game.game_core.input.PitchSource
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

class PitchRecogniser (context: Context,
                       minRecognised: String, maxRecognised: String,
                       private val energyThreshold: Int,
                       private val maxUncertainty: Float) : PitchSource {
    private var SPICE: SPICEModelManager? = null
    private var microphone: MicrophoneManager? = null

    val minPitch = MU.spice(minRecognised)
    val maxPitch = MU.spice(maxRecognised)

    private var updateRateMS =1000L/60
    private var isActive = false

    private val recogniserScope = CoroutineScope(SupervisorJob()+Dispatchers.Default)
    private var updateJob: Job? = null

    @Volatile
    private var lastPitchSpice: Float? = null
    @Volatile
    private var lastFastEnergy: Float = 0f

    init {
        SPICE = SPICEModelManager(context, context.getString(R.string.spice_model))
        microphone = MicrophoneManager()
    }

    companion object {
        const val UNDEFINED=-1f
        //trailing slice of the 200ms buffer used for the fast/local energy reading that feeds
        //onset detection - short enough that a real attack's energy rise shows up almost
        //immediately, instead of being smeared across the whole (much longer) pitch buffer
        private const val FAST_ENERGY_WINDOW_SAMPLES = 320 //20ms @ 16kHz
    }


    override fun getPitch(): Float {
        return lastPitchSpice ?: UNDEFINED
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

        if(calculateEnergy(buffer)< energyThreshold)
            return null

        val maxAbsValue = Short.MAX_VALUE.toFloat()
        val audioData :FloatArray = buffer.map { it.toFloat() / maxAbsValue }.toFloatArray()


        val outputSize = 7
        val result = SPICE?.getMeanDominantPitch(audioData, outputSize, outputSize, maxUncertainty)
            ?: return null

        if(result< minPitch || result> maxPitch)
            return null

        return result
    }

    override fun start() {
        if(isActive)
            return
        isActive = true
        require(microphone!=null){"Microphone is not set for pitch recogniser"}

        if(!microphone!!.isRecording()) {
            microphone!!.startRecording()
        }

        updateJob = recogniserScope.launch {
            while(isActive) {
                //TODO: fixed-delay scheduling - recognizePitch() (mic buffer read + SPICE
                //model inference, not free) runs before the delay, so the actual period is
                //recognizePitch()'s duration + updateRateMS, not a clean updateRateMS. If
                //precise timing ever matters here, switch to fixed-rate scheduling (track an
                //absolute next-tick target and delay only the remainder) - see
                //MicrophoneNoteInput's polling loop for that pattern.
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
        SPICE?.close()
        microphone = null
        SPICE = null
        recogniserScope.cancel()
    }
}
