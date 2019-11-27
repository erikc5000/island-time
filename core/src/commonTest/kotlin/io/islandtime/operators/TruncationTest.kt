package io.islandtime.operators

import io.islandtime.Time
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TruncationTest : AbstractIslandTimeTest() {
    @Test
    fun `truncatedToHours() removes components smaller than hours`() {
        assertEquals(
            Time(1, 0),
            Time(1, 2, 3, 4).truncatedToHours()
        )
    }

    @Test
    fun `truncatedToMinutes() removes components smaller than minutes`() {
        assertEquals(
            Time(1, 2),
            Time(1, 2, 3, 4).truncatedToMinutes()
        )
    }

    @Test
    fun `truncatedToSeconds() removes components smaller than seconds`() {
        assertEquals(
            Time(1, 2, 3),
            Time(1, 2, 3, 4).truncatedToSeconds()
        )
    }

    @Test
    fun `truncatedToMilliseconds() removes components smaller than milliseconds`() {
        assertEquals(
            Time(1, 2, 3, 444_000_000),
            Time(1, 2, 3, 444_555_666).truncatedToMilliseconds()
        )
    }

    @Test
    fun `truncatedToMicroseconds() removes components smaller than microseconds`() {
        assertEquals(
            Time(1, 2, 3, 444_555_000),
            Time(1, 2, 3, 444_555_666).truncatedToMicroseconds()
        )
    }
}