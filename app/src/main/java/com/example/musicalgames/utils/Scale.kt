package com.example.musicalgames.utils

enum class Scale {
    MAJOR,
    MINOR,
    HARMONIC,
    MELODIC_ASCENDING,
    CHROMATIC;

    companion object {
        private val w = Interval.M2
        private val h = Interval.m2
        private val aug = Interval.m3

        private val intervalMap = mapOf(
            MAJOR to listOf(w, w, h, w, w, w, h),
            MINOR to listOf(w,h,w,w,h,w,w),
            HARMONIC to listOf(w,h,w,w,h,aug,h),
            MELODIC_ASCENDING to listOf(w,h,w,w,w,w,h),
            CHROMATIC to List(11) {h}
        )
        private val degreeMap = mapOf(
            MAJOR to getDegreeList(MAJOR),
            MINOR to getDegreeList(MINOR),
            HARMONIC to getDegreeList(HARMONIC),
            MELODIC_ASCENDING to getDegreeList(MELODIC_ASCENDING),
            CHROMATIC to getDegreeList(CHROMATIC)
        )
        private fun getDegreeList(s: Scale) : List<Interval> {
            val result = mutableListOf<Interval>()
            var lastSemitones = 0
            for(interval in s.getIntervals()) {
                result.add(Interval.fromSemitones(lastSemitones))
                lastSemitones+=interval.getSemitones()
            }
            return result
        }
    }

    fun getIntervals(): List<Interval> {
        return intervalMap[this]!!
    }
    fun getDegrees(): List<Interval> {
        //with 0, without P8
        return degreeMap[this]!!
    }
}