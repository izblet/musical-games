package com.example.musicalgames.games.flappy

import android.content.Context
import com.example.musicalgames.R
import com.example.musicalgames.wrappers.sound_recording.MicrophoneManager
import com.example.musicalgames.wrappers.sound_recording.SPICEModelManager
import com.example.musicalgames.games.MusicUtil as MU
import kotlin.math.pow

class PitchRecogniser (context: Context,
                       minRecognised: String, maxRecognised: String){
    private var SPICE: SPICEModelManager? = null
    private var microphone: MicrophoneManager? = null

    val minPitch = MU.spice(minRecognised)
    val maxPitch = MU.spice(maxRecognised)

    init {
        SPICE = SPICEModelManager(context, context.getString(R.string.spice_model))
        microphone = MicrophoneManager()
    }

    companion object {
        const val THRESHOLD_ENERGY= 30000
        const val UNDEFINED=-1f
    }


    fun getPitch(): Float {
        val pitch = recognizePitch()
        return pitch ?: UNDEFINED // Return -1 if pitch recognition failed
    }
    private fun calculateEnergy(audioData: ShortArray): Float {
        var sum = 0.0
        for (sample in audioData) {
            sum += sample.toFloat().pow(2)
        }
        return (sum / audioData.size).toFloat()
    }
    private fun recognizePitch(): Float? {

        var buffer: ShortArray = microphone?.getBufferIfFull() ?: return null

        if(calculateEnergy(buffer)< THRESHOLD_ENERGY)
            return null

        val maxAbsValue = Short.MAX_VALUE.toFloat() // 32767
        val audioData :FloatArray = buffer.map { it.toFloat() / maxAbsValue }.toFloatArray()


        val outputSize = 3
        val result = SPICE?.getDominantPitch(audioData, outputSize, outputSize)
            ?: return null

        if(result< minPitch || result> maxPitch)
            return null

        return result
    }
    fun start() {
        microphone?.startRecording()
    }

    fun release() {
        microphone?.stopRecording()
        SPICE?.close()
    }
}
