package io.islandtime.format

import io.islandtime.DateTimeException
import io.islandtime.base.DateProperty
import io.islandtime.base.TimeProperty
import io.islandtime.locale.localeOf
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.*

@Suppress("PrivatePropertyName")
class DateTimeTextProviderTest : AbstractIslandTimeTest() {
    private val en_US = localeOf("en-US")
    private val de_DE = localeOf("de-DE")
    private val pl_PL = localeOf("pl-PL")

    @Test
    fun `textFor() throws an exception when value is out of range`() {
        assertFailsWith<DateTimeException> {
            DateTimeTextProvider.textFor(DateProperty.MonthOfYear, 0L, TextStyle.FULL, en_US)
        }
        assertFailsWith<DateTimeException> {
            DateTimeTextProvider.textFor(DateProperty.MonthOfYear, 13L, TextStyle.FULL, en_US)
        }
        assertFailsWith<DateTimeException> {
            DateTimeTextProvider.textFor(DateProperty.DayOfWeek, 0L, TextStyle.FULL, en_US)
        }
        assertFailsWith<DateTimeException> {
            DateTimeTextProvider.textFor(DateProperty.DayOfWeek, 8L, TextStyle.FULL, en_US)
        }
        assertFailsWith<DateTimeException> {
            DateTimeTextProvider.textFor(TimeProperty.AmPmOfDay, -1L, TextStyle.FULL, en_US)
        }
        assertFailsWith<DateTimeException> {
            DateTimeTextProvider.textFor(TimeProperty.AmPmOfDay, 2L, TextStyle.FULL, en_US)
        }
        assertFailsWith<DateTimeException> {
            DateTimeTextProvider.textFor(DateProperty.Era, -1L, TextStyle.FULL, en_US)
        }
        assertFailsWith<DateTimeException> {
            DateTimeTextProvider.textFor(DateProperty.Era, 2L, TextStyle.FULL, en_US)
        }
    }

    @Test
    fun `textFor() returns null when the field has no text representation`() {
        assertNull(DateTimeTextProvider.textFor(DateProperty.DayOfMonth, 1L, TextStyle.FULL, en_US))
        assertNull(DateTimeTextProvider.textFor(DateProperty.Year, 2010L, TextStyle.FULL, en_US))
    }

    @Test
    fun `parsableTextFor() returns an empty list when no styles are specified`() {
        assertTrue { DateTimeTextProvider.parsableTextFor(DateProperty.DayOfWeek, emptySet(), en_US).isEmpty() }
    }

