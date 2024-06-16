package com.example.musicalgames.wrappers.sound_recording

import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.lang.Exception
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.abs

class SPICEModelManager (context: Context, modelFile: String){

    private lateinit var interpreter: Interpreter
    init
    {
        loadModel(context, modelFile)
    }

    fun loadModel(context: Context, modelFile: String) {
        try {
            val model = loadModelFile(context, modelFile)
            var options = Interpreter.Options()
            options.setNumThreads(4)
            interpreter = Interpreter(model, options)
        } catch (e: Exception) {
            throw e
        }
    }
    private fun getMean(notes: MutableList<Float>): Float {
        return notes.average().toFloat()
    }
    private fun getStandardDeviation(notes: MutableList<Float>, mean: Float): Float {
        return notes.map{abs(it-mean)}.average().toFloat()
    }
    fun getDominantPitch(data: FloatArray, inputSize:Int, outputSize:Int):Float? {
        val inputs = arrayOf<Any>(data)
        val outputs = HashMap<Int, Any>()
        val pitches = FloatArray(inputSize)
        val uncertainties = FloatArray(outputSize)
        outputs[0]=pitches
        outputs[1]=uncertainties

        try {
            interpreter.runForMultipleInputsOutputs(inputs, outputs)
        } catch (e: Exception) {
            Log.e("EXCEPTION", e.toString())
        }

        //first remove values that are too uncertain
        val maxUncertainty = 0.15f
        val results: MutableList<Float> = mutableListOf()

        for(i in 0 until outputSize) {
            if(uncertainties[i]<maxUncertainty)
                results.add(pitches[i])
        }
        //Log.e("Spice", results.size.toString())
        if(results.size == 0)
            return null

        //then remove values that seem uncorrect (are too far from the mean)
        val mean = getMean(results)
        val standardDeviation = getStandardDeviation(results, mean)
        results.removeAll { result -> (abs(result-mean)>2*standardDeviation)  }

        //Log.e("Spice", results.size.toString())
        //Log.e("Spice", getMean(results).toString())
        //results cannot be empty if everything before works as expected
        return getMean(results)

        /*
        var result: Float ?= null
        var maxConfidence = 0f
        for(i in 0 until outputSize) {
            if(1f-uncertainties[i]>maxConfidence) {
                maxConfidence = 1f-uncertainties[i]
                result=pitches[i]
            }
        }
        return result

         */
    }

    private fun loadModelFile(context: Context, modelFile: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelFile)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength).apply {
        }
    }
    fun close() {
        interpreter.close()
    }

}