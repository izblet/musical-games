package com.example.musicalgames.wrappers

import android.Manifest
import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import androidx.activity.result.ActivityResultRegistry
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.musicalgames.utils.MusicUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.PI
import kotlin.math.round
import kotlin.math.sin

class FallbackSoundPlayerManager(private var context: Context, private var activityRegistry: ActivityResultRegistry): SoundPlayerManager {
    private var audioTrack: AudioTrack? = null
    private val sampleRate = 44100 // Sample rate in Hz
    private val durationInSeconds = 3 // Duration of the sound in seconds
    private val bufferSize: Int
    private var buffer: ShortArray? = null
    private val permissions = arrayOf(
        Manifest.permission.MODIFY_AUDIO_SETTINGS,
        Manifest.permission.RECORD_AUDIO
        // Add other permissions as needed
    )
    override fun listPermissions():Array<String> {
        return permissions
    }

    init {
        bufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)
    }

    override fun play(frequency: Double) {
        if (audioTrack?.state == AudioTrack.STATE_INITIALIZED) {
            audioTrack?.stop()
            audioTrack?.release()
        }

        audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize,
            AudioTrack.MODE_STREAM
        )

        buffer = ShortArray(bufferSize)
        audioTrack?.play()

        val numSamples = durationInSeconds * sampleRate
        val angularFrequency = 2 * PI * frequency / sampleRate

        for (i in 0 until numSamples) {
            val sample = round(sin(angularFrequency * i) * Short.MAX_VALUE).toInt().toShort()
            buffer!![i % bufferSize] = sample

            if (i % bufferSize == 0) {
                audioTrack?.write(buffer!!, 0, bufferSize)
            }
        }

        // Write any remaining samples to the AudioTrack
        audioTrack?.write(buffer!!, 0, numSamples % bufferSize)
        runBlocking {
            launch {
                delay(500) // Delay for 500 milliseconds (half a second)
                stopSound()
            }
        }
    }
    override fun play(note: String) {
        play(MusicUtil.frequency(note))
    }
    override fun play(midiCode: Int) {
        play(MusicUtil.frequency(midiCode))
    }

    fun stopSound() {
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
    }

}