package io.islandtime.format

import io.islandtime.*
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class IsoDateTimeFormatterTest : AbstractIslandTimeTest() {
    private val zonedDateTime =
        Date(2010, Month.SEPTEMBER, 3) at
            Time(1, 2, 3, 4) at
            TimeZone("America/Denver")

    @Test
    fun `by default, includes all present components in extended format`() {
        val formatter = isoDateTimeFormatter()

        assertEquals("2010", formatter.format(Year(zonedDateTime.year)))
        assertEquals("2010-09", formatter.format(zonedDateTime.yearMonth))
        assertEquals("2010-09-03", formatter.format(zonedDateTime.date))
        assertEquals(
            "2010-09-03T01:02:03.000000004",
            formatter.format(zonedDateTime.dateTime)
        )
        assertEquals(
            "2010-09-03T01:02:03.000000004-06:00",
            formatter.format(zonedDateTime.offsetDateTime)
        )
        assertEquals(
            "2010-09-03T01:02:03.000000004-06:00",
            formatter.format(zonedDateTime.withFixedOffsetZone())
        )
        assertEquals(
            "2010-09-03T01:02:03.000000004-06:00[America/Denver]",
            formatter.format(zonedDateTime)
        )
        assertEquals("01:02:03.000000004", formatter.format(zonedDateTime.time))
        assertEquals("01:02:03.000000004-06:00", formatter.format(zonedDateTime.offsetTime))
    }

    @Test
    fun `basic format with all present components`() {
        val formatter = isoDateTimeFormatter {
            format = IsoFormat.BASIC
        }

        assertEquals("2010", formatter.format(Year(zonedDateTime.year)))
        assertEquals("2010-09", formatter.format(zonedDateTime.yearMonth))
        assertEquals("20100903", formatter.format(zonedDateTime.date))
        assertEquals("20100903T010203.000000004", formatter.format(zonedDateTime.dateTime))
        assertEquals(
            "20100903T010203.000000004-0600",
            formatter.format(zonedDateTime.offsetDateTime)
        )
        assertEquals(
            "20100903T010203.000000004-0600",
            formatter.format(zonedDateTime.withFixedOffsetZone())
        )
        assertEquals(
            "20100903T010203.000000004-0600[America/Denver]",
            formatter.format(zonedDateTime)
        )
        assertEquals("010203.000000004", formatter.format(zonedDateTime.time))
        assertEquals("010203.000000004-0600", formatter.format(zonedDateTime.offsetTime))
    }
}