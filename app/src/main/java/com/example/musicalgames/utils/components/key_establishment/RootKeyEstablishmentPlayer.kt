package com.example.musicalgames.utils.components.key_establishment

import com.example.musicalgames.utils.wrappers.sound_playing.SoundPlayerManager

class RootKeyEstablishmentPlayer(private val rootMidi: Int, private val noteDuration: Long = 1000) : KeyEstablishmentPlayer() {

    override fun play(soundPlayerManager: SoundPlayerManager) {
        soundPlayerManager.playNote(rootMidi, onEndAction, noteDuration)
        onMessageChangeAction?.invoke("root note")
    }
}