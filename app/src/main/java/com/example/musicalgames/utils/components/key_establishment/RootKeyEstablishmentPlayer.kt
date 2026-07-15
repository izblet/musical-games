package com.example.musicalgames.utils.components.key_establishment

import com.example.musicalgames.utils.wrappers.sound_playing.SoundPlayerManager

class RootKeyEstablishmentPlayer(private val rootMidi: Int, private val noteDuration: Long? = null) : KeyEstablishmentPlayer() {

    override fun play(soundPlayerManager: SoundPlayerManager) {
        onMessageChangeAction?.invoke("root note")
        soundPlayerManager.playNote(rootMidi, onEndAction, noteDuration)
    }
}