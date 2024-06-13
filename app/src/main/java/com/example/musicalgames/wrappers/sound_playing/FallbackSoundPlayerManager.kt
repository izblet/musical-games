package com.example.musicalgames.wrappers.sound_playing

import android.Manifest
import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import com.example.musicalgames.utils.MusicUtil
import kotlin.math.PI
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.sin

class FallbackSoundPlayerManager(private var context: Context) : SoundPlayerManager {
    private var audioTrack: AudioTrack? = null
    private val sampleRate = 44100 // Sample rate in Hz
    private val durationInSeconds = 3 // Duration of the sound in seconds
    private var buffer: ShortArray? = null
    private val permissions = arrayOf(
        Manifest.permission.MODIFY_AUDIO_SETTINGS,
        Manifest.permission.RECORD_AUDIO
    )

    override fun listPermissions(): Array<String> {
        return permissions
    }

    init {
        val bufferSizeInBytes = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val bufferSizeInShorts = bufferSizeInBytes / 2
        buffer = ShortArray(bufferSizeInShorts)
    }

    override fun play(frequency: Double) {
        stopSound()

        audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            buffer!!.size * 2,
            AudioTrack.MODE_STREAM
        )

        audioTrack?.play()

        val angularFrequency = 2 * PI * frequency / sampleRate
        val numSamples = buffer!!.size

        for (i in buffer!!.indices) {
            val amplitudeFactor = getAmplitudeFactor(frequency)
            val sample = (amplitudeFactor*sin(angularFrequency * i) * Short.MAX_VALUE * (1 - i.toFloat() / numSamples)).toInt().toShort()
            buffer!![i] = sample
        }

        audioTrack?.write(buffer!!, 0, buffer!!.size)
    }

    private fun getAmplitudeFactor(frequency: Double): Double {
        val factor = (log2(MusicUtil.frequency("C3"))/log2(frequency)).pow(3)
        return factor
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
