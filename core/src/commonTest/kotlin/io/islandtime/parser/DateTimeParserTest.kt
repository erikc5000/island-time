package io.islandtime.parser

import io.islandtime.Date
import io.islandtime.test.AbstractIslandTimeTest
import io.islandtime.test.FakeDateTimeTextProvider
import io.islandtime.toDate
import kotlin.test.Test
import kotlin.test.assertEquals

class DateTimeParserTest : AbstractIslandTimeTest(
    testDateTimeTextProvider = FakeDateTimeTextProvider
) {
    @Test
    fun `create parser from date-time pattern`() {
        val parser = DateTimeParser("uuuu-MM-dd")
        val date = "2020-10-26".toDate(parser)
        assertEquals(Date(2020, 10, 26), date)
    }
}
