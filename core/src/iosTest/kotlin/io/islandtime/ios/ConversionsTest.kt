package io.islandtime.ios

import io.islandtime.*
import io.islandtime.measures.seconds
import io.islandtime.test.AbstractIslandTimeTest
import platform.Foundation.*
import kotlin.test.*

class ConversionsTest : AbstractIslandTimeTest() {
    @Test
    fun `convert Date to NSDateComponents`() {
        validateNSDateComponents(
            "2019-04-30".toDate().toNSDateComponents(),
            hasCalendar = false,
            hasTimeZone = false,
            year = 2019,
            month = 4,
            day = 30
        )

        validateNSDateComponents(
            "2019-04-30".toDate().toNSDateComponents(includeCalendar = true),
            hasCalendar = true,
            hasTimeZone = false,
            year = 2019,
            month = 4,
            day = 30
        )
    }

    @Test
    fun `convert Time to NSDateComponents`() {
        validateNSDateComponents(
            "03:00".toTime().toNSDateComponents(),
            hasCalendar = false,
            hasTimeZone = false,
            hour = 3,
            minute = 0,
            second = 0,
            nanosecond = 0
        )

        validateNSDateComponents(
            "03:00".toTime().toNSDateComponents(includeCalendar = true),
            hasCalendar = true,
            hasTimeZone = false,
            hour = 3,
            minute = 0,
            second = 0,
            nanosecond = 0
        )
    }

    @Test
    fun `convert DateTime to NSDateComponents`() {
        validateNSDateComponents(
            "2019-04-30T03:00".toDateTime().toNSDateComponents(),
            hasCalendar = false,
            hasTimeZone = false,
            year = 2019,
            month = 4,
            day = 30,
            hour = 3,
            minute = 0,
            second = 0,
            nanosecond = 0
        )

        validateNSDateComponents(
            "2019-04-30T03:00".toDateTime().toNSDateComponents(includeCalendar = true),
            hasCalendar = true,
            hasTimeZone = false,
            year = 2019,
            month = 4,
            day = 30,
            hour = 3,
            minute = 0,
            second = 0,
            nanosecond = 0
        )
    }

    @Test
    fun `convert OffsetDateTime to NSDateComponents`() {
        val offsetDateTime = "2019-04-30T03:00-04".toOffsetDateTime()
        val components = offsetDateTime.toNSDateComponents()

        validateNSDateComponents(
            components,
            hasCalendar = false,
            hasTimeZone = true,
            year = 2019,
            month = 4,
            day = 30,
            hour = 3,
            minute = 0,
            second = 0,
            nanosecond = 0
        )

        assertEquals(-14400, components.timeZone?.secondsFromGMT)

        val componentsWithCalendar = offsetDateTime.toNSDateComponents(includeCalendar = true)

        validateNSDateComponents(
            componentsWithCalendar,
            hasCalendar = true,
            hasTimeZone = true,
            year = 2019,
            month = 4,
            day = 30,
            hour = 3,
            minute = 0,
            second = 0,
            nanosecond = 0
        )

        assertEquals(-14400, componentsWithCalendar.timeZone?.secondsFromGMT)
    }

    @Test
    fun `convert ZonedDateTime to NSDateComponents`() {
        val zonedDateTime = "2019-04-30T03:00-04[America/New_York]".toZonedDateTime()
        val components = zonedDateTime.toNSDateComponents()

        validateNSDateComponents(
            components,
            hasCalendar = false,
            hasTimeZone = true,
            year = 2019,
            month = 4,
            day = 30,
            hour = 3,
            minute = 0,
            second = 0,
            nanosecond = 0
        )

        assertEquals("America/New_York", components.timeZone?.name)

        val componentsWithCalendar = zonedDateTime.toNSDateComponents(includeCalendar = true)

        validateNSDateComponents(
            componentsWithCalendar,
            hasCalendar = true,
            hasTimeZone = true,
            year = 2019,
            month = 4,
            day = 30,
            hour = 3,
            minute = 0,
            second = 0,
            nanosecond = 0
        )

        assertEquals("America/New_York", componentsWithCalendar.timeZone?.name)
    }

