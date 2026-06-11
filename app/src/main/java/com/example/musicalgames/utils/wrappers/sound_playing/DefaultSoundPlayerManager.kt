package com.example.musicalgames.utils.wrappers.sound_playing

import android.Manifest
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.example.musicalgames.music_model.Note
import kotlinx.coroutines.delay


class DefaultSoundPlayerManager(private val context: Context) : SoundPlayerManager {
    private val permissions = arrayOf(
        Manifest.permission.MODIFY_AUDIO_SETTINGS,
        Manifest.permission.RECORD_AUDIO
    )
    private var mediaPlayer: MediaPlayer? = null
    private val soundMap: MutableMap<Int, Int> = mutableMapOf()

    override fun listPermissions(): Array<String> {
        return permissions
    }

    init {
        for (i in 36..76) { // MIDI codes range from 36 to 76
            val resId = context.resources.getIdentifier("note_$i", "raw", context.packageName)
            if (resId != 0) {
                soundMap[i] = resId
            } else {
                Log.w("SoundPlayer", "Resource not found for MIDI code: $i")
            }
        }
    }

    override fun playNote(frequency: Double, listener: SoundPlayerListener?) {
        TODO("Not implemented")
    }

    override fun playNote(note: String, listener: SoundPlayerListener?) {
        val parsed = Note.parse(note) ?: throw IllegalArgumentException("Invalid note format $note")
        play(parsed.midiCode)
        listener?.onPlaybackFinished()
    }
    override suspend fun playSequence(sequence: List<Note>, listener: SoundPlayerListener) {
        for(note in sequence) {
            play(note.midiCode)
            delay(700)
        }
        listener.onPlaybackFinished()
    }

    override fun playNote(midicode: Int, listener: SoundPlayerListener?) {
        play(midicode)
        listener?.onPlaybackFinished()
    }

    private fun play(midicode: Int) {
        val resId = soundMap[midicode]
        if (resId != null) {
            try {
                releaseMediaPlayer()

                mediaPlayer = MediaPlayer.create(context, resId)
                mediaPlayer?.setOnPreparedListener {
                    mediaPlayer?.start()
                }
                mediaPlayer?.setOnErrorListener { mp, what, extra ->
                    Log.e("SoundPlayer", "MediaPlayer error: $what, $extra")
                    false
                }
                mediaPlayer?.setOnCompletionListener {
                    releaseMediaPlayer()
                }
                mediaPlayer?.prepareAsync()
            } catch (e: Exception) {
                Log.e("SoundPlayer", "Error playing sound: ${e.message}")
            }
        } else {
            Log.e("SoundPlayer", "Sound resource not found for MIDI code: $midicode")
        }
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.reset()
            it.release()
        }
        mediaPlayer = null
    }
}
