package io.islandtime

import io.islandtime.measures.*
import io.islandtime.parser.DateTimeParseException
import io.islandtime.parser.DateTimeParsers
import io.islandtime.zone.*
import kotlin.test.*

class ZonedDateTimeTest : AbstractIslandTimeTest() {

    private val nyZone = "America/New_York".toTimeZone()
    private val denverZone = "America/Denver".toTimeZone()

//    object TestTimeZoneRulesProvider : TimeZoneRulesProvider {
//        override fun getRules(regionId: String): TimeZoneRules {
//            return when (regionId) {
//                "Etc/UTC" -> UtcRules
//                "America/New_York" -> UtcRules //NewYorkRules
//                else -> throw TimeZoneRulesException("Region ID not found")
//            }
//        }
//
//        override fun getAvailableRegionIds(): Set<String> {
//            return setOf(
//                "Etc/UTC",
//                "America/New_York"
//            )
//        }

//        object UtcRules : TimeZoneRules {
//            override fun isValidOffset(dateTime: DateTime, offset: UtcOffset): Boolean {
//                return offset == UtcOffset.ZERO
//            }
//
//            override fun offsetAt(instant: Instant) = UtcOffset.ZERO
//            override fun offsetAt(dateTime: DateTime) = UtcOffset.ZERO
//            override fun transitionAt(dateTime: DateTime): TimeZoneOffsetTransition? = null
//            override fun validOffsetsAt(dateTime: DateTime) = listOf(UtcOffset.ZERO)
//        }

//        object NewYorkRules : TimeZoneRules {
//            override fun isValidOffset(dateTime: DateTime, offset: UtcOffset): Boolean {
//                return offset == UtcOffset.ZERO
//            }
//
//            override fun offsetAt(instant: Instant): UtcOffset {
//
//            }
//
//            override fun offsetAt(dateTime: DateTime): UtcOffset {
//                if (dateTime.month >= Month.MARCH && dateTime.dayOfWeek == DayOfWeek.SUNDAY)
//            }
//
//            override fun transitionAt(dateTime: DateTime): TimeZoneOffsetTransition? {
//                if (dateTime.month >= Month.MARCH && dateTime.dayOfWeek == DayOfWeek.SUNDAY)
//            }
//
//            override fun validOffsetsAt(dateTime: DateTime): List<UtcOffset> {
//                if (dateTime.month >= Month.MARCH && dateTime.dayOfWeek == DayOfWeek.SUNDAY)
//            }
//        }
//    }

    @Test
    fun `throws an exception when constructed with a TimeZone that has no rules`() {
        assertFailsWith<TimeZoneRulesException> {
            ZonedDateTime(
                DateTime(2019, 5, 30, 18, 0),
                "America/Boston".toTimeZone()
            )
        }
    }

    @Test
    fun `when constructed from a DateTime that falls in an overlap, the earlier offset is used by default`() {
        val actual = ZonedDateTime(
            DateTime(2019, 11, 3, 1, 0),
            nyZone
        )

        assertEquals(DateTime(2019, 11, 3, 1, 0), actual.dateTime)
        assertEquals(nyZone, actual.zone)
        assertEquals(UtcOffset((-4).hours), actual.offset)
    }

    @Test
    fun `when constructed from a DateTime that falls in an overlap, a preferred offset may be provided`() {
        val actual = ZonedDateTime.fromLocal(
            DateTime(2019, 11, 3, 1, 0),
            nyZone,
            UtcOffset((-5).hours)
        )

        assertEquals(DateTime(2019, 11, 3, 1, 0), actual.dateTime)
        assertEquals(nyZone, actual.zone)
        assertEquals(UtcOffset((-5).hours), actual.offset)
    }

    @Test
    fun `when constructed from a DateTime that falls in an overlap, an invalid preferred offset is ignored`() {
        val actual = ZonedDateTime.fromLocal(
            DateTime(2019, 11, 3, 1, 0),
            nyZone,
            UtcOffset((-8).hours)
        )

        assertEquals(DateTime(2019, 11, 3, 1, 0), actual.dateTime)
        assertEquals(nyZone, actual.zone)
        assertEquals(UtcOffset((-4).hours), actual.offset)
    }

