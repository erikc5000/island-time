package io.islandtime.operators

import io.islandtime.*
import io.islandtime.calendar.WeekSettings
import io.islandtime.locale.localeOf
import io.islandtime.measures.hours
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("RemoveRedundantBackticks")
class WeekTest {
    @Suppress("PrivatePropertyName")
    private val en_US = localeOf("en-US")

    private val nyZone = TimeZone("America/New_York")

    @Test
    fun `Date_weekRange() with ISO start`() {
        val date = Date(2020, Month.MARCH, 6)
        val expected = Date(2020, Month.MARCH, 2)..Date(2020, Month.MARCH, 8)

        assertEquals(expected, date.weekRange)
        assertEquals(expected, date.weekRange(WeekSettings.ISO))
    }

    @Test
    fun `Date_weekRange() with Sunday start`() {
        val date = Date(2020, Month.MARCH, 6)
        val expected = Date(2020, Month.MARCH, 1)..Date(2020, Month.MARCH, 7)

        assertEquals(expected, date.weekRange(WeekSettings.SUNDAY_START))
        assertEquals(expected, date.weekRange(en_US))
    }

    @Test
    fun `DateTime_weekInterval() with ISO start`() {
        val dateTime = DateTime(2020, Month.MARCH, 6, 13, 30)
        val start = Date(2020, Month.MARCH, 2) at Time.MIDNIGHT
        val end = Date(2020, Month.MARCH, 8) at Time.MAX

        assertEquals(start..end, dateTime.weekInterval)
        assertEquals(start..end, dateTime.weekInterval(WeekSettings.ISO))
    }

    @Test
    fun `DateTime_weekInterval() with Sunday start`() {
        val dateTime = DateTime(2020, Month.MARCH, 6, 13, 30)
        val start = Date(2020, Month.MARCH, 1) at Time.MIDNIGHT
        val end = Date(2020, Month.MARCH, 7) at Time.MAX

        assertEquals(start..end, dateTime.weekInterval(WeekSettings.SUNDAY_START))
        assertEquals(start..end, dateTime.weekInterval(en_US))
    }

    @Test
    fun `OffsetDateTime_weekInterval() with ISO start`() {
        // Note: DST transition occurs at 2AM on March 8
        val offsetDateTime = DateTime(2020, Month.MARCH, 6, 13, 30) at UtcOffset((-5).hours)
        val start = Date(2020, Month.MARCH, 2) at Time.MIDNIGHT at UtcOffset((-5).hours)
        val end = Date(2020, Month.MARCH, 8) at Time.MAX at UtcOffset((-5).hours)

        assertEquals(start..end, offsetDateTime.weekInterval)
        assertEquals(start..end, offsetDateTime.weekInterval(WeekSettings.ISO))
    }

    @Test
    fun `OffsetDateTime_weekInterval() with Sunday start`() {
        val offsetDateTime = DateTime(2020, Month.MARCH, 6, 13, 30) at UtcOffset((-5).hours)
        val start = Date(2020, Month.MARCH, 1) at Time.MIDNIGHT at UtcOffset((-5).hours)
        val end = Date(2020, Month.MARCH, 7) at Time.MAX at UtcOffset((-5).hours)

        assertEquals(start..end, offsetDateTime.weekInterval(en_US))
        assertEquals(start..end, offsetDateTime.weekInterval(WeekSettings.SUNDAY_START))
    }

    @Test
    fun `ZonedDateTime_weekInterval() with ISO start`() {
        // Note: DST transition occurs at 2AM on March 8
        val zonedDateTime = DateTime(2020, Month.MARCH, 6, 13, 30) at nyZone
        val start = Date(2020, Month.MARCH, 2) at Time.MIDNIGHT at nyZone
        val end = Date(2020, Month.MARCH, 8) at Time.MAX at nyZone

        assertEquals(start..end, zonedDateTime.weekInterval)
        assertEquals(start..end, zonedDateTime.weekInterval(WeekSettings.ISO))
    }

    @Test
    fun `ZonedDateTime_weekInterval() with Sunday start`() {
        val zonedDateTime = DateTime(2020, Month.MARCH, 6, 13, 30) at nyZone
        val start = Date(2020, Month.MARCH, 1) at Time.MIDNIGHT at nyZone
        val end = Date(2020, Month.MARCH, 7) at Time.MAX at nyZone

        assertEquals(start..end, zonedDateTime.weekInterval(en_US))
        assertEquals(start..end, zonedDateTime.weekInterval(WeekSettings.SUNDAY_START))
    }
}