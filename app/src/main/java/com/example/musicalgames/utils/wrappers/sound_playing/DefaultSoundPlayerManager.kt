package com.example.musicalgames.utils.wrappers.sound_playing

import android.Manifest
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.example.musicalgames.music_model.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


class DefaultSoundPlayerManager(private val context: Context) : SoundPlayerManager {
    private val permissions = arrayOf(
        Manifest.permission.MODIFY_AUDIO_SETTINGS,
        Manifest.permission.RECORD_AUDIO
    )
    private var mediaPlayer: MediaPlayer? = null
    private val soundMap: MutableMap<Int, Int> = mutableMapOf()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var cutoffJob: Job? = null

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

    override fun playNote(note: String, listener: SoundPlayerListener?, durationMs: Long?) {
        val parsed = Note.parse(note) ?: throw IllegalArgumentException("Invalid note format $note")
        play(parsed.midiCode, durationMs) { listener?.onPlaybackFinished() }
    }

    override suspend fun playSequence(sequence: List<Note>, listener: SoundPlayerListener, durationMs: Long?) {
        for (note in sequence) {
            suspendCancellableCoroutine<Unit> { cont ->
                play(note.midiCode, durationMs) {
                    if (cont.isActive) cont.resume(Unit)
                }
            }
        }
        listener.onPlaybackFinished()
    }

    override fun playNote(midicode: Int, listener: SoundPlayerListener?, durationMs: Long?) {
        play(midicode, durationMs) { listener?.onPlaybackFinished() }
    }

    private fun play(midicode: Int, durationMs: Long? = null, onComplete: (() -> Unit)? = null) {
        val resId = soundMap[midicode]
        if (resId == null) {
            Log.e("SoundPlayer", "Sound resource not found for MIDI code: $midicode")
            onComplete?.invoke()
            return
        }

        try {
            releaseMediaPlayer()

            val player = MediaPlayer.create(context, resId)
            if (player == null) {
                onComplete?.invoke()
                return
            }
            mediaPlayer = player
            player.setOnErrorListener { _, what, extra ->
                Log.e("SoundPlayer", "MediaPlayer error: $what, $extra")
                false
            }
            player.setOnCompletionListener {
                cutoffJob?.cancel()
                releaseMediaPlayer()
                onComplete?.invoke()
            }
            player.start()

            if (durationMs != null) {
                cutoffJob = scope.launch {
                    delay(durationMs)
                    if (mediaPlayer === player) {
                        releaseMediaPlayer()
                        onComplete?.invoke()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SoundPlayer", "Error playing sound: ${e.message}")
            onComplete?.invoke()
        }
    }

    private fun releaseMediaPlayer() {
        cutoffJob?.cancel()
        cutoffJob = null
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
