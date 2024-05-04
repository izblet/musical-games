package com.example.musicalgames.models

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import com.example.musicalgames.wrappers.SPICEModelManager
import com.example.musicalgames.utils.MusicUtil as MU
import kotlin.concurrent.thread
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.lang.Exception
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.pow

class PitchRecogniser (context: Context){

    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    private var recordingThread: Thread? = null
    private var recording = false
    private val bufferDurationMs = 64 // Duration of the buffer in milliseconds
    private val ringBufferLength = sampleRate * bufferDurationMs / 1000 // Length of the buffer in samples
    private val audioBuffer = ShortArray(bufferSize)
    private val audioBufferRing = ShortArray(ringBufferLength)
    private var ringBufferIndex = 0
    private var ringBufferFull = 0
    private var audioRecord: AudioRecord? = null

    private var SPICE: SPICEModelManager? = null

    init {
        SPICE = SPICEModelManager(context, "spice.tflite")
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
        if (ringBufferFull < ringBufferLength) {
            return null
        }

        //we know that the ring buffer is full, so the start will be right after the end
        //ring buffer index points to the first element not set
        val start = ringBufferIndex
        val temp = ShortArray(ringBufferLength)
        System.arraycopy(audioBufferRing, start, temp, 0, ringBufferLength - start)
        if(start!=0)
            System.arraycopy(audioBufferRing, 0, temp, ringBufferLength - start, start)

        if(calculateEnergy(temp)<2500)
            return null

        val maxAbsValue = Short.MAX_VALUE.toFloat() // 32767
        val audioData :FloatArray = temp.map { it.toFloat() / maxAbsValue }.toFloatArray()

        //HERE STARTS THE CODE THAT CAN BE MOVED

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
    fun startRecording() {
        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )
            audioRecord?.startRecording()
            recording = true
            recordingThread = thread(start = true) {
                while (recording) {
                    val bytesRead = audioRecord?.read(audioBuffer, 0, bufferSize) ?: 0
                    // Update bufferIndex
                    for (i in 0 until bytesRead) {
                        audioBufferRing[ringBufferIndex] = audioBuffer[i]
                        ringBufferIndex = (ringBufferIndex + 1) % ringBufferLength
                        if(ringBufferFull<ringBufferLength)
                            ringBufferFull++
                    }
                }
            }
        } catch (e: SecurityException) {
            // Handle the case where the RECORD_AUDIO permission is not granted
            // You might want to inform the user or log an error message
            throw e
        } catch (e: Exception) {
            throw e
        }
    }

    fun stopRecording() {
        recording = false
        recordingThread?.join() // Wait for the recording thread to finish
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

    fun release() {
        stopRecording()
        SPICE?.close()
        audioRecord?.stop()
        audioRecord?.release()
    }
}
