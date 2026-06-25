package com.example.musicalgames.utils.wrappers.sound_recording

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import kotlin.math.PI
import kotlin.math.sin

class SwiftF0ModelManagerTest {

    private val sampleRate = 16000

    // no Android Context in a plain JVM test, so load the asset straight off disk
    private fun loadManager(): SwiftF0ModelManager {
        return SwiftF0ModelManager(File("src/main/assets/swiftf0.onnx").readBytes())
    }

    private fun tone(freqHz: Double, durationSeconds: Double = 0.3): FloatArray {
        val n = (sampleRate * durationSeconds).toInt()
        return FloatArray(n) { i ->
            val t = i / sampleRate.toDouble()
            (0.8 * sin(2 * PI * freqHz * t)).toFloat()
        }
    }

    @Test
    fun `recognises E5, the note SPICE fails on`() {
        val manager = loadManager()
        val freqHz = 659.25 // E5
        val (pitchHz, confidence) = manager.getPitchAndConfidence(tone(freqHz))
        manager.close()

        assertTrue("expected pitch frames, got none", pitchHz.isNotEmpty())
        val meanPitch = pitchHz.average()
        val meanConfidence = confidence.average()
        assertTrue(
            "expected mean pitch near $freqHz, got $meanPitch",
            kotlin.math.abs(meanPitch - freqHz) < freqHz * 0.05
        )
        assertTrue("expected high confidence, got $meanConfidence", meanConfidence > 0.9)
    }

    @Test
    fun `recognises the upper register SPICE struggled with`() {
        val manager = loadManager()
        // C5-C6: the register where SPICE's own uncertainty gate started rejecting frames
        for (freqHz in listOf(523.25, 659.25, 880.0, 1046.5)) {
            val (pitchHz, confidence) = manager.getPitchAndConfidence(tone(freqHz))
            val meanPitch = pitchHz.average()
            val meanConfidence = confidence.average()
            assertTrue(
                "freq $freqHz: expected mean pitch within 5%, got $meanPitch",
                kotlin.math.abs(meanPitch - freqHz) < freqHz * 0.05
            )
            assertTrue("freq $freqHz: expected high confidence, got $meanConfidence", meanConfidence > 0.9)
        }
        manager.close()
    }
}
