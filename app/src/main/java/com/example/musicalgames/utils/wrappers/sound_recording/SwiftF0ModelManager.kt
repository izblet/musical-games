package com.example.musicalgames.utils.wrappers.sound_recording

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context

/**
 * Wraps the SwiftF0 ONNX model (https://github.com/lars76/swift-f0) - unlike SPICE, it takes
 * raw audio of any length directly (no fixed input/output array sizes to keep in sync) and
 * returns pitch already in Hz alongside a 0-1 confidence, one pair per analysis frame.
 */
class SwiftF0ModelManager(modelBytes: ByteArray) {
    private val environment: OrtEnvironment = OrtEnvironment.getEnvironment()
    private val session: OrtSession = environment.createSession(modelBytes, OrtSession.SessionOptions())

    /** Runs the model on [audio] (mono, 16kHz, any length) - returns (pitch_hz, confidence),
     * one entry per analysis frame, in chronological order. */
    fun getPitchAndConfidence(audio: FloatArray): Pair<FloatArray, FloatArray> {
        OnnxTensor.createTensor(environment, arrayOf(audio)).use { inputTensor ->
            session.run(mapOf(INPUT_NAME to inputTensor)).use { result ->
                val pitchHz = (result.get(PITCH_OUTPUT_NAME).get().value as Array<*>)[0] as FloatArray
                val confidence = (result.get(CONFIDENCE_OUTPUT_NAME).get().value as Array<*>)[0] as FloatArray
                return pitchHz to confidence
            }
        }
    }

    fun close() {
        session.close()
    }

    companion object {
        private const val INPUT_NAME = "input_audio"
        private const val PITCH_OUTPUT_NAME = "pitch_hz"
        private const val CONFIDENCE_OUTPUT_NAME = "confidence"

        fun fromAssets(context: Context, modelFile: String): SwiftF0ModelManager {
            return SwiftF0ModelManager(context.assets.open(modelFile).use { it.readBytes() })
        }
    }
}
