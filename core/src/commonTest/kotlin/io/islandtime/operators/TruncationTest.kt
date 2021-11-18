@file:Suppress("PackageDirectoryMismatch")

package io.islandtime

import io.islandtime.measures.TimeUnit.*
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TruncationTest : AbstractIslandTimeTest() {
    @Test
    fun `truncatedTo_DAYS returns midnight`() {
        assertEquals(
            Time.MIDNIGHT,
            Time(0, 0, 0, 1).truncatedTo(DAYS)
        )
    }

    @Test
    fun `truncatedTo_HOURS removes components smaller than hours`() {
        assertEquals(
            Time(1, 0),
            Time(1, 2, 3, 4).truncatedTo(HOURS)
        )
    }

    @Test
    fun `truncatedTo_MINUTES removes components smaller than minutes`() {
        assertEquals(
            Time(1, 2),
            Time(1, 2, 3, 4).truncatedTo(MINUTES)
        )
    }

    @Test
    fun `truncatedTo_SECONDS removes components smaller than seconds`() {
        assertEquals(
            Time(1, 2, 3),
            Time(1, 2, 3, 4).truncatedTo(SECONDS)
        )
    }

    @Test
    fun `truncatedTo_MILLISECONDS removes components smaller than milliseconds`() {
        assertEquals(
            Time(1, 2, 3, 444_000_000),
            Time(1, 2, 3, 444_555_666).truncatedTo(MILLISECONDS)
        )
    }

    @Test
    fun `truncatedTo_MICROSECONDS removes components smaller than microseconds`() {
        assertEquals(
            Time(1, 2, 3, 444_555_000),
            Time(1, 2, 3, 444_555_666).truncatedTo(MICROSECONDS)
        )
    }

    @Test
    fun `truncatedTo_NANOSECONDS does nothing`() {
        assertEquals(
            Time(1, 2, 3, 444_555_666),
            Time(1, 2, 3, 444_555_666).truncatedTo(NANOSECONDS)
        )
    }
}
