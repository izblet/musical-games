package com.example.musicalgames.models

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
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
    private val bufferLength = sampleRate * bufferDurationMs / 1000 // Length of the buffer in samples
    private val audioBuffer = ShortArray(bufferSize)
    private val audioBufferRing = ShortArray(bufferLength)
    private var bufferIndex = 0
    private var audioRecord: AudioRecord? = null

    private lateinit var interpreter: Interpreter
    init {
        loadModel(context, "spice.tflite")
    }
    private fun loadModel(context: Context, modelFile: String) {
        try {
            var model = loadModelFile(context, modelFile)
            interpreter = Interpreter(model)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun loadModelFile(context: Context, modelFile: String):MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelFile)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun getPitch(): Float {
        val pitch = recognizePitch()
        return pitch ?: -1f // Return -1 if pitch recognition failed
    }

    private fun recognizePitch(): Float? {
        // Access the last 64 ms of recorded audio from the ring buffer
        val start = (bufferIndex - bufferLength + bufferSize) % bufferSize
        val end = bufferIndex
        val audioData = if (start < end) {
            audioBufferRing.copyOfRange(start, end)
        } else {
            val temp = ShortArray(bufferSize - start + end)
            System.arraycopy(audioBufferRing, start, temp, 0, bufferSize - start)
            System.arraycopy(audioBufferRing, 0, temp, bufferSize - start, end)
            temp
        }

        // Placeholder function for pitch recognition
        // Replace this with your actual pitch recognition logic
        // Example: call your TensorFlow model to recognize pitch from audioData

        // For now, return a random value as a placeholder
        return (Math.random() * 1000).toFloat()
    }

    fun startRecording() {
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
                        // Copy audio data to the ring buffer
                        for (i in 0 until bytesRead) {
                            audioBufferRing[bufferIndex] = audioBuffer[i]
                            bufferIndex = (bufferIndex + 1) % bufferLength
                        }
                    }
                }
            } catch (e: SecurityException) {
                // Handle the case where the RECORD_AUDIO permission is not granted
                // You might want to inform the user or log an error message
                e.printStackTrace()
            }
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
        audioRecord?.stop()
        audioRecord?.release()
    }
}
