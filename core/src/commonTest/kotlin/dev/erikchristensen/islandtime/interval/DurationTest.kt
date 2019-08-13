package dev.erikchristensen.islandtime.interval

import kotlin.test.Test

class DurationTest {
    @Test
    fun `adding minutes to hours`() {
        val duration = durationOf(1.hours.toLong(), 30.seconds.toLong())
//        assertEquals(duration.seconds)
    }
}