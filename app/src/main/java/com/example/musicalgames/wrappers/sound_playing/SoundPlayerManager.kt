package com.example.musicalgames.wrappers.sound_playing

import com.example.musicalgames.utils.Note

interface SoundPlayerManager  {
    fun play(midiCode: Int)
    fun play(note: String)
    fun play(frequency: Double)
    fun listPermissions():Array<String>
    suspend fun playSequence(sequence: List<Note>, listener: SoundPlayerListener)
}
