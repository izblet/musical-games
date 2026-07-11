package com.example.musicalgames.utils.components.key_establishment

import com.example.musicalgames.music_model.ChromaticNote
import com.example.musicalgames.music_model.Mode
import com.example.musicalgames.utils.wrappers.sound_playing.SoundPlayerManager
/**
 * Plays the chosen root establishment. Invokes onEndAction at the end. Invokes onMessageAction anytime it makes sense to tell the user what's happening,
 * the messages are supposed to correspond to what's being played
 **/
abstract class KeyEstablishmentPlayer() {
    var onEndAction: (()->Unit)? = null
    var onMessageChangeAction :((String)->Unit)? = null
    abstract fun play(soundPlayerManager: SoundPlayerManager)
}