    @Test
    fun `when constructed from a DateTime that doesn't fall in an overlap, the preferred offset is ignored`() {
        val actual = ZonedDateTime.fromLocal(
            DateTime(2019, 11, 3, 2, 0),
            nyZone,
            UtcOffset((-4).hours)
        )

        assertEquals(DateTime(2019, 11, 3, 2, 0), actual.dateTime)
        assertEquals(nyZone, actual.zone)
        assertEquals(UtcOffset((-5).hours), actual.offset)
    }

    @Test
    fun `when constructed from a DateTime that falls during a gap, the DateTime is adjusted by the gap's length`() {
        val actual = ZonedDateTime(
            DateTime(2019, 3, 10, 2, 30),
            nyZone
        )

        assertEquals(DateTime(2019, 3, 10, 3, 30), actual.dateTime)
        assertEquals(nyZone, actual.zone)
        assertEquals(UtcOffset((-4).hours), actual.offset)
    }

    @Test
    fun `at infix creates a ZonedDateTime from a DateTime`() {
        assertEquals(
            ZonedDateTime(
                2019,
                3,
                3,
                1,
                0,
                0,
                0,
                TimeZone.UTC
            ),
            DateTime(2019, 3, 3, 1, 0) at TimeZone.UTC
        )

        assertEquals(
            ZonedDateTime(
                2019,
                3,
                3,
                1,
                0,
                0,
                0,
                nyZone
            ),
            DateTime(2019, 3, 3, 1, 0) at
                    nyZone
        )
    }

    @Test
    fun `at infix creates a ZonedDateTime from an instant`() {
        assertEquals(
            ZonedDateTime(
                2019,
                8,
                19,
                23,
                7,
                27,
                821_000_000,
                TimeZone.UTC
            ),
            Instant.fromMillisecondsSinceUnixEpoch(1566256047821L.milliseconds) at TimeZone.UTC
        )

        assertEquals(
            ZonedDateTime(
                2019,
                8,
                19,
                19,
                7,
                27,
                821_000_000,
                nyZone
            ),
            Instant.fromMillisecondsSinceUnixEpoch(1566256047821L.milliseconds)
                    at nyZone
        )
    }

    @Test
    fun `equality is based on date-time, time zone, and offset`() {
        assertEquals(
            ZonedDateTime.create(
                DateTime(2019, 11, 3, 1, 0),
                UtcOffset((-4).hours),
                nyZone
            ),
            ZonedDateTime.create(
                DateTime(2019, 11, 3, 1, 0),
                UtcOffset((-4).hours),
                nyZone
            )
        )

        assertNotEquals(
            ZonedDateTime.fromLocal(
                DateTime(2019, 11, 3, 1, 0),
                nyZone,
                UtcOffset((-4).hours)
            ),
            ZonedDateTime.fromLocal(
                DateTime(2019, 11, 3, 1, 0),
                nyZone,
                UtcOffset((-5).hours)
            )
        )

        assertNotEquals(
            ZonedDateTime(
                DateTime(2019, 11, 3, 5, 0),
                denverZone
            ),
            ZonedDateTime(
                DateTime(2019, 11, 3, 7, 0),
                nyZone
            )
        )
    }

    @Test
    fun `DEFAULT_SORT_ORDER compares based on instant, then date and time, and then zone`() {
        assertTrue {
            ZonedDateTime.DEFAULT_SORT_ORDER.compare(
                Date(1969, 365) at Time(23, 0) at "America/Chicago".toTimeZone(),
                Date(1970, 1) at Time(0, 0) at nyZone
            ) < 0
        }

        assertTrue {
            ZonedDateTime.DEFAULT_SORT_ORDER.compare(
                Date(1970, 1) at Time(0, 0) at "Etc/GMT+5".toTimeZone(),
                Date(1970, 1) at Time(0, 0) at nyZone
            ) > 0
        }

        assertTrue {
            ZonedDateTime.DEFAULT_SORT_ORDER.compare(
                Date(1969, 365) at Time(23, 0) at "Etc/GMT+5".toTimeZone(),
                Date(1969, 365) at Time(23, 0) at "Etc/GMT+5".toTimeZone()
            ) == 0
        }
    }

