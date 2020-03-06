package io.islandtime.operators

import io.islandtime.*
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
    fun `Date_weekRange`() {
        val date = Date(2020, Month.MARCH, 6)

        assertEquals(
            Date(2020, Month.MARCH, 2)..Date(2020, Month.MARCH, 8),
            date.weekRange
        )
    }

    @Test
    fun `Date_localizedWeekRange()`() {
        val date = Date(2020, Month.MARCH, 6)

        assertEquals(
            Date(2020, Month.MARCH, 1)..Date(2020, Month.MARCH, 7),
            date.localizedWeekRange(en_US)
        )
    }

    @Test
    fun `DateTime_weekInterval`() {
        val dateTime = DateTime(2020, Month.MARCH, 6, 13, 30)
        val start = Date(2020, Month.MARCH, 2) at Time.MIDNIGHT
        val end = Date(2020, Month.MARCH, 8) at Time.MAX

        assertEquals(start..end, dateTime.weekInterval)
    }

    @Test
    fun `DateTime_localizedWeekInterval()`() {
        val dateTime = DateTime(2020, Month.MARCH, 6, 13, 30)
        val start = Date(2020, Month.MARCH, 1) at Time.MIDNIGHT
        val end = Date(2020, Month.MARCH, 7) at Time.MAX

        assertEquals(start..end, dateTime.localizedWeekInterval(en_US))
    }

    @Test
    fun `OffsetDateTime_weekInterval`() {
        // Note: DST transition occurs at 2AM on March 8
        val offsetDateTime = DateTime(2020, Month.MARCH, 6, 13, 30) at UtcOffset((-5).hours)
        val start = Date(2020, Month.MARCH, 2) at Time.MIDNIGHT at UtcOffset((-5).hours)
        val end = Date(2020, Month.MARCH, 8) at Time.MAX at UtcOffset((-5).hours)

        assertEquals(start..end, offsetDateTime.weekInterval)
    }

    @Test
    fun `OffsetDateTime_localizedWeekInterval()`() {
        val offsetDateTime = DateTime(2020, Month.MARCH, 6, 13, 30) at UtcOffset((-5).hours)
        val start = Date(2020, Month.MARCH, 1) at Time.MIDNIGHT at UtcOffset((-5).hours)
        val end = Date(2020, Month.MARCH, 7) at Time.MAX at UtcOffset((-5).hours)

        assertEquals(start..end, offsetDateTime.localizedWeekInterval(en_US))
    }

    @Test
    fun `ZonedDateTime_weekInterval`() {
        // Note: DST transition occurs at 2AM on March 8
        val zonedDateTime = DateTime(2020, Month.MARCH, 6, 13, 30) at nyZone
        val start = Date(2020, Month.MARCH, 2) at Time.MIDNIGHT at nyZone
        val end = Date(2020, Month.MARCH, 8) at Time.MAX at nyZone

        assertEquals(start..end, zonedDateTime.weekInterval)
    }

    @Test
    fun `ZonedDateTime_localizedWeekInterval()`() {
        val zonedDateTime = DateTime(2020, Month.MARCH, 6, 13, 30) at nyZone
        val start = Date(2020, Month.MARCH, 1) at Time.MIDNIGHT at nyZone
        val end = Date(2020, Month.MARCH, 7) at Time.MAX at nyZone

        assertEquals(start..end, zonedDateTime.localizedWeekInterval(en_US))
    }
}