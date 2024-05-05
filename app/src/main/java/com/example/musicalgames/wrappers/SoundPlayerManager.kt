package com.example.musicalgames.wrappers

interface SoundPlayerManager  {
    fun play(midiCode: Int)
    fun play(note: String)
    fun play(frequency: Double)
    fun listPermissions():Array<String>
}