    @Test
    fun `parsableTextFor() returns an empty list when the field has no text representation`() {
        assertTrue {
            DateTimeTextProvider.parsableTextFor(DateProperty.DayOfMonth, TextStyle.FULL, en_US).isEmpty()
        }
        assertTrue {
            DateTimeTextProvider.parsableTextFor(
                DateProperty.Year,
                setOf(TextStyle.FULL, TextStyle.NARROW),
                en_US
            ).isEmpty()
        }
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
                DateTimeTextProvider.textFor(DateProperty.MonthOfYear, 1L, it.first, en_US)
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
                DateTimeTextProvider.textFor(DateProperty.MonthOfYear, 1L, it.first, pl_PL)
            )
        }
    }

    @Test
    fun `parsable month text is returned in descending order by length`() {
        val expected = listOf(
            "September" to 9L,
            "February" to 2L,
            "November" to 11L,
            "December" to 12L,
            "January" to 1L,
            "October" to 10L,
            "August" to 8L,
            "March" to 3L,
            "April" to 4L,
            "June" to 6L,
            "July" to 7L,
            "May" to 5L
        )

        assertEquals(
            expected,
            DateTimeTextProvider.parsableTextFor(DateProperty.MonthOfYear, TextStyle.FULL, en_US)
        )

        // Do this a second time to verify that caching worked properly
        assertEquals(
            expected,
            DateTimeTextProvider.parsableTextFor(DateProperty.MonthOfYear, TextStyle.FULL, en_US)
        )
    }

    @Test
    fun `parsable month text is consolidated when possible when multiple styles are requested`() {
        val expected = listOf(
            "September" to 9L,
            "February" to 2L,
            "November" to 11L,
            "December" to 12L,
            "January" to 1L,
            "October" to 10L,
            "August" to 8L,
            "March" to 3L,
            "April" to 4L,
            "June" to 6L,
            "July" to 7L,
            "Jan" to 1L,
            "Feb" to 2L,
            "Mar" to 3L,
            "Apr" to 4L,
            "May" to 5L,
            "Jun" to 6L,
            "Jul" to 7L,
            "Aug" to 8L,
            "Sep" to 9L,
            "Oct" to 10L,
            "Nov" to 11L,
            "Dec" to 12L
        )

        assertEquals(
            expected,
            DateTimeTextProvider.parsableTextFor(
                DateProperty.MonthOfYear,
                setOf(TextStyle.FULL, TextStyle.FULL_STANDALONE, TextStyle.SHORT, TextStyle.SHORT_STANDALONE),
                en_US
            )
        )
    }

    @Test
    fun `parsable month text does not include strings with conflicting values`() {
        val expected = listOf(
            "F" to 2L,
            "S" to 9L,
            "O" to 10L,
            "N" to 11L,
            "D" to 12L
        )

        assertEquals(
            expected,
            DateTimeTextProvider.parsableTextFor(DateProperty.MonthOfYear, TextStyle.NARROW, en_US)
        )
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
                DateTimeTextProvider.textFor(DateProperty.DayOfWeek, 1L, it.first, en_US)?.removeSuffix(".")
            )
        }

        assertEquals(
            "Sunday",
            DateTimeTextProvider.textFor(DateProperty.DayOfWeek, 7L, TextStyle.FULL, en_US)
        )
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
                DateTimeTextProvider.textFor(DateProperty.DayOfWeek, 1L, it.first, de_DE)?.removeSuffix(".")
            )
        }
    }

    @Test
    fun `parsable day of week text is returned in descending order by length`() {
        val expected = listOf(
            "Wednesday" to 3L,
            "Thursday" to 4L,
            "Saturday" to 6L,
            "Tuesday" to 2L,
            "Monday" to 1L,
            "Friday" to 5L,
            "Sunday" to 7L
        )

        assertEquals(
            expected,
            DateTimeTextProvider.parsableTextFor(DateProperty.DayOfWeek, TextStyle.FULL, en_US)
        )

        // Do this a second time to verify that caching worked properly
        assertEquals(
            expected,
            DateTimeTextProvider.parsableTextFor(DateProperty.DayOfWeek, TextStyle.FULL, en_US)
        )
    }

    @Test
    fun `parsable day of week text is consolidated when possible when multiple styles are requested`() {
        val expected = listOf(
            "Wednesday" to 3L,
            "Thursday" to 4L,
            "Saturday" to 6L,
            "Tuesday" to 2L,
            "Monday" to 1L,
            "Friday" to 5L,
            "Sunday" to 7L,
            "Mon" to 1L,
            "Tue" to 2L,
            "Wed" to 3L,
            "Thu" to 4L,
            "Fri" to 5L,
            "Sat" to 6L,
            "Sun" to 7L
        )

        assertEquals(
            expected,
            DateTimeTextProvider.parsableTextFor(
                DateProperty.DayOfWeek,
                setOf(TextStyle.FULL, TextStyle.FULL_STANDALONE, TextStyle.SHORT, TextStyle.SHORT_STANDALONE),
                en_US
            )
        )
    }

    @Test
    fun `parsable day of week text does not include strings with conflicting values`() {
        val expected = listOf(
            "M" to 1L,
            "W" to 3L,
            "F" to 5L
        )

        assertEquals(
            expected,
            DateTimeTextProvider.parsableTextFor(DateProperty.DayOfWeek, TextStyle.NARROW, en_US)
        )
    }

    @Test
    fun `am-pm text in en-US`() {
        TextStyle.values().forEach {
            assertEquals(
                "AM",
                DateTimeTextProvider.textFor(TimeProperty.AmPmOfDay, 0L, it, en_US)
            )
            assertEquals(
                "PM",
                DateTimeTextProvider.textFor(TimeProperty.AmPmOfDay, 1L, it, en_US)
            )
        }
    }

    @Test
    fun `parsable am-pm text is returned in descending order by length`() {
        val expected = listOf(
            "AM" to 0L,
            "PM" to 1L
        )

        assertEquals(
            expected,
            DateTimeTextProvider.parsableTextFor(TimeProperty.AmPmOfDay, TextStyle.FULL, en_US)
        )

        // Do this a second time to verify that caching worked properly
        assertEquals(
            expected,
            DateTimeTextProvider.parsableTextFor(TimeProperty.AmPmOfDay, TextStyle.FULL, en_US)
        )
    }

    @Test
    fun `era text in en-US`() {
        listOf(
            TextStyle.FULL to listOf("Before Christ", "Anno Domini"),
            TextStyle.FULL_STANDALONE to listOf("Before Christ", "Anno Domini"),
            TextStyle.SHORT to listOf("BC", "AD"),
            TextStyle.SHORT_STANDALONE to listOf("BC", "AD"),
            TextStyle.NARROW to listOf("B", "A"),
            TextStyle.NARROW_STANDALONE to listOf("B", "A")
        ).forEach {
            it.second.forEachIndexed { index, eraValue ->
                assertEquals(
                    eraValue,
                    DateTimeTextProvider.textFor(DateProperty.Era, index.toLong(), it.first, en_US)
                )
            }
        }
    }

    @Test
    fun `era text in de-DE`() {
        listOf(
            TextStyle.FULL to listOf("v. Chr.", "n. Chr."),
            TextStyle.FULL_STANDALONE to listOf("v. Chr.", "n. Chr."),
            TextStyle.SHORT to listOf("v. Chr.", "n. Chr."),
            TextStyle.SHORT_STANDALONE to listOf("v. Chr.", "n. Chr."),
            TextStyle.NARROW to listOf("B", "A"),
            TextStyle.NARROW_STANDALONE to listOf("B", "A")
        ).forEach {
            it.second.forEachIndexed { index, eraValue ->
                assertEquals(
                    eraValue,
                    DateTimeTextProvider.textFor(DateProperty.Era, index.toLong(), it.first, de_DE)
                )
            }
        }
    }

    @Test
    fun `parsable era text is returned in descending order by length`() {
        val expected = listOf(
            "Before Christ" to 0L,
            "Anno Domini" to 1L
        )

        assertEquals(
            expected,
            DateTimeTextProvider.parsableTextFor(DateProperty.Era, TextStyle.FULL, en_US)
        )

        // Do this a second time to verify that caching worked properly
        assertEquals(
            expected,
            DateTimeTextProvider.parsableTextFor(DateProperty.Era, TextStyle.FULL, en_US)
        )
    }
}