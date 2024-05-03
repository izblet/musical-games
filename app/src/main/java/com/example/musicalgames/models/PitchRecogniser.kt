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
            Log.d("PitchRecogniser", "Loading model: $modelFile")
            val model = loadModelFile(context, modelFile)
            interpreter = Interpreter(model)
            Log.d("PitchRecogniser", "Model loaded successfully")
        } catch (e: Exception) {
            Log.e("PitchRecogniser", "Error loading model: ${e.message}", e)
            throw e
        }
    }

    private fun loadModelFile(context: Context, modelFile: String): MappedByteBuffer {
        Log.d("PitchRecogniser", "Loading model file: $modelFile")
        val fileDescriptor = context.assets.openFd(modelFile)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength).apply {
            Log.d("PitchRecogniser", "Model file loaded successfully")
        }
    }

    fun getPitch(): Float {
        val pitch = recognizePitch()
        return pitch ?: -1f // Return -1 if pitch recognition failed
    }

    private fun recognizePitch(): Float? {
        Log.d("PitchRecogniser", "Recognizing pitch")
        Log.d("PitchRecogniser", "Buffer size is $bufferSize")
        if (ringBufferFull < ringBufferLength) {
            Log.d("PitchRecogniser", "Not enough data in the buffer to recognize pitch: $ringBufferFull")
            return null
        }

        //we know that the ring buffer is full, so the start will be right after the end
        //ring buffer index points to the first element not set
        val start = ringBufferIndex
        val end = (ringBufferIndex-1 ) % ringBufferLength
        val audioData = {
            val temp = ShortArray(ringBufferLength)
            System.arraycopy(audioBufferRing, start, temp, 0, ringBufferLength - start)
            System.arraycopy(audioBufferRing, 0, temp, ringBufferLength - start, end)
            temp
        }

        Log.d("PitchRecogniser", "Pitch recognized successfully")
        // Placeholder function for pitch recognition
        // Replace this with your actual pitch recognition logic
        // Example: call your TensorFlow model to recognize pitch from audioData

        // For now, return a random value as a placeholder
        return (Math.random()).toFloat()
    }
    fun startRecording() {
        try {
            Log.d("PitchRecogniser", "Starting recording")
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
                    Log.d("PitchRecogniser", "Bytes read: $bytesRead")
                    // Update bufferIndex
                    for (i in 0 until bytesRead) {
                        audioBufferRing[ringBufferIndex] = audioBuffer[i]
                        ringBufferIndex = (ringBufferIndex + 1) % ringBufferLength
                        if(ringBufferFull<ringBufferLength)
                            ringBufferFull++
                    }
                    Log.d("PitchRecogniser", "Buffer index: $ringBufferIndex, Buffer length: $ringBufferLength")
                }
            }
        } catch (e: SecurityException) {
            Log.e("PitchRecogniser", "Security exception: ${e.message}", e)
            // Handle the case where the RECORD_AUDIO permission is not granted
            // You might want to inform the user or log an error message
            throw e
        } catch (e: Exception) {
            Log.e("PitchRecogniser", "Error starting recording: ${e.message}", e)
            throw e
        }
    }

    fun stopRecording() {
        Log.d("PitchRecogniser", "Stopping recording")
        recording = false
        recordingThread?.join() // Wait for the recording thread to finish
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

    fun release() {
        stopRecording()
        Log.d("PitchRecogniser", "Releasing resources")
        audioRecord?.stop()
        audioRecord?.release()
    }
}
