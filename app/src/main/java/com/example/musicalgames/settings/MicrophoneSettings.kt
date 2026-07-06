package com.example.musicalgames.settings

/**
 * Tunable parameters for the external-instrument (microphone) input pipeline -
 * how loud a sound must be to attempt recognition, how confident the pitch model
 * must be, and how long/decisively a pitch must hold before it's treated as a
 * recognised note. These are sensitive to the player's device/room, so they're
 * exposed for tuning rather than fixed.
 */
data class MicrophoneSettings(
    val energyThreshold: Int = DEFAULT_ENERGY_THRESHOLD,
    val maxUncertainty: Float = DEFAULT_MAX_UNCERTAINTY,
    val minConfidence: Float = DEFAULT_MIN_CONFIDENCE,
    val entryThresholdPercent: Int = DEFAULT_ENTRY_THRESHOLD_PERCENT,
    val exitThresholdPercent: Int = DEFAULT_EXIT_THRESHOLD_PERCENT,
    val windowMs: Long = DEFAULT_WINDOW_MS,
    val onsetRiseFactor: Float = DEFAULT_ONSET_RISE_FACTOR,
    val onsetRefractoryMs: Long = DEFAULT_ONSET_REFRACTORY_MS
) {
    companion object {
        const val DEFAULT_ENERGY_THRESHOLD = 30000
        const val DEFAULT_MAX_UNCERTAINTY = 0.15f
        //SwiftF0's confidence is the opposite sense of maxUncertainty above (higher = better,
        //not lower = better), so it needs its own field rather than reinterpreting that one -
        //0.9 matches SwiftF0's own published default voicing threshold
        const val DEFAULT_MIN_CONFIDENCE = 0.9f
        const val DEFAULT_ENTRY_THRESHOLD_PERCENT = 95
        const val DEFAULT_EXIT_THRESHOLD_PERCENT = 10
        const val DEFAULT_WINDOW_MS = 300L
        //instant energy must clear this multiple of the adaptive baseline to count as an attack
        const val DEFAULT_ONSET_RISE_FACTOR = 2.0f
        //minimum gap between onsets, to debounce vibrato/tremolo within one attack
        const val DEFAULT_ONSET_REFRACTORY_MS = 120L
    }
}
