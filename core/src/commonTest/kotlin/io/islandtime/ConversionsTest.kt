package io.islandtime

import io.islandtime.measures.hours
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ConversionsTest : AbstractIslandTimeTest() {
    private val nyZone = TimeZone("America/New_York")
    private val denverZone = TimeZone("America/Denver")

    @Test
    fun `YearMonth_toYear() converts to Year`() {
        assertEquals(Year(2018), YearMonth(2018, Month.JULY).toYear())
    }

    @Test
    fun `Date_toYear() converts to Year`() {
        assertEquals(Year(2018), Date(2018, Month.JULY, 4).toYear())
    }

    @Test
    fun `DateTime_toYear() converts to Year`() {
        val dateTime = Date(2019, 3, 3) at Time(7, 0)
        assertEquals(Year(2019), dateTime.toYear())
    }

    @Test
    fun `OffsetDateTime_toYear() converts to Year`() {
        val offsetDateTime =
            Date(2019, 3, 3) at Time(7, 0) at (-7).hours.asUtcOffset()

        assertEquals(Year(2019), offsetDateTime.toYear())
    }

    @Test
    fun `ZonedDateTime_toYear() converts to Year`() {
        val zonedDateTime = DateTime(2019, 3, 3, 7, 0) at denverZone
        assertEquals(Year(2019), zonedDateTime.toYear())
    }

    @Test
    fun `Date_toYearMonth() converts to YearMonth`() {
        assertEquals(YearMonth(2018, Month.JULY), Date(2018, Month.JULY, 4).toYearMonth())
    }

    @Test
    fun `DateTime_toYearMonth() converts to YearMonth`() {
        val dateTime = Date(2019, 3, 3) at Time(7, 0)
        assertEquals(YearMonth(2019, 3), dateTime.toYearMonth())
    }

    @Test
    fun `OffsetDateTime_toYearMonth() converts to YearMonth`() {
        val offsetDateTime =
            Date(2019, 3, 3) at Time(7, 0) at (-7).hours.asUtcOffset()

        assertEquals(YearMonth(2019, 3), offsetDateTime.toYearMonth())
    }

    @Test
    fun `ZonedDateTime_toYearMonth() converts to YearMonth`() {
        val zonedDateTime = DateTime(2019, 3, 3, 7, 0) at denverZone
        assertEquals(YearMonth(2019, 3), zonedDateTime.toYearMonth())
    }

    @Test
    fun `Instant_toDateTimeAt() converts to DateTime at zone`() {
        val instant = "1980-09-10T14:30Z".toInstant()

        assertEquals(
            Date(1980, 9, 10) at Time(10, 30),
            instant.toDateTimeAt(nyZone)
        )
    }

    @Test
    fun `OffsetDateTime_toOffsetTime() converts to OffsetTime`() {
        val offsetDateTime =
            DateTime(2019, 3, 3, 7, 0) at (-7).hours.asUtcOffset()

        assertEquals(OffsetTime(Time(7, 0), UtcOffset((-7).hours)), offsetDateTime.toOffsetTime())
    }

    @Test
    fun `ZonedDateTime_toOffsetTime() converts to OffsetTime`() {
        val zonedDateTime = DateTime(2019, 3, 3, 7, 0) at denverZone
        assertEquals(OffsetTime(Time(7, 0), UtcOffset((-7).hours)), zonedDateTime.toOffsetTime())
    }

    @Test
    fun `ZonedDateTime_toOffsetDateTime() converts to OffsetDateTime`() {
        assertEquals(
            "1970-01-01T00:00Z".toOffsetDateTime(),
            "1970-01-01T00:00Z".toZonedDateTime().toOffsetDateTime()
        )

        assertEquals(
            "2017-02-28T14:00:00.123456789-07:00".toOffsetDateTime(),
            "2017-02-28T14:00:00.123456789-07:00[America/Denver]".toZonedDateTime().toOffsetDateTime()
        )
    }

    @Test
    fun `OffsetDateTime_toZonedDateTime() converts to ZonedDateTime preserving local time`() {
        val offsetDateTime =
            Date(2019, 3, 3) at Time(1, 0) at UtcOffset((-5).hours)

        assertEquals(
            ZonedDateTime(
                2019,
                3,
                3,
                1,
                0,
                0,
                0,
                denverZone
            ),
            offsetDateTime.toZonedDateTime(denverZone, OffsetConversionStrategy.PRESERVE_LOCAL_TIME)
        )
    }

    @Test
    fun `OffsetDateTime_toZonedDateTime() converts to ZonedDateTime preserving instant`() {
        val offsetDateTime =
            Date(2019, 3, 3) at Time(1, 0) at UtcOffset((-5).hours)

        assertEquals(
            ZonedDateTime(
                2019,
                3,
                2,
                23,
                0,
                0,
                0,
                denverZone
            ),
            offsetDateTime.toZonedDateTime(denverZone, OffsetConversionStrategy.PRESERVE_INSTANT)
        )
    }

    @Test
    fun `OffsetDateTime_toInstant() returns an equivalent Instant`() {
        assertEquals(
            "1970-01-01T00:00Z".toInstant(),
            "1970-01-01T00:00Z".toOffsetDateTime().toInstant()
        )

        assertEquals(
            "2017-02-28T21:00:00.123456789Z".toInstant(),
            "2017-02-28T14:00:00.123456789-07:00".toOffsetDateTime().toInstant()
        )
    }

    @Test
    fun `ZonedDateTime_toInstant() returns an equivalent Instant`() {
        assertEquals(
            "1970-01-01T00:00Z".toInstant(),
            "1970-01-01T00:00Z".toZonedDateTime().toInstant()
        )

        assertEquals(
            "2017-02-28T21:00:00.123456789Z".toInstant(),
            "2017-02-28T14:00:00.123456789-07:00[America/Denver]".toZonedDateTime().toInstant()
        )
    }
}