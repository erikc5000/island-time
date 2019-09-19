package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.date.Date
import dev.erikchristensen.islandtime.interval.hours
import dev.erikchristensen.islandtime.interval.milliseconds
import dev.erikchristensen.islandtime.parser.DateTimeParseException
import dev.erikchristensen.islandtime.parser.Iso8601
import dev.erikchristensen.islandtime.tz.*
import kotlin.test.*

class ZonedDateTimeTest {

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

    @BeforeTest
    fun setUp() {
        IslandTime.initialize(PlatformDefault)
    }

    @AfterTest
    fun tearDown() {
        IslandTime.tearDown()
    }

    @Test
    fun `throws an exception when constructed with a TimeZone that has no rules`() {
        assertFailsWith<TimeZoneRulesException> {
            ZonedDateTime(
                DateTime(2019, 5, 30, 18, 0),
                TimeZone("America/Boston")
            )
        }
    }

    @Test
    fun `when constructed from a DateTime that falls in an overlap, the earlier offset is used by default`() {
        val actual = ZonedDateTime(
            DateTime(2019, 11, 3, 1, 0),
            TimeZone("America/New_York")
        )

        assertEquals(DateTime(2019, 11, 3, 1, 0), actual.dateTime)
        assertEquals(TimeZone("America/New_York"), actual.zone)
        assertEquals(UtcOffset((-4).hours), actual.offset)
    }

    @Test
    fun `when constructed from a DateTime that falls in an overlap, a preferred offset may be provided`() {
        val actual = ZonedDateTime.ofLocal(
            DateTime(2019, 11, 3, 1, 0),
            TimeZone("America/New_York"),
            UtcOffset((-5).hours)
        )

        assertEquals(DateTime(2019, 11, 3, 1, 0), actual.dateTime)
        assertEquals(TimeZone("America/New_York"), actual.zone)
        assertEquals(UtcOffset((-5).hours), actual.offset)
    }

    @Test
    fun `when constructed from a DateTime that falls in an overlap, an invalid preferred offset is ignored`() {
        val actual = ZonedDateTime.ofLocal(
            DateTime(2019, 11, 3, 1, 0),
            TimeZone("America/New_York"),
            UtcOffset((-8).hours)
        )

        assertEquals(DateTime(2019, 11, 3, 1, 0), actual.dateTime)
        assertEquals(TimeZone("America/New_York"), actual.zone)
        assertEquals(UtcOffset((-4).hours), actual.offset)
    }

    @Test
    fun `when constructed from a DateTime that doesn't fall in an overlap, the preferred offset is ignored`() {
        val actual = ZonedDateTime.ofLocal(
            DateTime(2019, 11, 3, 2, 0),
            TimeZone("America/New_York"),
            UtcOffset((-4).hours)
        )

        assertEquals(DateTime(2019, 11, 3, 2, 0), actual.dateTime)
        assertEquals(TimeZone("America/New_York"), actual.zone)
        assertEquals(UtcOffset((-5).hours), actual.offset)
    }

