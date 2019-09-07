package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.interval.milliseconds
import dev.erikchristensen.islandtime.tz.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RegionalDateTimeTest {

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
    fun at_infix_creates_a_RegionalDateTime_from_a_DateTime() {
        assertEquals(
            RegionalDateTime(
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
            RegionalDateTime(
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
    fun at_infix_creates_a_RegionalDateTime_from_an_instant() {
        assertEquals(
            RegionalDateTime(
                2019,
                8,
                19,
                23,
                7,
                27,
                821_000_000,
                TimeZone.UTC
            ),
            Instant(1566256047821L.milliseconds) at TimeZone.UTC
        )

        assertEquals(
            RegionalDateTime(
                2019,
                8,
                19,
                19,
                7,
                27,
                821_000_000,
                TimeZone("America/New_York")
            ),
            Instant(1566256047821L.milliseconds) at TimeZone("America/New_York")
        )
    }
}