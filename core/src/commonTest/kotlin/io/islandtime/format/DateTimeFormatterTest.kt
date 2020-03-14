package io.islandtime.format

import io.islandtime.Date
import io.islandtime.Month
import kotlin.test.Test
import kotlin.test.assertEquals


class DateTimeFormatterTest {
    @Test
    fun `format Date`() {
        val date = Date(2020, Month.FEBRUARY, 1)
        val result = DateTimeFormatters.Iso.CALENDAR_DATE.format(date)
        assertEquals("2020-02-01", result)
    }
}