    @Test
    fun `when constructed from a DateTime that falls during a gap, the DateTime is adjusted by the gap's length`() {
        val actual = ZonedDateTime(
            DateTime(2019, 3, 10, 2, 30),
            TimeZone("America/New_York")
        )

        assertEquals(DateTime(2019, 3, 10, 3, 30), actual.dateTime)
        assertEquals(TimeZone("America/New_York"), actual.zone)
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
                TimeZone("America/New_York")
            ),
            DateTime(2019, 3, 3, 1, 0) at
                TimeZone("America/New_York")
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
                TimeZone("America/New_York")
            ),
            Instant.fromMillisecondsSinceUnixEpoch(1566256047821L.milliseconds)
                at TimeZone("America/New_York")
        )
    }

    @Test
    fun `equality is based on date-time, time zone, and offset`() {
        assertEquals(
            ZonedDateTime.create(
                DateTime(2019, 11, 3, 1, 0),
                UtcOffset((-4).hours),
                TimeZone("America/New_York")
            ),
            ZonedDateTime.create(
                DateTime(2019, 11, 3, 1, 0),
                UtcOffset((-4).hours),
                TimeZone("America/New_York")
            )
        )

        assertNotEquals(
            ZonedDateTime.ofLocal(
                DateTime(2019, 11, 3, 1, 0),
                TimeZone("America/New_York"),
                UtcOffset((-4).hours)
            ),
            ZonedDateTime.ofLocal(
                DateTime(2019, 11, 3, 1, 0),
                TimeZone("America/New_York"),
                UtcOffset((-5).hours)
            )
        )

        assertNotEquals(
            ZonedDateTime(
                DateTime(2019, 11, 3, 5, 0),
                TimeZone("America/Denver")
            ),
            ZonedDateTime(
                DateTime(2019, 11, 3, 7, 0),
                TimeZone("America/New_York")
            )
        )
    }

    @Test
    fun `Date_atStartOfDayIn() creates a ZonedDateTime at the start of the day in a particular time zone`() {
        assertEquals(
            ZonedDateTime(
                DateTime(2019, 5, 20, 0, 0),
                TimeZone("America/New_York")
            ),
            Date(2019, 5, 20).atStartOfDayIn("America/New_York".toTimeZone())
        )

        // TODO: Add tests where transitions occur during midnight
    }

    @Test
    fun `Date_atEndOfDayIn() creates a ZonedDateTime at the end of the day in a particular time zone`() {
        assertEquals(
            ZonedDateTime(
                DateTime(2019, 5, 20, 23, 59, 59, 999_999_999),
                TimeZone("America/New_York")
            ),
            Date(2019, 5, 20).atEndOfDayIn("America/New_York".toTimeZone())
        )

        ZonedDateTime.now().yearMonth.startDate

        // TODO: Add tests where transitions occur during midnight
    }

    @Test
    fun `copy() ignores changes to the offset if it isn't valid for the time zone`() {
        assertEquals(
            ZonedDateTime.create(
                DateTime(2019, 3, 3, 7, 0),
                UtcOffset((-7).hours),
                TimeZone("America/Denver")
            ),
            ZonedDateTime(
                DateTime(2019, 11, 3, 7, 0),
                TimeZone("America/New_York")
            ).copy(
                monthNumber = 3,
                offset = (-4).hours.asUtcOffset(),
                zone = "America/Denver".toTimeZone()
            )
        )
    }

    @Test
    fun `copy() adjusts components forward when rendered invalid due to gaps`() {
        assertEquals(
            ZonedDateTime.create(
                DateTime(2019, 3, 10, 3, 3),
                UtcOffset((-4).hours),
                TimeZone("America/New_York")
            ),
            ZonedDateTime(
                DateTime(2019, 3, 10, 7, 0),
                TimeZone("America/New_York")
            ).copy(hour = 2, minute = 3)
        )
    }

    @Test
    fun `copy() replaces components directly with new values when it's possible to do so`() {
        assertEquals(
            ZonedDateTime.create(
                DateTime(2018, 3, 10, 3, 0),
                UtcOffset((-5).hours),
                TimeZone("America/New_York")
            ),
            ZonedDateTime(
                DateTime(2019, 3, 10, 7, 5),
                TimeZone("America/New_York")
            ).copy(hour = 3, minute = 0, year = 2018)
        )
    }

    @Test
    fun `withEarlierOffsetAtOverlap() returns the same DateTime with the earlier offset when there's a DST overlap`() {
        assertEquals(
            ZonedDateTime.create(
                DateTime(2019, 11, 3, 1, 30),
                UtcOffset((-4).hours),
                TimeZone("America/New_York")
            ),
            ZonedDateTime.ofLocal(
                DateTime(2019, 11, 3, 1, 30),
                TimeZone("America/New_York"),
                UtcOffset((-5).hours)
            ).withEarlierOffsetAtOverlap()
        )
    }

    @Test
    fun `withEarlierOffsetAtOverlap() returns the same ZonedDateTime when there's no overlap`() {
        assertEquals(
            ZonedDateTime(
                DateTime(2019, 11, 3, 2, 30),
                TimeZone("America/New_York")
            ),
            ZonedDateTime(
                DateTime(2019, 11, 3, 2, 30),
                TimeZone("America/New_York")
            ).withEarlierOffsetAtOverlap()
        )
    }

    @Test
    fun `withLaterOffsetAtOverlap() returns the same DateTime with the later offset when there's a DST overlap`() {
        assertEquals(
            ZonedDateTime.create(
                DateTime(2019, 11, 3, 1, 30),
                UtcOffset((-5).hours),
                TimeZone("America/New_York")
            ),
            ZonedDateTime.create(
                DateTime(2019, 11, 3, 1, 30),
                UtcOffset((-4).hours),
                TimeZone("America/New_York")
            ).withLaterOffsetAtOverlap()
        )
    }

    @Test
    fun `withLaterOffsetAtOverlap() returns the same ZonedDateTime when there's no overlap`() {
        assertEquals(
            ZonedDateTime(
                DateTime(2019, 11, 3, 2, 30),
                TimeZone("America/New_York")
            ),
            ZonedDateTime(
                DateTime(2019, 11, 3, 2, 30),
                TimeZone("America/New_York")
            ).withLaterOffsetAtOverlap()
        )
    }

    @Test
    fun `adjustedTo() converts to a different time zone while preserving the instant during overlap`() {
        // New York in overlap, Denver not in overlap
        assertEquals(
            ZonedDateTime.create(
                DateTime(2019, 11, 3, 0, 30),
                UtcOffset((-6).hours),
                TimeZone("America/Denver")
            ),
            ZonedDateTime.ofLocal(
                DateTime(2019, 11, 3, 1, 30),
                TimeZone("America/New_York"),
                UtcOffset((-5).hours)
            ).adjustedTo("America/Denver".toTimeZone())
        )

        // New York no longer in overlap, Denver in earlier offset at overlap
        assertEquals(
            ZonedDateTime.create(
                DateTime(2019, 11, 3, 1, 30),
                UtcOffset((-6).hours),
                TimeZone("America/Denver")
            ),
            ZonedDateTime.ofLocal(
                DateTime(2019, 11, 3, 2, 30),
                TimeZone("America/New_York"),
                UtcOffset((-5).hours)
            ).adjustedTo("America/Denver".toTimeZone())
        )

        // New York not in overlap, Denver in later offset at overlap
        assertEquals(
            ZonedDateTime.create(
                DateTime(2019, 11, 3, 1, 30),
                UtcOffset((-7).hours),
                TimeZone("America/Denver")
            ),
            ZonedDateTime.ofLocal(
                DateTime(2019, 11, 3, 3, 30),
                TimeZone("America/New_York"),
                UtcOffset((-5).hours)
            ).adjustedTo("America/Denver".toTimeZone())
        )
    }

    @Test
    fun `adjustedTo() converts to a different time zone while preserving the instant during gaps`() {
        // New York in DST, Denver not yet
        assertEquals(
            ZonedDateTime.create(
                DateTime(2019, 3, 10, 1, 30),
                UtcOffset((-7).hours),
                TimeZone("America/Denver")
            ),
            ZonedDateTime(
                DateTime(2019, 3, 10, 4, 30),
                TimeZone("America/New_York")
            ).adjustedTo("America/Denver".toTimeZone())
        )

        // New York and Denver both in DST
        assertEquals(
            ZonedDateTime.create(
                DateTime(2019, 3, 10, 3, 30),
                UtcOffset((-6).hours),
                TimeZone("America/Denver")
            ),
            ZonedDateTime(
                DateTime(2019, 3, 10, 5, 30),
                TimeZone("America/New_York")
            ).adjustedTo("America/Denver".toTimeZone())
        )
    }

    @Test
    fun `toString() returns an ISO-8601 extended offset date-time along with a non-standard region ID`() {
        assertEquals(
            "2019-11-03T01:30Z[Etc/UTC]",
            ZonedDateTime(
                DateTime(2019, 11, 3, 1, 30),
                TimeZone.UTC
            ).toString()
        )

        assertEquals(
            "2019-11-03T01:30-05:00[America/New_York]",
            ZonedDateTime.create(
                DateTime(2019, 11, 3, 1, 30),
                UtcOffset((-5).hours),
                TimeZone("America/New_York")
            ).toString()
        )
    }

    @Test
    fun `String_toZonedDateTime() throws an exception when the string is empty`() {
        assertFailsWith<DateTimeParseException> { "".toZonedDateTime() }
    }

    @Test
    fun `String_toZonedDateTime() throws an exception when the format is unexpected`() {
        assertFailsWith<DateTimeParseException> { "2019-12-05T12:00+01:00America/New_York".toZonedDateTime() }
        assertFailsWith<DateTimeParseException> { "2019-12-05T12:00+01:00[America/New_York".toZonedDateTime() }
        assertFailsWith<DateTimeParseException> { "2019-12-05T12:00+01:00[]".toZonedDateTime() }
        assertFailsWith<DateTimeParseException> {
            "2019-12-05T12:00+01:00[America/New_York/one_more/characters/than_supported]".toZonedDateTime()
        }
    }

    @Test
    fun `String_toZonedDateTime() throws an exception when fields are out of range`() {
        assertFailsWith<DateTimeException> { "2000-01-01T24:00Z[Etc/Utc]".toZonedDateTime() }
        assertFailsWith<DateTimeException> { "2000-01-01T08:60-01:00[GMT+1]".toZonedDateTime() }
        assertFailsWith<DateTimeException> { "2000-13-01T08:59-01:00[GMT+1]".toZonedDateTime() }
        assertFailsWith<DateTimeException> { "2000-01-32T08:59-01:00[GMT+1]".toZonedDateTime() }
    }

    @Test
    fun `String_toZonedDateTime() throws an exception when the region is invalid`() {
        assertFailsWith<DateTimeException> { "2000-01-01T23:00+01:00[America/Boston]".toZonedDateTime() }
        assertFailsWith<DateTimeException> { "2000-01-01T23:00+01:00[Etc/GMT-20]".toZonedDateTime() }
    }

    @Test
    fun `String_toZonedDateTime() parses ISO-8601 calendar date time strings in extended format by default`() {
        assertEquals(
            ZonedDateTime.create(
                DateTime(Date(2019, Month.MAY, 5), Time.NOON),
                UtcOffset((-4).hours),
                TimeZone("America/New_York")
            ),
            "2019-05-05T12:00-04:00[America/New_York]".toZonedDateTime()
        )

        assertEquals(
            ZonedDateTime.create(
                DateTime(2019, 11, 3, 1, 0),
                UtcOffset((-5).hours),
                TimeZone("America/New_York")
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
                TimeZone("America/New_York")
            ),
            "20190505 1200-04[America/New_York]".toZonedDateTime(Iso8601.ZONED_DATE_TIME_PARSER)
        )
    }
}