package io.islandtime.measures

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MonthsTest {
    @Test
    fun `Months can be compared to other Months`() {
        assertTrue { 0.months < 1.months }
        assertTrue { 0.months == 0.months }
        assertTrue { 5.months > (-1).months }
    }

    @Test
    fun `adding years to months produces months`() {
        assertEquals(13.months, 1.months + 1.years)
    }

    @Test
    fun `subtracting years from months produces months`() {
        assertEquals(0.months, 12.months - 1.years)
    }

    @Test
    fun `inWholeYears converts months to an equivalent number of full years`() {
        assertEquals(1.years, 13.months.inWholeYears)
    }
}
