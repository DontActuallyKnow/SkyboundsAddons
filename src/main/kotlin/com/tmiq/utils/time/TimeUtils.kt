package com.tmiq.utils.time

object TimeUtils {


    /**
     * Milliseconds to seconds
     *
     * @param milliseconds
     * @return
     */
    fun milliToS(milliseconds: Long): Long {
        val seconds = milliseconds / 1000
        return seconds
    }

    /**
     * Milliseconds to minutes
     *
     * @param milliseconds
     * @return
     */
    fun milliToM(milliseconds: Long): Long {
        val minutes = milliseconds / 60000
        return minutes
    }

    /**
     * Seconds to milliseconds
     *
     * @param seconds
     * @return
     */
    fun sToMilli(seconds: Long): Long {
        val milliseconds = seconds * 1000
        return milliseconds
    }

    /**
     * Minutes and seconds to milliseconds
     *
     * @param minutes
     * @param seconds
     * @return
     */
    fun mAndSToMilli(minutes: Long, seconds: Long): Long {
        val totalMilliseconds = (minutes * 60 + seconds) * 1000
        return totalMilliseconds
    }

    /**
     * Minutes and seconds to seconds
     *
     * @param minutes
     * @param seconds
     * @return
     */
    fun mAndSToS(minutes: Long, seconds: Long): Long {
        val minutesInSeconds = minutes * 60
        val totalSeconds = minutesInSeconds + seconds
        return totalSeconds
    }

    /**
     * Milliseconds to minutes and seconds
     *
     * @param milliseconds
     * @return Pair<>() where the first is the minutes and the second is the seconds
     */
    fun milliToMAndS(milliseconds: Long): Pair<Long, Long> {
        val minutes = Math.floor((milliseconds / 60000).toDouble()).toLong()
        val seconds = (milliseconds % 60000) / 1000
        return Pair<Long, Long>(minutes, seconds)
    }
}