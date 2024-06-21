package com.tmiq.utils.time

import kotlin.time.Duration

class Timer(
    var duration: Duration,

    var started: TimeMarker = TimeMarker.now(),

    startPaused: Boolean = false
) : Comparable<Timer> {

    private var paused: TimeMarker? = null

    init {
        if (startPaused) {
            paused = started
        }
    }

    val ended get() = !remaining.isPositive()
    val remaining get() = duration - elapsed
    val elapsed get() = paused?.let { it - started } ?: started.passedSince()

    fun pause() {
        paused = TimeMarker.now()
    }

    fun resume() {
        paused?.let {
            started = it
            duration = it - started
            paused = null
        }
    }

    override fun compareTo(other: Timer): Int = remaining.compareTo(other.remaining)

}