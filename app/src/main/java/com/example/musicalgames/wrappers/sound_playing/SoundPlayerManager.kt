package com.example.musicalgames.wrappers.sound_playing

import com.example.musicalgames.utils.Note

interface SoundPlayerManager  {
    fun playNote(midiCode: Int, listener: SoundPlayerListener?=null)
    fun playNote(note: String, listener: SoundPlayerListener?=null)
    fun playNote(frequency: Double, listener: SoundPlayerListener?=null)
    fun listPermissions():Array<String>
    suspend fun playSequence(sequence: List<Note>, listener: SoundPlayerListener)
}
