package io.islandtime.format

import io.islandtime.DateTimeException
import io.islandtime.TimeZone
import io.islandtime.base.DateTimeField
import io.islandtime.locale.localeFor
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.*

class DateTimeTextProviderTest : AbstractIslandTimeTest() {
    private val en_US = localeFor("en-US")
    private val de_DE = localeFor("de-DE")
    private val pl_PL = localeFor("pl-PL")

    @Test
    fun `textFor() throws an exception when value is out of range`() {
        assertFailsWith<DateTimeException> {
            DateTimeTextProvider.textFor(DateTimeField.MONTH_OF_YEAR, 0L, TextStyle.FULL, en_US)
        }
        assertFailsWith<DateTimeException> {
            DateTimeTextProvider.textFor(DateTimeField.MONTH_OF_YEAR, 13L, TextStyle.FULL, en_US)
        }
        assertFailsWith<DateTimeException> {
            DateTimeTextProvider.textFor(DateTimeField.DAY_OF_WEEK, 0L, TextStyle.FULL, en_US)
        }
        assertFailsWith<DateTimeException> {
            DateTimeTextProvider.textFor(DateTimeField.DAY_OF_WEEK, 8L, TextStyle.FULL, en_US)
        }
        assertFailsWith<DateTimeException> {
            DateTimeTextProvider.textFor(DateTimeField.AM_PM_OF_DAY, -1L, TextStyle.FULL, en_US)
        }
        assertFailsWith<DateTimeException> {
            DateTimeTextProvider.textFor(DateTimeField.AM_PM_OF_DAY, 2L, TextStyle.FULL, en_US)
        }
    }

    @Test
    fun `textFor() returns null when the field has no text representation`() {
        assertNull(DateTimeTextProvider.textFor(DateTimeField.DAY_OF_MONTH, 1L, TextStyle.FULL, en_US))
        assertNull(DateTimeTextProvider.textFor(DateTimeField.YEAR, 2010L, TextStyle.FULL, en_US))
    }

    @Test
    fun `month text in en-US`() {
        listOf(
            TextStyle.FULL to "January",
            TextStyle.FULL_STANDALONE to "January",
            TextStyle.SHORT to "Jan",
            TextStyle.SHORT_STANDALONE to "Jan",
            TextStyle.NARROW to "J",
            TextStyle.NARROW_STANDALONE to "J"
        ).forEach {
            assertEquals(
                it.second,
                DateTimeTextProvider.textFor(DateTimeField.MONTH_OF_YEAR, 1L, it.first, en_US)
            )
            assertEquals(
                it.second,
                DateTimeTextProvider.monthTextFor(1L, it.first, en_US)
            )
        }
    }

    @Test
    fun `month text in pl-PL`() {
        listOf(
            TextStyle.FULL to "stycznia",
            TextStyle.FULL_STANDALONE to "styczeń",
            TextStyle.SHORT to "sty",
            TextStyle.SHORT_STANDALONE to "sty",
            TextStyle.NARROW to "s",
            TextStyle.NARROW_STANDALONE to "s"
        ).forEach {
            assertEquals(
                it.second,
                DateTimeTextProvider.textFor(DateTimeField.MONTH_OF_YEAR, 1L, it.first, pl_PL)
            )
            assertEquals(
                it.second,
                DateTimeTextProvider.monthTextFor(1L, it.first, pl_PL)
            )
        }
    }

    @Test
    fun `day of week text in en-US`() {
        listOf(
            TextStyle.FULL to "Monday",
            TextStyle.FULL_STANDALONE to "Monday",
            TextStyle.SHORT to "Mon",
            TextStyle.SHORT_STANDALONE to "Mon",
            TextStyle.NARROW to "M",
            TextStyle.NARROW_STANDALONE to "M"
        ).forEach {
            assertEquals(
                it.second,
                DateTimeTextProvider.textFor(DateTimeField.DAY_OF_WEEK, 1L, it.first, en_US)?.removeSuffix(".")
            )
            assertEquals(
                it.second,
                DateTimeTextProvider.dayOfWeekTextFor(1L, it.first, en_US)?.removeSuffix(".")
            )
        }
    }

    @Test
    fun `day of week text in de-DE`() {
        listOf(
            TextStyle.FULL to "Montag",
            TextStyle.FULL_STANDALONE to "Montag",
            TextStyle.SHORT to "Mo",
            TextStyle.SHORT_STANDALONE to "Mo",
            TextStyle.NARROW to "M",
            TextStyle.NARROW_STANDALONE to "M"
        ).forEach {
            assertEquals(
                it.second,
                DateTimeTextProvider.textFor(DateTimeField.DAY_OF_WEEK, 1L, it.first, de_DE)?.removeSuffix(".")
            )
            assertEquals(
                it.second,
                DateTimeTextProvider.dayOfWeekTextFor(1L, it.first, de_DE)?.removeSuffix(".")
            )
        }
    }

    @Test
    fun `am-pm text in en-US`() {
        TextStyle.values().forEach {
            assertEquals(
                "AM",
                DateTimeTextProvider.textFor(DateTimeField.AM_PM_OF_DAY, 0L, it, en_US)
            )
            assertEquals(
                "PM",
                DateTimeTextProvider.textFor(DateTimeField.AM_PM_OF_DAY, 1L, it, en_US)
            )
        }

        assertEquals(
            "AM",
            DateTimeTextProvider.amPmTextFor(0L, en_US)
        )
        assertEquals(
            "PM",
            DateTimeTextProvider.amPmTextFor(1L, en_US)
        )
    }

    @Test
    fun `timeZoneTextFor() returns null when given a fixed offset time zone`() {
        listOf(
            TimeZone.FixedOffset("-04:00"),
            TimeZone.FixedOffset("+00:00"),
            TimeZone.FixedOffset("+14:00")
        ).forEach { zone ->
            TimeZoneTextStyle.values().forEach { style ->
                assertNull(DateTimeTextProvider.timeZoneTextFor(zone, style, en_US))
                assertNull(DateTimeTextProvider.timeZoneTextFor(zone, style, de_DE))
            }
        }
    }

    @Test
    fun `timeZoneTextFor() returns a localized string when available`() {
        val zone = TimeZone("America/New_York")

        assertEquals(
            "Eastern Standard Time",
            DateTimeTextProvider.timeZoneTextFor(zone, TimeZoneTextStyle.STANDARD, en_US)
        )
        assertEquals(
            "EST",
            DateTimeTextProvider.timeZoneTextFor(zone, TimeZoneTextStyle.SHORT_STANDARD, en_US)
        )
        assertEquals(
            "Eastern Daylight Time",
            DateTimeTextProvider.timeZoneTextFor(zone, TimeZoneTextStyle.DAYLIGHT_SAVING, en_US)
        )
        assertEquals(
            "EDT",
            DateTimeTextProvider.timeZoneTextFor(zone, TimeZoneTextStyle.SHORT_DAYLIGHT_SAVING, en_US)
        )
    }

    @Test
    fun `timeZoneTextFor() returns null when the zone is invalid`() {
        val zone = TimeZone("America/Boston")
        assertFalse { zone.isValid }

        TimeZoneTextStyle.values().forEach { style ->
            assertNull(DateTimeTextProvider.timeZoneTextFor(zone, style, en_US))
        }
    }
}