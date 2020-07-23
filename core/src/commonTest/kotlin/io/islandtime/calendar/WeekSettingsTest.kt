package io.islandtime.calendar

import io.islandtime.DayOfWeek
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class WeekSettingsTest {
    @Test
    fun `constructor throws an exception when minimumDaysInFirstWeek is out of range`() {
        assertFailsWith<IllegalArgumentException> { WeekSettings(DayOfWeek.SUNDAY,  minimumDaysInFirstWeek = 0) }
        assertFailsWith<IllegalArgumentException> { WeekSettings(DayOfWeek.SUNDAY,  minimumDaysInFirstWeek = 8) }
    }

    @Test
    fun `can be constructed when minimumDaysInFirstWeek is in range`() {
        (1..7).forEach {
            assertEquals(it, WeekSettings(DayOfWeek.SATURDAY, it).minimumDaysInFirstWeek)
        }
    }
}