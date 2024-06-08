package com.example.musicalgames.wrappers.sound_playing

interface SoundPlayerManager  {
    fun play(midiCode: Int)
    fun play(note: String)
    fun play(frequency: Double)
    fun listPermissions():Array<String>
}
