package com.example.musicalgames.wrappers.sound_recording

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlin.concurrent.thread
import kotlin.jvm.Throws

class MicrophoneManager {
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

    fun getBufferIfFull(): ShortArray? {
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
        return temp
    }

    @SuppressLint("MissingPermission")
    @Throws
    fun startRecording() {
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
    }
    fun stopRecording() {
        recording = false
        recordingThread?.join() // Wait for the recording thread to finish
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

}