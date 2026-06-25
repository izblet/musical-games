package com.example.musicalgames.game.game_core.input

/**
 * A live source of raw pitch+energy samples, polled rather than pushed - what
 * [MicrophoneNoteInput] actually needs from whichever pitch-recognition model backs it
 * (e.g. SPICE, SwiftF0), without depending on which one it is.
 */
interface PitchSource {
    companion object {
        const val UNDEFINED = -1f
    }

    fun start()
    fun stop()

    /** Last recognised pitch, or [UNDEFINED] for "no clear pitch this sample" (e.g. silence). */
    fun getPitch(): Float

    /** Fast, local energy reading suitable for onset detection. */
    fun getEnergy(): Float

    /** Releases the underlying model/microphone resources - the source is unusable afterwards. */
    fun release()
}
