package io.islandtime.operators

import io.islandtime.Time
import io.islandtime.measures.days
import io.islandtime.measures.hours
import io.islandtime.measures.minutes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RoundedUpToNearestTest {
    @Test
    fun `throws an exception when an hour increment is outside of 1-12`() {
        listOf(
            (-1).hours,
            0.hours,
            25.hours
        ).forEach {
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                Time.MIDNIGHT.roundedUpToNearest(it)
            }
        }
    }

    @Test
    fun `throws an exception when an hour increment can't be divided out evenly`() {
        listOf(5.hours, 7.hours).forEach {
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                Time.MIDNIGHT.roundedUpToNearest(it)
            }
        }
    }

    @Test
    fun `round up time by 2-hour increment`() {
        listOf(
            Time.MIDNIGHT to Time.MIDNIGHT,
            Time(1, 0) to Time(2, 0),
            Time.NOON to Time.NOON,
            Time(13, 0) to Time(14, 0),
            Time(23, 0) to Time.MIDNIGHT,
            Time(0, 0, 0, 1) to Time(2, 0),
            Time(11, 59, 59, 999_999_999) to Time.NOON,
            Time(12, 0, 0, 1) to Time(14, 0),
            Time(23, 59, 59, 999_999_999) to Time.MIDNIGHT
        ).forEach { (time, expectedResult) ->
            assertEquals(expectedResult, time.roundedUpToNearest(2.hours))
        }
    }

    @Test
    fun `round up time by 12-hour increment`() {
        listOf(
            Time.MIDNIGHT to Time.MIDNIGHT,
            Time(1, 0) to Time.NOON,
            Time.NOON to Time.NOON,
            Time(13, 0) to Time.MIDNIGHT,
            Time(23, 0) to Time.MIDNIGHT,
            Time(0, 0, 0, 1) to Time.NOON,
            Time(11, 59, 59, 999_999_999) to Time.NOON,
            Time(12, 0, 0, 1) to Time.MIDNIGHT,
            Time(23, 59, 59, 999_999_999) to Time.MIDNIGHT
        ).forEach { (time, expectedResult) ->
            assertEquals(expectedResult, time.roundedUpToNearest(12.hours))
        }
    }

    @Test
    fun `throws an exception when a minute increment is out of range`() {
        listOf(
            (-1).minutes,
            0.minutes,
            1.days + 1.minutes,
            2.days.inMinutes
        ).forEach {
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                Time.MIDNIGHT.roundedUpToNearest(it)
            }
        }
    }

    @Test
    fun `throws an exception when a minute increment can't be divided out evenly`() {
        listOf(7.minutes, 11.minutes).forEach {
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                Time.MIDNIGHT.roundedUpToNearest(it)
            }
        }
    }

    @Test
    fun `round up time by 1-minute increment`() {
        listOf(
            Time.MIDNIGHT to Time.MIDNIGHT,
            Time.NOON to Time.NOON,
            Time(1, 1, 1, 1) to Time(1, 2),
            Time(13, 59, 0, 1) to Time(14, 0),
            Time(0, 0, 0, 1) to Time(0, 1),
            Time(23, 59, 59, 999_999_999) to Time.MIDNIGHT
        ).forEach { (time, expectedResult) ->
            assertEquals(expectedResult, time.roundedUpToNearest(1.minutes))
        }
    }

    @Test
    fun `round up time by 2-minute increment`() {
        listOf(
            Time.MIDNIGHT to Time.MIDNIGHT,
            Time.NOON to Time.NOON,
            Time(1, 1) to Time(1, 2),
            Time(13, 59, 0, 1) to Time(14, 0),
            Time(0, 0, 0, 1) to Time(0, 2),
            Time(23, 58, 0, 1) to Time.MIDNIGHT
        ).forEach { (time, expectedResult) ->
            assertEquals(expectedResult, time.roundedUpToNearest(2.minutes))
        }
    }

    @Test
    fun `round up time by 30-minute increment`() {
        listOf(
            Time.MIDNIGHT to Time.MIDNIGHT,
            Time.NOON to Time.NOON,
            Time(1, 1) to Time(1, 30),
            Time(13, 0, 0, 1) to Time(13, 30),
            Time(23, 30) to Time(23, 30),
            Time(23, 31) to Time.MIDNIGHT,
            Time(0, 0, 0, 1) to Time(0, 30)
        ).forEach { (time, expectedResult) ->
            assertEquals(expectedResult, time.roundedUpToNearest(30.minutes))
        }
    }
}