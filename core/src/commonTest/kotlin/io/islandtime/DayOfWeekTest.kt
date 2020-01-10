package io.islandtime

import io.islandtime.format.TextStyle
import io.islandtime.locale.localeOf
import io.islandtime.measures.days
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@Suppress("PrivatePropertyName")
class DayOfWeekTest : AbstractIslandTimeTest() {
    private val en_US = localeOf("en-US")
    private val de_DE = localeOf("de-DE")
    private val ar_EG = localeOf("ar-EG")

    @Test
    fun `Int_toDayOfWeek() throws an exception when the number is out of range`() {
        assertFailsWith<DateTimeException> { 0.toDayOfWeek() }
        assertFailsWith<DateTimeException> { 8.toDayOfWeek() }
        assertFailsWith<DateTimeException> { (-1).toDayOfWeek() }
    }

    @Test
    fun `Int_toDayOfWeek() returns the correct day`() {
        assertEquals(DayOfWeek.MONDAY, 1.toDayOfWeek())
        assertEquals(DayOfWeek.WEDNESDAY, 3.toDayOfWeek())
        assertEquals(DayOfWeek.SUNDAY, 7.toDayOfWeek())
    }

    @Test
    fun `number property matches ISO-8601 day of week number`() {
        assertEquals(1, DayOfWeek.MONDAY.number)
        assertEquals(3, DayOfWeek.WEDNESDAY.number)
    }

    @Test
    fun `localizedNumber() matches locale`() {
        assertEquals(1, DayOfWeek.SUNDAY.localizedNumber(en_US))
        assertEquals(7, DayOfWeek.SATURDAY.localizedNumber(en_US))

        assertEquals(1, DayOfWeek.MONDAY.localizedNumber(de_DE))
        assertEquals(7, DayOfWeek.SUNDAY.localizedNumber(de_DE))

        assertEquals(1, DayOfWeek.SATURDAY.localizedNumber(ar_EG))
        assertEquals(7, DayOfWeek.FRIDAY.localizedNumber(ar_EG))
    }

    @Test
    fun `localizedName() and displayName() get localized text from the provider`() {
        assertEquals("Wednesday", DayOfWeek.WEDNESDAY.localizedName(TextStyle.FULL_STANDALONE, en_US))
        assertEquals("Wed", DayOfWeek.WEDNESDAY.displayName(TextStyle.SHORT_STANDALONE, en_US))
    }

    @Test
    fun `adds zero days`() {
        assertEquals(DayOfWeek.TUESDAY, DayOfWeek.TUESDAY + 0.days)
        assertEquals(DayOfWeek.TUESDAY, DayOfWeek.TUESDAY + 0L.days)
    }

    @Test
    fun `adds positive days`() {
        assertEquals(DayOfWeek.MONDAY, DayOfWeek.SUNDAY + 1.days)
        assertEquals(DayOfWeek.TUESDAY, DayOfWeek.TUESDAY + 7.days)
        assertEquals(DayOfWeek.TUESDAY, DayOfWeek.MONDAY + Int.MAX_VALUE.days)

        assertEquals(DayOfWeek.MONDAY, DayOfWeek.SUNDAY + 1L.days)
        assertEquals(DayOfWeek.TUESDAY, DayOfWeek.TUESDAY + 7L.days)
        assertEquals(DayOfWeek.MONDAY, DayOfWeek.MONDAY + Long.MAX_VALUE.days)
    }

    @Test
    fun `adds negative days`() {
        assertEquals(DayOfWeek.SATURDAY, DayOfWeek.MONDAY + (-2).days)
        assertEquals(DayOfWeek.MONDAY, DayOfWeek.MONDAY + (-7).days)
        assertEquals(DayOfWeek.SATURDAY, DayOfWeek.MONDAY + Int.MIN_VALUE.days)

        assertEquals(DayOfWeek.SATURDAY, DayOfWeek.MONDAY + (-2L).days)
        assertEquals(DayOfWeek.MONDAY, DayOfWeek.MONDAY + (-7L).days)
        assertEquals(DayOfWeek.SUNDAY, DayOfWeek.MONDAY + Long.MIN_VALUE.days)
    }

    @Test
    fun `subtracts zero days`() {
        assertEquals(DayOfWeek.MONDAY, DayOfWeek.MONDAY - 0.days)
        assertEquals(DayOfWeek.MONDAY, DayOfWeek.MONDAY - 0L.days)
    }

    @Test
    fun `subtracts positive days`() {
        assertEquals(DayOfWeek.SATURDAY, DayOfWeek.MONDAY - 2.days)
        assertEquals(DayOfWeek.MONDAY, DayOfWeek.MONDAY - 7.days)
        assertEquals(DayOfWeek.SUNDAY, DayOfWeek.MONDAY - Int.MAX_VALUE.days)

        assertEquals(DayOfWeek.SATURDAY, DayOfWeek.MONDAY - 2L.days)
        assertEquals(DayOfWeek.MONDAY, DayOfWeek.MONDAY - 7L.days)
        assertEquals(DayOfWeek.MONDAY, DayOfWeek.MONDAY - Long.MAX_VALUE.days)
    }

    @Test
    fun `subtracts negative days`() {
        assertEquals(DayOfWeek.MONDAY, DayOfWeek.SUNDAY - (-1).days)
        assertEquals(DayOfWeek.TUESDAY, DayOfWeek.TUESDAY - (-7).days)
        assertEquals(DayOfWeek.WEDNESDAY, DayOfWeek.MONDAY - Int.MIN_VALUE.days)

        assertEquals(DayOfWeek.MONDAY, DayOfWeek.SUNDAY - (-1L).days)
        assertEquals(DayOfWeek.TUESDAY, DayOfWeek.TUESDAY - (-7L).days)
        assertEquals(DayOfWeek.TUESDAY, DayOfWeek.MONDAY - Long.MIN_VALUE.days)
    }
}