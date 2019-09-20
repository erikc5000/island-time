package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.date.Date
import dev.erikchristensen.islandtime.interval.milliseconds
import dev.erikchristensen.islandtime.zone.PlatformDefault
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

// These tests all require a time zone provider to execute, so keeping them together at least for now
class NowTest {
    @BeforeTest
    fun setUp() {
        IslandTime.initialize(PlatformDefault)
    }

    @AfterTest
    fun tearDown() {
        IslandTime.tearDown()
    }

    @Test
    fun `YearMonth_now()`() {
        val clock = FixedClock((-1L).milliseconds)
        assertEquals(YearMonth(1969, Month.DECEMBER), YearMonth.now(clock))

        clock += 1.milliseconds
        assertEquals(YearMonth(1970, Month.JANUARY), YearMonth.now(clock))
    }

    @Test
    fun `Date_now()`() {
        val clock = FixedClock((-1L).milliseconds)
        assertEquals(Date(1969, Month.DECEMBER, 31), Date.now(clock))

        clock += 1.milliseconds
        assertEquals(Date(1970, Month.JANUARY, 1), Date.now(clock))
    }

    @Test
    fun `DateTime_now()`() {
        val clock = FixedClock((-1L).milliseconds)
        assertEquals(
            DateTime(1969, Month.DECEMBER, 31, 23, 59, 59, 999_000_000),
            DateTime.now(clock)
        )

        clock += 1.milliseconds
        assertEquals(DateTime(1970, Month.JANUARY, 1, 0, 0), DateTime.now(clock))
    }
}