    @Test
    fun `TIMELINE_ORDER compare based on instant only`() {
        assertTrue {
            OffsetDateTime.TIMELINE_ORDER.compare(
                Date(1969, 365) at Time(23, 0) at UtcOffset((-1).hours),
                Date(1970, 1) at Time(0, 0) at UtcOffset.ZERO
            ) == 0
        }
    }

    @Test
    fun `compareTo() compares based on instant only`() {
        assertTrue {
            Date(1969, 365) at Time(22, 0) at UtcOffset((-1).hours) <
                    Date(1970, 1) at Time(0, 0) at UtcOffset.ZERO
        }
    }

    @Test
    fun `Date_startOfDayAt() creates a ZonedDateTime at the start of the day in a particular time zone`() {
        assertEquals(
            ZonedDateTime(
                DateTime(2019, 5, 20, 0, 0),
                nyZone
            ),
            Date(2019, 5, 20).startOfDayAt(nyZone)
        )

        // TODO: Add tests where transitions occur during midnight
    }

    @Test
    fun `Date_endOfDayAt() creates a ZonedDateTime at the end of the day in a particular time zone`() {
        assertEquals(
            ZonedDateTime(
                DateTime(2019, 5, 20, 23, 59, 59, 999_999_999),
                nyZone
            ),
            Date(2019, 5, 20).endOfDayAt(nyZone)
        )

        // TODO: Add tests where transitions occur during midnight
    }

    @Test
    fun `copy() ignores changes to the offset if it isn't valid for the time zone`() {
        assertEquals(
            ZonedDateTime.create(
                DateTime(2019, 3, 3, 7, 0),
                UtcOffset((-7).hours),
                denverZone
            ),
            ZonedDateTime(
                DateTime(2019, 11, 3, 7, 0),
                nyZone
            ).copy(
                monthNumber = 3,
                offset = (-4).hours.asUtcOffset(),
                zone = denverZone
            )
        )
    }

    @Test
    fun `copy() adjusts components forward when rendered invalid due to gaps`() {
        assertEquals(
            ZonedDateTime.create(
                DateTime(2019, 3, 10, 3, 3),
                UtcOffset((-4).hours),
                nyZone
            ),
            ZonedDateTime(
                DateTime(2019, 3, 10, 7, 0),
                nyZone
            ).copy(hour = 2, minute = 3)
        )
    }

    @Test
    fun `copy() replaces components directly with new values when it's possible to do so`() {
        assertEquals(
            ZonedDateTime.create(
                DateTime(2018, 3, 10, 3, 0),
                UtcOffset((-5).hours),
                nyZone
            ),
            ZonedDateTime(
                DateTime(2019, 3, 10, 7, 5),
                nyZone
            ).copy(hour = 3, minute = 0, year = 2018)
        )
    }

    @Test
    fun `withEarlierOffsetAtOverlap() returns the same DateTime with the earlier offset when there's a DST overlap`() {
        assertEquals(
            ZonedDateTime.create(
                DateTime(2019, 11, 3, 1, 30),
                UtcOffset((-4).hours),
                nyZone
            ),
            ZonedDateTime.fromLocal(
                DateTime(2019, 11, 3, 1, 30),
                nyZone,
                UtcOffset((-5).hours)
            ).withEarlierOffsetAtOverlap()
        )
    }

    @Test
    fun `withEarlierOffsetAtOverlap() returns the same ZonedDateTime when there's no overlap`() {
        assertEquals(
            ZonedDateTime(
                DateTime(2019, 11, 3, 2, 30),
                nyZone
            ),
            ZonedDateTime(
                DateTime(2019, 11, 3, 2, 30),
                nyZone
            ).withEarlierOffsetAtOverlap()
        )
    }

    @Test
    fun `withLaterOffsetAtOverlap() returns the same DateTime with the later offset when there's a DST overlap`() {
        assertEquals(
            ZonedDateTime.create(
                DateTime(2019, 11, 3, 1, 30),
                UtcOffset((-5).hours),
                nyZone
            ),
            ZonedDateTime.create(
                DateTime(2019, 11, 3, 1, 30),
                UtcOffset((-4).hours),
                nyZone
            ).withLaterOffsetAtOverlap()
        )
    }

