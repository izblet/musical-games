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
    val entryThresholdPercent: Int = DEFAULT_ENTRY_THRESHOLD_PERCENT,
    val exitThresholdPercent: Int = DEFAULT_EXIT_THRESHOLD_PERCENT,
    val windowMs: Long = DEFAULT_WINDOW_MS
) {
    companion object {
        const val DEFAULT_ENERGY_THRESHOLD = 30000
        const val DEFAULT_MAX_UNCERTAINTY = 0.15f
        const val DEFAULT_ENTRY_THRESHOLD_PERCENT = 95
        const val DEFAULT_EXIT_THRESHOLD_PERCENT = 10
        const val DEFAULT_WINDOW_MS = 300L
    }
}
