package com.tmiq.utils.time

import java.sql.Time
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

class TimeMarker(val millis: Long) : Comparable<TimeMarker> {

    operator fun minus(other: TimeMarker) = (millis - other.millis).milliseconds

    operator fun plus(other: Duration) = TimeMarker(millis + other.inWholeMilliseconds)

    operator fun minus(other: Duration) = plus(-other)

    fun passedSince() = now() - this

    fun timeUntil() = -passedSince()

    fun isInFuture() = timeUntil().isPositive()

    fun isInPast() = timeUntil().isNegative()

    fun isFarPast() = millis == 0L

    fun isFarFuture() = millis == Long.MAX_VALUE

    override fun compareTo(other: TimeMarker): Int = millis.compareTo(other.millis)

    override fun toString(): String = when (this) {
        farPast() -> "The Far Past"
        farFuture() -> "The Far Future"
        else -> Instant.ofEpochMilli(millis).toString()
    }

    fun formattedDate(pattern: String): String {
        val newPattern =
            pattern

        val instant = Instant.ofEpochMilli(millis)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern(newPattern.trim())
        return localDateTime.format(formatter)
    }

    fun toMillis() = millis

    fun elapsedMinutes() = passedSince().inWholeMinutes

    companion object {

        fun now() = TimeMarker(System.currentTimeMillis())

        @JvmStatic
        @JvmName("farPast")
        fun farPast() = TimeMarker(0)
        fun farFuture() = TimeMarker(Long.MAX_VALUE)

        fun Duration.fromNow() = now() + this

        fun Long.asTimeMark() = TimeMarker(this)
    }



}