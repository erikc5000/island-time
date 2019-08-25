package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.interval.days
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DayOfWeekTest {
    @Test
    fun `DayOfWeek() returns the day of the week by number, starting with Monday`() {
        assertEquals(DayOfWeek.WEDNESDAY, DayOfWeek(3))
    }

    @Test
    fun `Int_toDayOfWeek() throws an exception when the number is out of range`() {
        assertFailsWith<IllegalArgumentException> { 0.toDayOfWeek() }
        assertFailsWith<IllegalArgumentException> { 8.toDayOfWeek() }
        assertFailsWith<IllegalArgumentException> { (-1).toDayOfWeek() }
    }

    @Test
    fun `Int_toDayOfWeek() returns the correct day`() {
        assertEquals(DayOfWeek.MONDAY, 1.toDayOfWeek())
        assertEquals(DayOfWeek.SUNDAY, 7.toDayOfWeek())
    }

    @Test
    fun `Day of week number matches ISO-8601`() {
        assertEquals(1, DayOfWeek.MONDAY.number)
        assertEquals(3, DayOfWeek.WEDNESDAY.number)
    }

    @Test
    fun `adds zero days`() {
        assertEquals(DayOfWeek.TUESDAY, DayOfWeek.TUESDAY + 0.days)
    }

    @Test
    fun `adds positive days`() {
        assertEquals(DayOfWeek.MONDAY, DayOfWeek.SUNDAY + 1.days)
        assertEquals(DayOfWeek.TUESDAY, DayOfWeek.TUESDAY + 7.days)
    }

    @Test
    fun `adds negative days`() {
        assertEquals(DayOfWeek.SATURDAY, DayOfWeek.MONDAY + (-2).days)
        assertEquals(DayOfWeek.MONDAY, DayOfWeek.MONDAY + (-7).days)
    }

    @Test
    fun `subtracts zero days`() {
        assertEquals(DayOfWeek.MONDAY, DayOfWeek.MONDAY - 0.days)
    }

    @Test
    fun `subtracts positive days`() {
        assertEquals(DayOfWeek.SATURDAY, DayOfWeek.MONDAY - 2.days)
        assertEquals(DayOfWeek.MONDAY, DayOfWeek.MONDAY - 7.days)
    }

    @Test
    fun `subtracts negative days`() {
        assertEquals(DayOfWeek.MONDAY, DayOfWeek.SUNDAY - (-1).days)
        assertEquals(DayOfWeek.TUESDAY, DayOfWeek.TUESDAY - (-7).days)
    }
}