package io.islandtime.darwin

import io.islandtime.*
import io.islandtime.measures.*
import io.islandtime.ranges.InstantInterval
import io.islandtime.ranges.until
import io.islandtime.test.AbstractIslandTimeTest
import kotlinx.cinterop.convert
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
        assertEquals(NSDate.dateWithTimeIntervalSince1970(1572546943.0), Instant(1572546943L.seconds).toNSDate())
    }

    @Test
    fun `convert NSDate to Instant`() {
        assertEquals(Instant.UNIX_EPOCH, NSDate.dateWithTimeIntervalSince1970(0.0).toIslandInstant())

        assertEquals(
            Instant(1572546943L.seconds),
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
            Instant(1572546943L.seconds).toDateTimeAt(UtcOffset((-14400).seconds)),
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
            Instant(1572546943L.seconds) at UtcOffset((-14400).seconds),
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
            Instant(1572546943L.seconds) at zone,
            NSDate.dateWithTimeIntervalSince1970(1572546943.0)
                .toIslandZonedDateTimeAt(NSTimeZone.timeZoneWithName("America/New_York")!!)
        )
    }

    @Test
    fun `convert Duration to NSTimeInterval`() {
        assertEquals(0.0, Duration.ZERO.toNSTimeInterval())
        assertEquals(1.0, 1.seconds.asDuration().toNSTimeInterval())
        assertEquals(-1.0, (-1).seconds.asDuration().toNSTimeInterval())
        assertEquals(0.000_000_001, 1.nanoseconds.asDuration().toNSTimeInterval())
        assertEquals(-0.000_000_001, (-1).nanoseconds.asDuration().toNSTimeInterval())
        assertEquals(Long.MAX_VALUE.toDouble() + 0.999_999_999, Duration.MAX.toNSTimeInterval())
        assertEquals(Long.MIN_VALUE.toDouble() - 0.999_999_999, Duration.MIN.toNSTimeInterval())
    }

    @Test
    fun `convert days to NSTimeInterval`() {
        listOf(
            0 to 0.0,
            1 to 86_400.0,
            -1 to -86_400.0
        ).forEach { (value, expected) ->
            assertEquals(expected, value.days.toNSTimeInterval())
            assertEquals(expected, value.toLong().days.toNSTimeInterval())
        }
    }

    @Test
    fun `convert hours to NSTimeInterval`() {
        listOf(
            0 to 0.0,
            1 to 3600.0,
            -1 to -3600.0
        ).forEach { (value, expected) ->
            assertEquals(expected, value.hours.toNSTimeInterval())
            assertEquals(expected, value.toLong().hours.toNSTimeInterval())
        }
    }

    @Test
    fun `convert minutes to NSTimeInterval`() {
        listOf(
            0 to 0.0,
            1 to 60.0,
            -1 to -60.0
        ).forEach { (value, expected) ->
            assertEquals(expected, value.minutes.toNSTimeInterval())
            assertEquals(expected, value.toLong().minutes.toNSTimeInterval())
        }
    }

    @Test
    fun `convert seconds to NSTimeInterval`() {
        listOf(
            0 to 0.0,
            1 to 1.0,
            -1 to -1.0
        ).forEach { (value, expected) ->
            assertEquals(expected, value.seconds.toNSTimeInterval())
            assertEquals(expected, value.toLong().seconds.toNSTimeInterval())
        }
    }

    @Test
    fun `convert milliseconds to NSTimeInterval`() {
        listOf(
            0 to 0.0,
            1 to 0.001,
            -1 to -0.001
        ).forEach { (value, expected) ->
            assertEquals(expected, value.milliseconds.toNSTimeInterval())
            assertEquals(expected, value.toLong().milliseconds.toNSTimeInterval())
        }
    }

    @Test
    fun `convert microseconds to NSTimeInterval`() {
        listOf(
            0 to 0.0,
            1 to 0.000001,
            -1 to -0.000001
        ).forEach { (value, expected) ->
            assertEquals(expected, value.microseconds.toNSTimeInterval())
            assertEquals(expected, value.toLong().microseconds.toNSTimeInterval())
        }
    }

    @Test
    fun `convert nanoseconds to NSTimeInterval`() {
        listOf(
            0 to 0.0,
            1 to 0.000000001,
            -1 to -0.000000001
        ).forEach { (value, expected) ->
            assertEquals(expected, value.nanoseconds.toNSTimeInterval())
            assertEquals(expected, value.toLong().nanoseconds.toNSTimeInterval())
        }
    }

    @Test
    fun `convert an InstantInterval to an NSDateInterval`() {
        assertEquals(0.0, InstantInterval.EMPTY.toNSDateInterval().duration)

        assertEquals(
            NSDateInterval(
                NSDate.dateWithTimeIntervalSince1970(0.0),
                NSDate.dateWithTimeIntervalSince1970(1.0)
            ),
            (Instant.UNIX_EPOCH until Instant(1L.seconds)).toNSDateInterval()
        )

        assertFailsWith<UnsupportedOperationException> { InstantInterval.UNBOUNDED.toNSDateInterval() }
        assertNull(InstantInterval.UNBOUNDED.toNSDateIntervalOrNull())
    }

    @Test
    fun `convert an NSDateInterval to an InstantInterval`() {
        assertEquals(
            InstantInterval.EMPTY,
            NSDateInterval(NSDate.dateWithTimeIntervalSince1970(0.0), 0.0).toIslandInstantInterval()
        )

        assertEquals(
            Instant.UNIX_EPOCH until Instant(1L.seconds),
            NSDateInterval(
                NSDate.dateWithTimeIntervalSince1970(0.0),
                NSDate.dateWithTimeIntervalSince1970(1.0)
            ).toIslandInstantInterval()
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
        assertEquals(year?.convert() ?: NSDateComponentUndefined, dateComponents.year())
        assertEquals(month?.convert() ?: NSDateComponentUndefined, dateComponents.month())
        assertEquals(day?.convert() ?: NSDateComponentUndefined, dateComponents.day())
        assertEquals(hour?.convert() ?: NSDateComponentUndefined, dateComponents.hour())
        assertEquals(minute?.convert() ?: NSDateComponentUndefined, dateComponents.minute())
        assertEquals(second?.convert() ?: NSDateComponentUndefined, dateComponents.second())
        assertEquals(nanosecond?.convert() ?: NSDateComponentUndefined, dateComponents.nanosecond())
    }
}