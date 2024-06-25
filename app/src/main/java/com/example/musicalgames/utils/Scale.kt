package com.example.musicalgames.utils

enum class Scale {
    MAJOR,
    MINOR,
    HARMONIC,
    MELODIC_ASCENDING,
    CHROMATIC;

    companion object {
        private val intervalMap = mapOf(
            MAJOR to listOf(2, 2, 1, 2, 2, 2, 1),
            MINOR to listOf(2,1,2,2,1,2,2),
            HARMONIC to listOf(2,1,2,2,1,3,1),
            MELODIC_ASCENDING to listOf(2,1,2,2,2,2,1),
            CHROMATIC to listOf(1)
        )
    }

    fun getIntervals(): List<Int> {
        return intervalMap[this]!!
    }
}