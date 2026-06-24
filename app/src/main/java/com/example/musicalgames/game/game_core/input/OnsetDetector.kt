package com.example.musicalgames.game.game_core.input

import android.util.Log

/**
 * Detects amplitude attacks (onsets) in a stream of energy samples, independent of pitch -
 * fires when the signal jumps sharply above its own recent background level, e.g. a note being
 * (re-)articulated. Tracks the background as an asymmetric exponential moving average: it rises
 * slowly, so a genuine attack doesn't get absorbed into the baseline before it can be detected
 * against it, but decays quickly, so the baseline is ready to detect the next attack soon after
 * one note's energy settles.
 *
 * Pure and Android/coroutine-free, like [MicrophoneNoteDetector] - just timestamped Float
 * samples in, Boolean onset events out.
 */
class OnsetDetector(
    private val energyFloor: Float,
    private val riseFactor: Float,
    private val refractoryMs: Long
) {
    companion object {
        //fixed, not user-tunable (unlike the constructor params above) - these shape how fast
        //the background estimate tracks the signal, not how sensitive onset detection is
        private const val ATTACK_ALPHA = 0.1f
        private const val RELEASE_ALPHA = 0.3f
    }

    private var baseline: Float = 0f
    private var lastOnsetMs: Long = 0

    /** Feed one energy sample; returns true exactly on ticks where a new onset is detected. */
    fun onEnergySample(energy: Float, timestampMs: Long): Boolean {
        val isOnset = energy >= energyFloor &&
            energy >= baseline * riseFactor &&
            (timestampMs - lastOnsetMs) >= refractoryMs

        val alpha = if (energy > baseline) ATTACK_ALPHA else RELEASE_ALPHA
        baseline += alpha * (energy - baseline)

        if (isOnset) lastOnsetMs = timestampMs
        return isOnset
    }
}