    @Test
    fun `withLaterOffsetAtOverlap() returns the same ZonedDateTime when there's no overlap`() {
        assertEquals(
            ZonedDateTime(
                DateTime(2019, 11, 3, 2, 30),
                nyZone
            ),
            ZonedDateTime(
                DateTime(2019, 11, 3, 2, 30),
                nyZone
            ).withLaterOffsetAtOverlap()
        )
    }

    @Test
    fun `withFixedOffsetZone() returns the same ZonedDateTime if it already has a fixed zone`() {
        assertEquals(
            ZonedDateTime(
                DateTime(2019, 11, 4, 8, 30),
                UtcOffset((-5).hours).toTimeZone()
            ),
            ZonedDateTime(
                DateTime(2019, 11, 4, 8, 30),
                UtcOffset((-5).hours).toTimeZone()
            ).withFixedOffsetZone()
        )
    }

    @Test
    fun `withFixedOffsetZone() returns a ZonedDateTime with a fixed offset zone when region-based`() {
        assertEquals(
            ZonedDateTime(
                DateTime(2019, 11, 4, 8, 30),
                UtcOffset((-5).hours).toTimeZone()
            ),
            ZonedDateTime(
                DateTime(2019, 11, 4, 8, 30),
                nyZone
            ).withFixedOffsetZone()
        )
    }

    @Test
    fun `adjustedTo() converts to a different time zone while preserving the instant during overlap`() {
        // New York in overlap, Denver not in overlap
        assertEquals(
            ZonedDateTime.create(
                DateTime(2019, 11, 3, 0, 30),
                UtcOffset((-6).hours),
                denverZone
            ),
            ZonedDateTime.fromLocal(
                DateTime(2019, 11, 3, 1, 30),
                nyZone,
                UtcOffset((-5).hours)
            ).adjustedTo(denverZone)
        )

        // New York no longer in overlap, Denver in earlier offset at overlap
        assertEquals(
            ZonedDateTime.create(
                DateTime(2019, 11, 3, 1, 30),
                UtcOffset((-6).hours),
                denverZone
            ),
            ZonedDateTime.fromLocal(
                DateTime(2019, 11, 3, 2, 30),
                nyZone,
                UtcOffset((-5).hours)
            ).adjustedTo(denverZone)
        )

        // New York not in overlap, Denver in later offset at overlap
        assertEquals(
            ZonedDateTime.create(
                DateTime(2019, 11, 3, 1, 30),
                UtcOffset((-7).hours),
                denverZone
            ),
            ZonedDateTime.fromLocal(
                DateTime(2019, 11, 3, 3, 30),
                nyZone,
                UtcOffset((-5).hours)
            ).adjustedTo(denverZone)
        )
    }

    @Test
    fun `adjustedTo() converts to a different time zone while preserving the instant during gaps`() {
        // New York in DST, Denver not yet
        assertEquals(
            ZonedDateTime.create(
                DateTime(2019, 3, 10, 1, 30),
                UtcOffset((-7).hours),
                denverZone
            ),
            ZonedDateTime(
                DateTime(2019, 3, 10, 4, 30),
                nyZone
            ).adjustedTo(denverZone)
        )

        // New York and Denver both in DST
        assertEquals(
            ZonedDateTime.create(
                DateTime(2019, 3, 10, 3, 30),
                UtcOffset((-6).hours),
                denverZone
            ),
            ZonedDateTime(
                DateTime(2019, 3, 10, 5, 30),
                nyZone
            ).adjustedTo(denverZone)
        )
    }

    @Test
    fun `add period of zero`() {
        val zonedDateTime = DateTime(2016, Month.FEBRUARY, 29, 13, 0) at nyZone
        assertEquals(zonedDateTime, zonedDateTime + Period.ZERO)
    }

    @Test
    fun `adding a period first adds years, then months, then days`() {
        assertEquals(
            DateTime(2017, Month.MARCH, 29, 9, 0) at nyZone,
            (DateTime(2016, Month.FEBRUARY, 29, 9, 0) at nyZone) +
                    periodOf(1.years, 1.months, 1.days)
        )

        assertEquals(
            DateTime(2015, Month.JANUARY, 27, 9, 0) at nyZone,
            (DateTime(2016, Month.FEBRUARY, 29, 9, 0) at nyZone) +
                    periodOf((-1).years, (-1).months, (-1).days)
        )
    }

