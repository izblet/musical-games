package com.example.musicalgames.models

import android.content.Context
import com.example.musicalgames.wrappers.MicrophoneManager
import com.example.musicalgames.wrappers.SPICEModelManager
import com.example.musicalgames.utils.MusicUtil as MU
import kotlin.math.pow

class PitchRecogniser (context: Context){

    private var SPICE: SPICEModelManager? = null
    private var microphone: MicrophoneManager? = null

    init {
        SPICE = SPICEModelManager(context, "spice.tflite")
        microphone = MicrophoneManager()
    }

    fun getPitch(): Float {
        val pitch = recognizePitch()
        return pitch ?: -1f // Return -1 if pitch recognition failed
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

        if(calculateEnergy(buffer)<2500)
            return null

        val maxAbsValue = Short.MAX_VALUE.toFloat() // 32767
        val audioData :FloatArray = buffer.map { it.toFloat() / maxAbsValue }.toFloatArray()


        //TODO: check if and whyyy
        val outputSize = 3
        val result = SPICE?.getDominantPitch(audioData, outputSize, outputSize)
            ?: return null

        val minListenedFrequency = MU.spice("C2")
        val maxListenedFrequency = MU.spice("C6")
        val minScreenFreq = (MU.spice("G3")+MU.spice("F#3"))/2
        val maxScreenFreq = (MU.spice("G4")+MU.spice("G#4"))/2

        if(result< minListenedFrequency || result> maxListenedFrequency)
            return null

        val rangeNormalizedResult = (result-minScreenFreq)/(maxScreenFreq-minScreenFreq)

        return rangeNormalizedResult.toFloat()
    }
    fun start() {
        microphone?.startRecording()
    }

    fun release() {
        microphone?.stopRecording()
        SPICE?.close()
    }
}
