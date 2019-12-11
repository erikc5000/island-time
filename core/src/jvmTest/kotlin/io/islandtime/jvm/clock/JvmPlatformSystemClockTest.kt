package io.islandtime.jvm.clock

import io.islandtime.DateTimeException
import io.islandtime.TimeZone
import io.islandtime.clock.toIslandTimeZone
import io.islandtime.test.AbstractIslandTimeTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class JvmPlatformSystemClockTest : AbstractIslandTimeTest() {
    @Test
    fun `converts java_util_TimeZone with UTC equivalent IDs to Island region-based zones`() {
        listOf("GMT", "UTC").forEach {
            val tz = java.util.TimeZone.getTimeZone(it).toIslandTimeZone()
            assertEquals(it, tz.id)
            assertTrue { tz.isValid }
        }
    }

    @Test
    fun `converts java_util_TimeZone with fixed offset IDs to Island fixed offset zones`() {
        val tz1 = java.util.TimeZone.getTimeZone("GMT+18:00").toIslandTimeZone()
        assertTrue { tz1 is TimeZone.FixedOffset }
        assertEquals("+18:00", tz1.id)

        val tz2 = java.util.TimeZone.getTimeZone("GMT-18:00").toIslandTimeZone()
        assertTrue { tz2 is TimeZone.FixedOffset }
        assertEquals("-18:00", tz2.id)
    }

    @Test
    fun `throws exception if converting java_util_TimeZone with fixed offset produces an invalid Island offset`() {
        listOf("GMT+18:01", "GMT-18:01").forEach {
            assertFailsWith<DateTimeException> { java.util.TimeZone.getTimeZone(it).toIslandTimeZone() }
        }
    }
}
