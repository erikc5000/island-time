package io.islandtime

import io.islandtime.measures.milliseconds
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BuildersTest : AbstractIslandTimeTest() {
    private val nyZone = TimeZone("America/New_York")

    @Test
    fun `combine a Year and Month to create a YearMonth`() {
        assertEquals(
            YearMonth(2018, Month.DECEMBER),
            Year(2018) at Month.DECEMBER
        )

        assertEquals(
            YearMonth(2018, Month.APRIL),
            Year(2018) at 4.toMonth()
        )
    }

    @Test
    fun `Year_atMonth creates a YearMonth`() {
        assertEquals(
            YearMonth(2018, Month.APRIL),
            Year(2018).atMonth(4)
        )
    }

    @Test
    fun `combine a Date and Time to create a DateTime`() {
        val today = Date(2019, Month.JANUARY, 1)

        assertEquals(
            DateTime(today, Time(2, 30)),
            today at "02:30".toTime()
        )
    }

    @Test
    fun `YearMonth_atDay creates a Date`() {
        assertEquals(
            Date(2018, Month.MAY, 30),
            YearMonth(2018, Month.MAY).atDay(30)
        )
    }

    @Test
    fun `combine a Date and OffsetTime to create an OffsetDateTime`() {
        assertEquals(
            OffsetDateTime(
                2019, Month.FEBRUARY, 1, 1, 2, 3, 4, UtcOffset.MIN
            ),
            Date(2019, 32) at
                OffsetTime(1, 2, 3, 4, UtcOffset.MIN)
        )
    }

    @Test
    fun `combine DateTime and TimeZone to create a ZonedDateTime`() {
        assertEquals(
            ZonedDateTime(
                2019,
                3,
                3,
                1,
                0,
                0,
                0,
                TimeZone.UTC
            ),
            DateTime(2019, 3, 3, 1, 0) at TimeZone.UTC
        )

        assertEquals(
            ZonedDateTime(
                2019,
                3,
                3,
                1,
                0,
                0,
                0,
                nyZone
            ),
            DateTime(2019, 3, 3, 1, 0) at
                nyZone
        )
    }

    @Test
    fun `combine an Instant and TimeZone to create a ZonedDateTime`() {
        assertEquals(
            ZonedDateTime(
                2019,
                8,
                19,
                23,
                7,
                27,
                821_000_000,
                TimeZone.UTC
            ),
            Instant(1566256047821L.milliseconds) at TimeZone.UTC
        )

        assertEquals(
            ZonedDateTime(
                2019,
                8,
                19,
                19,
                7,
                27,
                821_000_000,
                nyZone
            ),
            Instant(1566256047821L.milliseconds) at nyZone
        )
    }

    @Test
    fun `Date_startOfDay returns the DateTime at midnight of same day`() {
        assertEquals(
            DateTime(
                Date(2019, Month.JULY, 1),
                Time.MIDNIGHT
            ),
            Date(2019, Month.JULY, 1).startOfDay
        )
    }

    @Test
    fun `Date_endOfDay returns the DateTime at the end of the same day`() {
        assertEquals(
            DateTime(
                Date(2019, Month.JULY, 1),
                Time(23, 59, 59, 999_999_999)
            ),
            Date(2019, Month.JULY, 1).endOfDay
        )
    }

    @Test
    fun `Date_startOfDayAt returns the ZonedDateTime at the start of the day in a time zone`() {
        assertEquals(
            ZonedDateTime(
                DateTime(2019, 5, 20, 0, 0),
                nyZone
            ),
            Date(2019, 5, 20).startOfDayAt(nyZone)
        )

        // TODO: Add tests where transitions occur during midnight
    }

    @Test
    fun `Date_endOfDayAt returns the ZonedDateTime at the end of the day in a time zone`() {
        assertEquals(
            ZonedDateTime(
                DateTime(2019, 5, 20, 23, 59, 59, 999_999_999),
                nyZone
            ),
            Date(2019, 5, 20).endOfDayAt(nyZone)
        )

        // TODO: Add tests where transitions occur during midnight
    }
}
