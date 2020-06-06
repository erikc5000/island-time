package io.islandtime.operators

import io.islandtime.Instant
import io.islandtime.Time
import io.islandtime.measures.days
import io.islandtime.measures.hours
import io.islandtime.measures.minutes
import io.islandtime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RoundedToNearestTest {
    @Test
    fun `throws an exception when an hour increment is outside of 1-24`() {
        listOf(
            (-1).hours,
            0.hours,
            25.hours,
            48.hours
        ).forEach {
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                Instant.UNIX_EPOCH.roundedToNearest(it)
            }
        }
    }

    @Test
    fun `throws an exception when an hour increment can't be divided out evenly`() {
        listOf(5.hours, 7.hours).forEach {
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                Instant.UNIX_EPOCH.roundedToNearest(it)
            }
        }
    }

    @Test
    fun `round instant by 2-hour increment`() {
        listOf(
            Instant.UNIX_EPOCH to Instant.UNIX_EPOCH,
            "1969-12-31T01:00Z".toInstant() to "1969-12-31T02:00Z".toInstant(),
            "1969-12-31T00:59:59.999999999Z".toInstant() to "1969-12-31T00:00Z".toInstant(),
            "1969-12-31T00:00:00.000000001Z".toInstant() to "1969-12-31T00:00Z".toInstant(),
            "1969-12-30T23:59:59.999999999Z".toInstant() to "1969-12-31T00:00Z".toInstant(),
            "1969-12-31T23:00Z".toInstant() to "1970-01-01T00:00Z".toInstant()
        ).forEach { (instant, expectedResult) ->
            assertEquals(expectedResult, instant.roundedToNearest(2.hours))
        }
    }

//    @Test
//    fun `round up time by 12-hour increment`() {
//        listOf(
//            Time.MIDNIGHT to Time.MIDNIGHT,
//            Time(1, 0) to Time.NOON,
//            Time.NOON to Time.NOON,
//            Time(13, 0) to Time.MIDNIGHT,
//            Time(23, 0) to Time.MIDNIGHT,
//            Time(0, 0, 0, 1) to Time.NOON,
//            Time(11, 59, 59, 999_999_999) to Time.NOON,
//            Time(12, 0, 0, 1) to Time.MIDNIGHT,
//            Time(23, 59, 59, 999_999_999) to Time.MIDNIGHT
//        ).forEach { (time, expectedResult) ->
//            assertEquals(expectedResult, time.roundedToNearest(12.hours))
//        }
//    }

    @Test
    fun `throws an exception when a minute increment is out of range`() {
        listOf(
            (-1).minutes,
            0.minutes,
            1.days + 1.minutes,
            2.days.inMinutes
        ).forEach {
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                Instant.UNIX_EPOCH.roundedToNearest(it)
            }
        }
    }

    @Test
    fun `throws an exception when a minute increment can't be divided out evenly`() {
        listOf(7.minutes, 11.minutes).forEach {
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                Instant.UNIX_EPOCH.roundedToNearest(it)
            }
        }
    }
}