    @Test
    fun `subtract period of zero`() {
        val zonedDateTime = DateTime(2016, Month.FEBRUARY, 29, 13, 0) at nyZone
        assertEquals(zonedDateTime, zonedDateTime - Period.ZERO)
    }

    @Test
    fun `subtracting a period first subtracts years, then months, then days`() {
        assertEquals(
            DateTime(2017, Month.MARCH, 29, 9, 0) at nyZone,
            (DateTime(2016, Month.FEBRUARY, 29, 9, 0) at nyZone) -
                    periodOf((-1).years, (-1).months, (-1).days)
        )

        assertEquals(
            DateTime(2015, Month.JANUARY, 27, 9, 0) at nyZone,
            (DateTime(2016, Month.FEBRUARY, 29, 9, 0) at nyZone) -
                    periodOf(1.years, 1.months, 1.days)
        )
    }

    @Test
    fun `toString() returns an ISO-8601 extended offset date-time along with a non-standard region ID`() {
        assertEquals(
            "2019-11-03T01:30Z[Etc/UTC]",
            ZonedDateTime(
                DateTime(2019, 11, 3, 1, 30),
                "Etc/UTC".toTimeZone()
            ).toString()
        )

        assertEquals(
            "2019-11-03T01:30-05:00[America/New_York]",
            ZonedDateTime.create(
                DateTime(2019, 11, 3, 1, 30),
                UtcOffset((-5).hours),
                nyZone
            ).toString()
        )
    }

    @Test
    fun `String_toZonedDateTime() throws an exception when the string is empty`() {
        assertFailsWith<DateTimeParseException> { "".toZonedDateTime() }
    }

    @Test
    fun `String_toZonedDateTime() throws an exception when the format is unexpected`() {
        listOf(
            "2019-12-05T12:00+01:00[America/New_York ]",
            "2019-12-05T12:00+01:00America/New_York",
            "2019-12-05T12:00+01:00[America/New_York",
            "2019-12-05T12:00+01:00[]",
            "2019-12-05T12:00+01:00[America/New_York/one_more/characters/than_supported]"
        ).forEach {
            assertFailsWith<DateTimeParseException> { it.toZonedDateTime() }
        }
    }

    @Test
    fun `String_toZonedDateTime() throws an exception when fields are out of range`() {
        listOf(
            "2000-01-01T24:00Z[Etc/Utc]",
            "2000-01-01T08:60-01:00[GMT+1]",
            "2000-13-01T08:59-01:00[GMT+1]",
            "2000-01-32T08:59-01:00[GMT+1]"
        ).forEach {
            assertFailsWith<DateTimeException> { it.toZonedDateTime() }
        }
    }

    @Test
    fun `String_toZonedDateTime() throws an exception if the parsed zone isn't valid`() {
        listOf(
            "2000-01-01T23:00+01:00[America/Boston]",
            "2000-01-01T23:00+01:00[Etc/GMT-20]"
        ).forEach {
            assertFailsWith<DateTimeException> { it.toZonedDateTime() }
        }
    }

    @Test
    fun `String_toZonedDateTime() parses ISO-8601 calendar date time strings in extended format by default`() {
        assertEquals(
            ZonedDateTime.create(
                DateTime(Date(2019, Month.MAY, 5), Time.NOON),
                UtcOffset((-4).hours),
                nyZone
            ),
            "2019-05-05T12:00-04:00[America/New_York]".toZonedDateTime()
        )

        assertEquals(
            ZonedDateTime.create(
                DateTime(2019, 11, 3, 1, 0),
                UtcOffset((-5).hours),
                nyZone
            ),
            "2019-11-03T01:00-05:00[America/New_York]".toZonedDateTime()
        )
    }

    @Test
    fun `String_toZonedDateTime() parses valid ISO-8601 strings with explicit parser`() {
        assertEquals(
            ZonedDateTime.create(
                DateTime(Date(2019, Month.MAY, 5), Time.NOON),
                UtcOffset((-4).hours),
                nyZone
            ),
            "20190505 1200-04[America/New_York]".toZonedDateTime(DateTimeParsers.Iso.ZONED_DATE_TIME)
        )
    }
}