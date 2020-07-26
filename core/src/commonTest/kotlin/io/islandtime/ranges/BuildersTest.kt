package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BuildersTest : AbstractIslandTimeTest() {
    private val nyZone = TimeZone("America/New_York")

    @Test
    fun `convert empty DateRange to ZonedDateTimeInterval`() {
        assertEquals(ZonedDateTimeInterval.EMPTY, DateRange.EMPTY at nyZone)
    }

    @Test
    fun `convert unbounded DateRange to ZonedDateTimeInterval`() {
        assertEquals(ZonedDateTimeInterval.UNBOUNDED, DateRange.UNBOUNDED at nyZone)
    }

    @Test
    fun `convert half-bounded DateRange to ZonedDateTimeInterval`() {
        val dateRange1 = Date.MIN..Date(2019, Month.MARCH, 12)

        assertEquals(
            ZonedDateTimeInterval(
                DateTime.MIN at nyZone,
                Date(2019, Month.MARCH, 13) at Time.MIDNIGHT at nyZone
            ),
            dateRange1 at nyZone
        )

        val dateRange2 = Date(2019, Month.MARCH, 10)..Date.MAX

        assertEquals(
            ZonedDateTimeInterval(
                Date(2019, Month.MARCH, 10) at Time.MIDNIGHT at nyZone,
                DateTime.MAX at nyZone
            ),
            dateRange2 at nyZone
        )
    }

    @Test
    fun `convert bounded DateRange to ZonedDateTimeInterval`() {
        val dateRange1 = Date(2019, Month.MARCH, 10)..Date(2019, Month.MARCH, 12)

        assertEquals(
            ZonedDateTimeInterval(
                Date(2019, Month.MARCH, 10) at Time.MIDNIGHT at nyZone,
                Date(2019, Month.MARCH, 13) at Time.MIDNIGHT at nyZone
            ),
            dateRange1 at nyZone
        )

        val dateRange2 = Date(2019, Month.MARCH, 10)..Date(2019, Month.MARCH, 10)

        assertEquals(
            ZonedDateTimeInterval(
                Date(2019, Month.MARCH, 10) at Time.MIDNIGHT at nyZone,
                Date(2019, Month.MARCH, 11) at Time.MIDNIGHT at nyZone
            ),
            dateRange2 at nyZone
        )
    }

    @Test
    fun `convert empty DateTimeInterval to ZonedDateTimeInterval`() {
        assertEquals(ZonedDateTimeInterval.EMPTY, DateTimeInterval.EMPTY at nyZone)
    }

    @Test
    fun `convert unbounded DateTimeInterval to ZonedDateTimeInterval`() {
        assertEquals(ZonedDateTimeInterval.UNBOUNDED, DateTimeInterval.UNBOUNDED at nyZone)
    }

    @Test
    fun `convert half-bounded DateTimeInterval to ZonedDateTimeInterval`() {
        val interval1 = DateTime.MIN until DateTime(2019, Month.MARCH, 12, 2, 0)

        assertEquals(
            ZonedDateTimeInterval(
                DateTime.MIN at nyZone,
                DateTime(2019, Month.MARCH, 12, 2, 0) at nyZone
            ),
            interval1 at nyZone
        )

        val interval2 = DateTime(2019, Month.MARCH, 10, 2, 0) until DateTime.MAX

        assertEquals(
            ZonedDateTimeInterval(
                DateTime(2019, Month.MARCH, 10, 2, 0) at nyZone,
                DateTime.MAX at nyZone
            ),
            interval2 at nyZone
        )
    }

    @Test
    fun `convert bounded DateTimeInterval to ZonedDateTimeInterval`() {
        val interval = DateTime(2019, Month.MARCH, 10, 2, 0) until
            DateTime(2019, Month.MARCH, 12, 14, 0)

        assertEquals(
            ZonedDateTimeInterval(
                DateTime(2019, Month.MARCH, 10, 2, 0) at nyZone,
                DateTime(2019, Month.MARCH, 12, 14, 0) at nyZone
            ),
            interval at nyZone
        )
    }

    @Test
    fun `convert empty InstantInterval to ZonedDateTimeInterval`() {
        assertEquals(ZonedDateTimeInterval.EMPTY, InstantInterval.EMPTY at nyZone)
    }

    @Test
    fun `convert unbounded InstantInterval to ZonedDateTimeInterval`() {
        assertEquals(ZonedDateTimeInterval.UNBOUNDED, InstantInterval.UNBOUNDED at nyZone)
    }

    @Test
    fun `convert half-bounded InstantInterval to ZonedDateTimeInterval`() {
        val interval1 = Instant.MIN until "2019-03-12T06:00Z".toInstant()

        assertEquals(
            ZonedDateTimeInterval(
                DateTime.MIN at nyZone,
                DateTime(2019, Month.MARCH, 12, 2, 0) at nyZone
            ),
            interval1 at nyZone
        )

        val interval2 = "2019-03-10T07:00Z".toInstant() until Instant.MAX

        assertEquals(
            ZonedDateTimeInterval(
                DateTime(2019, Month.MARCH, 10, 2, 0) at nyZone,
                DateTime.MAX at nyZone
            ),
            interval2 at nyZone
        )
    }

    @Test
    fun `convert bounded InstantInterval to ZonedDateTimeInterval`() {
        val interval = "2019-03-10T07:00Z/2019-03-12T18:00Z".toInstantInterval()

        assertEquals(
            ZonedDateTimeInterval(
                DateTime(2019, Month.MARCH, 10, 2, 0) at nyZone,
                DateTime(2019, Month.MARCH, 12, 14, 0) at nyZone
            ),
            interval at nyZone
        )
    }
}