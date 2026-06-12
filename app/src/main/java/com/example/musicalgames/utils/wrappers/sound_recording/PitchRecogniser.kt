package com.example.musicalgames.utils.wrappers.sound_recording

import android.content.Context
import com.example.musicalgames.R
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
                       minRecognised: String, maxRecognised: String) {
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

    init {
        SPICE = SPICEModelManager(context, context.getString(R.string.spice_model))
        microphone = MicrophoneManager()
    }

    companion object {
        const val THRESHOLD_ENERGY= 30000
        const val UNDEFINED=-1f
    }


    fun getPitch(): Float {
        return lastPitchSpice ?: UNDEFINED
    }
    private fun calculateEnergy(audioData: ShortArray): Float {
        var sum = 0.0
        for (sample in audioData) {
            sum += sample.toFloat().pow(2)
        }
        return (sum / audioData.size).toFloat()
    }
    private fun recognizePitch(): Float? {

        val buffer: ShortArray = microphone?.getBufferIfFull() ?: return null

        if(calculateEnergy(buffer)< THRESHOLD_ENERGY)
            return null

        val maxAbsValue = Short.MAX_VALUE.toFloat()
        val audioData :FloatArray = buffer.map { it.toFloat() / maxAbsValue }.toFloatArray()


        val outputSize = 7
        val result = SPICE?.getMeanDominantPitch(audioData, outputSize, outputSize)
            ?: return null

        if(result< minPitch || result> maxPitch)
            return null

        return result
    }

    fun start() {
        if(isActive)
            return
        isActive = true
        require(microphone!=null){"Microphone is not set for pitch recogniser"}

        if(!microphone!!.isRecording()) {
            microphone!!.startRecording()
        }

        updateJob = recogniserScope.launch {
            while(isActive) {
                lastPitchSpice = recognizePitch()
                delay(updateRateMS)
            }
        }
    }

    fun stop() {
        isActive = false
        updateJob?.cancel()
        microphone?.stopRecording()
    }

    fun release() {
        runBlocking { updateJob?.cancelAndJoin() }
        microphone?.stopRecording()
        SPICE?.close()
        microphone = null
        SPICE = null
        recogniserScope.cancel()
    }
}
