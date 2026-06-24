package com.example.musicalgames.utils.wrappers.sound_recording

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
    //AudioRecord.getMinBufferSize() is a count of BYTES, sizing AudioRecord's internal
    //hardware/driver buffer - that's the only thing it should be used for. The per-read() chunk
    //size below is a separate, deliberately small value in SAMPLES (the unit the
    //read(ShortArray, offset, sizeInShorts) overload actually takes) - small chunks mean the
    //recording thread refills the ring buffer frequently in small increments instead of
    //blocking for one big chunk at a time, which matters for anything (e.g. onset detection)
    //that needs fresh audio at finer time resolution than that.
    private val hardwareBufferSizeBytes = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
    private val readChunkSamples = 320 // 20ms @ 16kHz

    private var recordingThread: Thread? = null
    @Volatile
    private var recording = false
    private val bufferDurationMs = 200 // Duration of the buffer in milliseconds
    private val ringBufferLength = sampleRate * bufferDurationMs / 1000 // Length of the buffer in samples
    private val audioBuffer = ShortArray(readChunkSamples)
    private val audioBufferRing = ShortArray(ringBufferLength)
    private var ringBufferIndex = 0
    private var ringBufferFull = 0
    private var audioRecord: AudioRecord? = null

    //TODO: stopRecording()/startRecording() doesn't reset ringBufferIndex/ringBufferFull, so
    //right after a restart this immediately returns a full buffer that's still (partly) old
    //audio from before the stop - stale for up to bufferDurationMs until fresh samples cycle in
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
                hardwareBufferSizeBytes
            )
            audioRecord?.startRecording()
            recording = true
            recordingThread = thread(start = true) {
                while (recording) {
                    val samplesRead = audioRecord?.read(audioBuffer, 0, readChunkSamples) ?: 0
                    // Update bufferIndex
                    for (i in 0 until samplesRead) {
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
    fun isRecording(): Boolean {
        return recording
    }

}