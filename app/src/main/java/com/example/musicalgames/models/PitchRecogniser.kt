package com.example.musicalgames.models

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
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

    private lateinit var interpreter: Interpreter
    init {
        loadModel(context, "spice.tflite")
    }

    private fun loadModel(context: Context, modelFile: String) {
        try {
            val model = loadModelFile(context, modelFile)
            var options = Interpreter.Options()
            options.setNumThreads(4)
            interpreter = Interpreter(model, options)
        } catch (e: Exception) {
            throw e
        }
    }

    private fun loadModelFile(context: Context, modelFile: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelFile)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength).apply {
        }
    }

    fun hzToOutput(hz: Double): Double {
        // Constants taken from the link you provided
        val PT_OFFSET = 25.58
        val PT_SLOPE = 63.07
        val FMIN = 10.0
        val BINS_PER_OCTAVE = 12.0

        val cqtBin = BINS_PER_OCTAVE * (Math.log(hz / FMIN) / Math.log(2.0)) - PT_OFFSET
        return (cqtBin / PT_SLOPE)
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

        val inputs = arrayOf<Any>(audioData)
        val outputs = HashMap<Int, Any>()

        //TODO: check if and whyyy
        val outputSize = 3
        val pitches = FloatArray(outputSize)
        val uncertainties = FloatArray(outputSize)
        outputs[0]=pitches
        outputs[1]=uncertainties

        try {
            interpreter.runForMultipleInputsOutputs(inputs, outputs)
        } catch (e: Exception) {
            Log.e("EXCEPTION", e.toString())
        }

        var result: Float ?= null
        var maxConfidence = 0f
        for(i in 0 until outputSize) {
            if(1f-uncertainties[i]>maxConfidence) {
                maxConfidence = 1f-uncertainties[i]
                result=pitches[i]
            }
        }
        if(result==null)
            return null

        val normalizedC4 = hzToOutput(261.63)
        val normalizedC5 = hzToOutput(523.25)
        val normalizedG3 = hzToOutput(195.99)
        val normalizedG4 = hzToOutput(392.995)

        val normalizedResult = (result-normalizedG3)/(normalizedG4-normalizedG3)

        return normalizedResult.toFloat()
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
        interpreter.close()
        audioRecord?.stop()
        audioRecord?.release()
    }
}
