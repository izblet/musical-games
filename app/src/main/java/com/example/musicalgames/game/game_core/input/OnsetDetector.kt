package com.example.musicalgames.game.game_core.input

import kotlin.math.exp

/**
 * Detects amplitude attacks (onsets) in a stream of energy samples, independent of pitch -
 * fires when the signal jumps sharply above its own recent background level, e.g. a note being
 * (re-)articulated. Tracks the background as an exponential moving average whose time constant
 * is derived from [refractoryMs]: baseline is ~99% converged to a new sustained level by the
 * time the refractory window clears. That's deliberate, not incidental - if baseline were still
 * catching up when refractory reopens, the note's own ordinary sustain (no new attack at all)
 * could look like a "rise" relative to a baseline that's structurally still too low, firing a
 * spurious second onset on a single held note.
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
        //time-constant multiple for exponential convergence to ~99% (ln(100) ~= 4.6) - fixed,
        //not user-tunable; it's what makes the baseline's convergence track refractoryMs
        //automatically, whatever the latter is set to
        private const val CONVERGENCE_FACTOR = 4.6f
    }

    private val baselineTimeConstantMs = refractoryMs / CONVERGENCE_FACTOR

    private var baseline: Float = 0f
    private var lastOnsetMs: Long = 0
    private var lastSampleMs: Long? = null

    /** Feed one energy sample; returns true exactly on ticks where a new onset is detected. */
    fun onEnergySample(energy: Float, timestampMs: Long): Boolean {
        val isOnset = energy >= energyFloor &&
            energy >= baseline * riseFactor &&
            (timestampMs - lastOnsetMs) >= refractoryMs

        val previousSampleMs = lastSampleMs
        lastSampleMs = timestampMs
        if (previousSampleMs == null) {
            //nothing to converge from yet - start the baseline at the first real reading
            //instead of chasing up from an arbitrary 0
            baseline = energy
        } else {
            val deltaMs = (timestampMs - previousSampleMs).coerceAtLeast(0)
            val alpha = 1f - exp(-deltaMs / baselineTimeConstantMs)
            baseline += alpha * (energy - baseline)
        }

        if (isOnset) lastOnsetMs = timestampMs
        return isOnset
    }
}
