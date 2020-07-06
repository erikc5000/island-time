package io.islandtime.operators

import io.islandtime.Time
import io.islandtime.measures.TimeUnit.*
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TruncationTest : AbstractIslandTimeTest() {
    @Test
    fun `truncatedTo(DAYS) returns midnight`() {
        assertEquals(
            Time.MIDNIGHT,
            Time(0, 0, 0, 1).truncatedTo(DAYS)
        )
    }

    @Test
    fun `truncatedTo(HOURS) removes components smaller than hours`() {
        assertEquals(
            Time(1, 0),
            Time(1, 2, 3, 4).truncatedTo(HOURS)
        )
    }

    @Test
    fun `truncatedTo(MINUTES) removes components smaller than minutes`() {
        assertEquals(
            Time(1, 2),
            Time(1, 2, 3, 4).truncatedTo(MINUTES)
        )
    }

    @Test
    fun `truncatedTo(SECONDS) removes components smaller than seconds`() {
        assertEquals(
            Time(1, 2, 3),
            Time(1, 2, 3, 4).truncatedTo(SECONDS)
        )
    }

    @Test
    fun `truncatedTo(MILLISECONDS) removes components smaller than milliseconds`() {
        assertEquals(
            Time(1, 2, 3, 444_000_000),
            Time(1, 2, 3, 444_555_666).truncatedTo(MILLISECONDS)
        )
    }

    @Test
    fun `truncatedTo(MICROSECONDS) removes components smaller than microseconds`() {
        assertEquals(
            Time(1, 2, 3, 444_555_000),
            Time(1, 2, 3, 444_555_666).truncatedTo(MICROSECONDS)
        )
    }

    @Test
    fun `truncatedTo(NANOSECONDS) does nothing`() {
        assertEquals(
            Time(1, 2, 3, 444_555_666),
            Time(1, 2, 3, 444_555_666).truncatedTo(NANOSECONDS)
        )
    }
}