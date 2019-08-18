package dev.erikchristensen.islandtime.interval

import kotlin.test.Test
import kotlin.test.assertEquals

class HoursTest {
    @Test
    fun `toComponents() breaks the hours up into days and hours`() {
        36.hours.toComponents { days, hours ->
            assertEquals(1.days, days)
            assertEquals(12.hours, hours)
        }
    }
}