    @Test
    fun `converting empty NSDateComponents to OffsetDateTime causes an exception`() {
        assertFailsWith<DateTimeException> { NSDateComponents().toIslandOffsetDateTime() }
    }

    @Test
    fun `converting empty NSDateComponents to ZonedDateTime causes an exception`() {
        assertFailsWith<DateTimeException> { NSDateComponents().toIslandZonedDateTime() }
    }

    @Test
    fun `convert Instant to NSDate`() {
        assertEquals(0.0, Instant.UNIX_EPOCH.toNSDate().timeIntervalSince1970)
        assertEquals(NSDate.dateWithTimeIntervalSince1970(1572546943.0), Instant(1572546943.seconds).toNSDate())
    }

    @Test
    fun `convert NSDate to Instant`() {
        assertEquals(Instant.UNIX_EPOCH, NSDate.dateWithTimeIntervalSince1970(0.0).toIslandInstant())

        assertEquals(
            Instant(1572546943.seconds),
            NSDate.dateWithTimeIntervalSince1970(1572546943.0).toIslandInstant()
        )
    }

    @Test
    fun `convert NSDate to DateTime`() {
        assertEquals(
            Instant.UNIX_EPOCH.toDateTimeAt(UtcOffset.MAX),
            NSDate.dateWithTimeIntervalSince1970(0.0).toIslandDateTimeAt(UtcOffset.MAX)
        )

        assertEquals(
            Instant(1572546943.seconds).toDateTimeAt(UtcOffset((-14400).seconds)),
            NSDate.dateWithTimeIntervalSince1970(1572546943.0)
                .toIslandDateTimeAt(NSTimeZone.timeZoneForSecondsFromGMT(-14400))
        )
    }

    @Test
    fun `convert NSDate to OffsetDateTime`() {
        assertEquals(
            Instant.UNIX_EPOCH at UtcOffset.MAX,
            NSDate.dateWithTimeIntervalSince1970(0.0).toIslandOffsetDateTimeAt(UtcOffset.MAX)
        )

        assertEquals(
            Instant(1572546943.seconds) at UtcOffset((-14400).seconds),
            NSDate.dateWithTimeIntervalSince1970(1572546943.0)
                .toIslandOffsetDateTimeAt(NSTimeZone.timeZoneForSecondsFromGMT(-14400))
        )
    }

    @Test
    fun `convert NSDate to ZonedDateTime`() {
        val zone = "America/New_York".toTimeZone()

        assertEquals(
            Instant.UNIX_EPOCH at zone,
            NSDate.dateWithTimeIntervalSince1970(0.0).toIslandZonedDateTimeAt(zone)
        )

        assertEquals(
            Instant(1572546943.seconds) at zone,
            NSDate.dateWithTimeIntervalSince1970(1572546943.0)
                .toIslandZonedDateTimeAt(NSTimeZone.timeZoneWithName("America/New_York")!!)
        )
    }

    private fun validateNSDateComponents(
        dateComponents: NSDateComponents,
        hasCalendar: Boolean,
        hasTimeZone: Boolean,
        year: Int? = null,
        month: Int? = null,
        day: Int? = null,
        hour: Int? = null,
        minute: Int? = null,
        second: Int? = null,
        nanosecond: Int? = null
    ) {
        if (hasCalendar) assertNotNull(dateComponents.calendar()) else assertNull(dateComponents.calendar())
        if (hasTimeZone) assertNotNull(dateComponents.timeZone()) else assertNull(dateComponents.timeZone())
        assertEquals(year?.toLong() ?: NSDateComponentUndefined, dateComponents.year())
        assertEquals(month?.toLong() ?: NSDateComponentUndefined, dateComponents.month())
        assertEquals(day?.toLong() ?: NSDateComponentUndefined, dateComponents.day())
        assertEquals(hour?.toLong() ?: NSDateComponentUndefined, dateComponents.hour())
        assertEquals(minute?.toLong() ?: NSDateComponentUndefined, dateComponents.minute())
        assertEquals(second?.toLong() ?: NSDateComponentUndefined, dateComponents.second())
        assertEquals(nanosecond?.toLong() ?: NSDateComponentUndefined, dateComponents.nanosecond())
    }
}