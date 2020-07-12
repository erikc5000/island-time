package io.islandtime.ranges

import io.islandtime.*
import io.islandtime.OffsetConversionStrategy.PRESERVE_INSTANT
import io.islandtime.OffsetConversionStrategy.PRESERVE_LOCAL_TIME
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ConversionsTest : AbstractIslandTimeTest() {
    private val nyZone = TimeZone("America/New_York")

    @Test
    fun `convert empty DateTimeInterval to DateRange`() {
        assertEquals(DateRange.EMPTY, DateTimeInterval.EMPTY.toDateRange())
    }

    @Test
    fun `convert unbounded DateTimeInterval to DateRange`() {
        assertEquals(DateRange.UNBOUNDED, DateTimeInterval.UNBOUNDED.toDateRange())
    }

    @Test
    fun `convert half-bounded DateTimeInterval to DateRange`() {
        val dateTimeInterval1 = "1968-10-05T05:00/..".toDateTimeInterval()

        assertEquals(
            "1968-10-05".toDate()..Date.MAX,
            dateTimeInterval1.toDateRange()
        )

        val dateTimeInterval2 = "../2000-01-03T10:00".toDateTimeInterval()

        assertEquals(
            Date.MIN.."2000-01-03".toDate(),
            dateTimeInterval2.toDateRange()
        )
    }

    @Test
    fun `convert bounded DateTimeInterval to DateRange`() {
        val dateTimeInterval = "1968-10-05T05:00/2000-01-03T10:00".toDateTimeInterval()
        assertEquals("1968-10-05".toDate().."2000-01-03".toDate(), dateTimeInterval.toDateRange())
    }

    @Test
    fun `convert empty OffsetDateTimeInterval to DateRange`() {
        assertEquals(DateRange.EMPTY, OffsetDateTimeInterval.EMPTY.toDateRange())
    }

    @Test
    fun `convert unbounded OffsetDateTimeInterval to DateRange`() {
        assertEquals(DateRange.UNBOUNDED, OffsetDateTimeInterval.UNBOUNDED.toDateRange())
    }

    @Test
    fun `convert half-bounded OffsetDateTimeInterval to DateRange`() {
        val offsetDateTimeInterval1 = "1968-10-05T05:00-04:00/..".toOffsetDateTimeInterval()
        assertEquals("1968-10-05".toDate()..Date.MAX, offsetDateTimeInterval1.toDateRange())

        val offsetDateTimeInterval2 = "../2000-01-03T10:00-05:00".toOffsetDateTimeInterval()
        assertEquals(Date.MIN.."2000-01-03".toDate(), offsetDateTimeInterval2.toDateRange())
    }

    @Test
    fun `convert bounded OffsetDateTimeInterval to DateRange`() {
        val offsetDateTimeInterval = "1968-10-05T05:00-04:00/2000-01-03T10:00-05:00".toOffsetDateTimeInterval()

        assertEquals(
            "1968-10-05".toDate().."2000-01-03".toDate(),
            offsetDateTimeInterval.toDateRange()
        )
    }

    @Test
    fun `convert empty ZonedDateTimeInterval to DateRange`() {
        assertEquals(DateRange.EMPTY, ZonedDateTimeInterval.EMPTY.toDateRange())
    }

    @Test
    fun `convert unbounded ZonedDateTimeInterval to DateRange`() {
        assertEquals(DateRange.UNBOUNDED, ZonedDateTimeInterval.UNBOUNDED.toDateRange())
    }

    @Test
    fun `convert half-bounded ZonedDateTimeInterval to DateRange`() {
        val zonedDateTimeInterval1 = "1968-10-05T05:00-04:00[America/New_York]/..".toZonedDateTimeInterval()

        assertEquals(
            "1968-10-05".toDate()..Date.MAX,
            zonedDateTimeInterval1.toDateRange()
        )

        val zonedDateTimeInterval2 = "../2000-01-03T10:00-05:00[America/New_York]".toZonedDateTimeInterval()

        assertEquals(
            Date.MIN.."2000-01-03".toDate(),
            zonedDateTimeInterval2.toDateRange()
        )
    }

    @Test
    fun `convert bounded ZonedDateTimeInterval to DateRange`() {
        val zonedDateTimeInterval = "1968-10-05T05:00-04:00[America/New_York]/2000-01-03T10:00-05:00[America/New_York]"
            .toZonedDateTimeInterval()

        assertEquals(
            "1968-10-05".toDate().."2000-01-03".toDate(),
            zonedDateTimeInterval.toDateRange()
        )
    }

    @Test
    fun `convert empty InstantInterval to DateRange`() {
        assertEquals(DateRange.EMPTY, InstantInterval.EMPTY.toDateRangeAt(nyZone))
    }

    @Test
    fun `convert unbounded InstantInterval to DateRange`() {
        assertEquals(DateRange.UNBOUNDED, InstantInterval.UNBOUNDED.toDateRangeAt(nyZone))
    }

    @Test
    fun `convert half-bounded InstantInterval to DateRange`() {
        val instantInterval1 = "1968-10-05T04:00Z/..".toInstantInterval()
        assertEquals("1968-10-05".toDate()..Date.MAX, instantInterval1.toDateRangeAt(nyZone))

        val instantInterval2 = "../2000-01-04T04:59:59.999999999Z".toInstantInterval()
        assertEquals(Date.MIN.."2000-01-03".toDate(), instantInterval2.toDateRangeAt(nyZone))
    }

    @Test
    fun `convert bounded InstantInterval to DateRange`() {
        val instantInterval = "1968-10-05T04:00Z/2000-01-04T04:59:59.999999999Z".toInstantInterval()

        assertEquals(
            "1968-10-05".toDate().."2000-01-03".toDate(),
            instantInterval.toDateRangeAt(nyZone)
        )
    }

    @Test
    fun `convert empty OffsetDateTimeInterval to DateTimeInterval`() {
        assertEquals(DateTimeInterval.EMPTY, OffsetDateTimeInterval.EMPTY.toDateTimeInterval())
    }

    @Test
    fun `convert unbounded OffsetDateTimeInterval to DateTimeInterval`() {
        assertEquals(DateTimeInterval.UNBOUNDED, OffsetDateTimeInterval.UNBOUNDED.toDateTimeInterval())
    }

    @Test
    fun `convert half-bounded OffsetDateTimeInterval to DateTimeInterval`() {
        val offsetDateTimeInterval1 = "1968-10-05T05:00-04:00/..".toOffsetDateTimeInterval()

        assertEquals(
            "1968-10-05T05:00".toDateTime() until DateTime.MAX,
            offsetDateTimeInterval1.toDateTimeInterval()
        )

        val offsetDateTimeInterval2 = "../2000-01-03T10:00-05:00".toOffsetDateTimeInterval()

        assertEquals(
            DateTime.MIN until "2000-01-03T10:00".toDateTime(),
            offsetDateTimeInterval2.toDateTimeInterval()
        )
    }

    @Test
    fun `convert bounded OffsetDateTimeInterval to DateTimeInterval`() {
        val offsetDateTimeInterval = "1968-10-05T05:00-04:00/2000-01-03T10:00-05:00"
            .toOffsetDateTimeInterval()

        assertEquals(
            "1968-10-05T05:00".toDateTime() until "2000-01-03T10:00".toDateTime(),
            offsetDateTimeInterval.toDateTimeInterval()
        )
    }

    @Test
    fun `convert empty ZonedDateTimeInterval to DateTimeInterval`() {
        assertEquals(DateTimeInterval.EMPTY, ZonedDateTimeInterval.EMPTY.toDateTimeInterval())
    }

    @Test
    fun `convert unbounded ZonedDateTimeInterval to DateTimeInterval`() {
        assertEquals(DateTimeInterval.UNBOUNDED, ZonedDateTimeInterval.UNBOUNDED.toDateTimeInterval())
    }

    @Test
    fun `convert half-bounded ZonedDateTimeInterval to DateTimeInterval`() {
        val zonedDateTimeInterval1 = "1968-10-05T05:00-04:00[America/New_York]/..".toZonedDateTimeInterval()

        assertEquals(
            "1968-10-05T05:00".toDateTime() until DateTime.MAX,
            zonedDateTimeInterval1.toDateTimeInterval()
        )

        val zonedDateTimeInterval2 = "../2000-01-03T10:00-05:00[America/New_York]".toZonedDateTimeInterval()

        assertEquals(
            DateTime.MIN until "2000-01-03T10:00".toDateTime(),
            zonedDateTimeInterval2.toDateTimeInterval()
        )
    }

    @Test
    fun `convert bounded ZonedDateTimeInterval to DateTimeInterval`() {
        val zonedDateTimeInterval = "1968-10-05T05:00-04:00[America/New_York]/2000-01-03T10:00-05:00[America/New_York]"
            .toZonedDateTimeInterval()

        assertEquals(
            "1968-10-05T05:00".toDateTime() until "2000-01-03T10:00".toDateTime(),
            zonedDateTimeInterval.toDateTimeInterval()
        )
    }

    @Test
    fun `convert empty InstantInterval to DateTimeInterval`() {
        assertEquals(DateTimeInterval.EMPTY, InstantInterval.EMPTY.toDateTimeIntervalAt(nyZone))
    }

    @Test
    fun `convert unbounded InstantInterval to DateTimeInterval`() {
        assertEquals(DateTimeInterval.UNBOUNDED, InstantInterval.UNBOUNDED.toDateTimeIntervalAt(nyZone))
    }

    @Test
    fun `convert half-bounded InstantInterval to DateTimeInterval`() {
        val instantInterval1 = "1968-10-05T04:00Z/..".toInstantInterval()

        assertEquals(
            "1968-10-05T00:00".toDateTime() until DateTime.MAX,
            instantInterval1.toDateTimeIntervalAt(nyZone)
        )

        val instantInterval2 = "../2000-01-04T04:59:59.999999999Z".toInstantInterval()

        assertEquals(
            DateTime.MIN until "2000-01-03T23:59:59.999999999".toDateTime(),
            instantInterval2.toDateTimeIntervalAt(nyZone)
        )
    }

    @Test
    fun `convert bounded InstantInterval to DateTimeInterval`() {
        val instantInterval = "1968-10-05T04:00Z/2000-01-04T04:59:59.999999999Z".toInstantInterval()

        assertEquals(
            "1968-10-05T00:00".toDateTime() until "2000-01-03T23:59:59.999999999".toDateTime(),
            instantInterval.toDateTimeIntervalAt(nyZone)
        )
    }

    @Test
    fun `convert empty ZonedDateTimeInterval to OffsetDateTimeInterval`() {
        assertEquals(OffsetDateTimeInterval.EMPTY, ZonedDateTimeInterval.EMPTY.toOffsetDateTimeInterval())
    }

    @Test
    fun `convert unbounded ZonedDateTimeInterval to OffsetDateTimeInterval`() {
        assertEquals(OffsetDateTimeInterval.UNBOUNDED, ZonedDateTimeInterval.UNBOUNDED.toOffsetDateTimeInterval())
    }

    @Test
    fun `convert half-bounded ZonedDateTimeInterval to OffsetDateTimeInterval`() {
        val zonedDateTimeInterval1 = "1968-10-05T05:00-04:00[America/New_York]/..".toZonedDateTimeInterval()

        assertEquals(
            "1968-10-05T05:00-04:00".toOffsetDateTime() until OffsetDateTime.MAX,
            zonedDateTimeInterval1.toOffsetDateTimeInterval()
        )

        val zonedDateTimeInterval2 = "../2000-01-03T10:00-05:00[America/New_York]".toZonedDateTimeInterval()

        assertEquals(
            OffsetDateTime.MIN until "2000-01-03T10:00-05:00".toOffsetDateTime(),
            zonedDateTimeInterval2.toOffsetDateTimeInterval()
        )
    }

    @Test
    fun `convert bounded ZonedDateTimeInterval to OffsetDateTimeInterval`() {
        val zonedDateTimeInterval = "1968-10-05T05:00-04:00[America/New_York]/2000-01-03T10:00-05:00[America/New_York]"
            .toZonedDateTimeInterval()

        assertEquals(
            "1968-10-05T05:00-04:00".toOffsetDateTime() until "2000-01-03T10:00-05:00".toOffsetDateTime(),
            zonedDateTimeInterval.toOffsetDateTimeInterval()
        )
    }

    @Test
    fun `convert empty OffsetDateTimeInterval to ZonedDateTimeInterval with fixed-offset zone`() {
        assertEquals(ZonedDateTimeInterval.EMPTY, OffsetDateTimeInterval.EMPTY.asZonedDateTimeInterval())
    }

    @Test
    fun `convert unbounded OffsetDateTimeInterval to ZonedDateTimeInterval with fixed-offset zone`() {
        assertEquals(ZonedDateTimeInterval.UNBOUNDED, OffsetDateTimeInterval.UNBOUNDED.asZonedDateTimeInterval())
    }

    @Test
    fun `convert half-bounded OffsetDateTimeInterval to ZonedDateTimeInterval with fixed-offset zone`() {
        assertEquals(
            "../1991-06-23T14:00-04:00".toZonedDateTimeInterval(),
            "../1991-06-23T14:00-04:00".toOffsetDateTimeInterval().asZonedDateTimeInterval()
        )
        assertEquals(
            "1991-02-15T12:00-05:00/..".toZonedDateTimeInterval(),
            "1991-02-15T12:00-05:00/..".toOffsetDateTimeInterval().asZonedDateTimeInterval()
        )
    }

    @Test
    fun `convert bounded OffsetDateTimeInterval to ZonedDateTimeInterval with fixed-offset zone`() {
        assertEquals(
            "1991-02-15T12:00-05:00/1991-06-23T14:00-04:00".toZonedDateTimeInterval(),
            "1991-02-15T12:00-05:00/1991-06-23T14:00-04:00".toOffsetDateTimeInterval().asZonedDateTimeInterval()
        )
    }

    @Test
    fun `convert empty OffsetDateTimeInterval to ZonedDateTimeInterval preserving instant`() {
        assertEquals(
            ZonedDateTimeInterval.EMPTY,
            OffsetDateTimeInterval.EMPTY.toZonedDateTimeInterval(nyZone, PRESERVE_INSTANT)
        )
    }

    @Test
    fun `convert unbounded OffsetDateTimeInterval to ZonedDateTimeInterval preserving instant`() {
        assertEquals(
            ZonedDateTimeInterval.UNBOUNDED,
            OffsetDateTimeInterval.UNBOUNDED.toZonedDateTimeInterval(nyZone, PRESERVE_INSTANT)
        )
    }

    @Test
    fun `convert half-bounded OffsetDateTimeInterval to ZonedDateTimeInterval preserving instant`() {
        assertEquals(
            "../1991-06-23T15:00-04:00[America/New_York]".toZonedDateTimeInterval(),
            "../1991-06-23T14:00-05:00".toOffsetDateTimeInterval().toZonedDateTimeInterval(nyZone, PRESERVE_INSTANT)
        )
        assertEquals(
            "1991-02-15T11:00-05:00[America/New_York]/..".toZonedDateTimeInterval(),
            "1991-02-15T12:00-04:00/..".toOffsetDateTimeInterval().toZonedDateTimeInterval(nyZone, PRESERVE_INSTANT)
        )
    }

    @Test
    fun `convert bounded OffsetDateTimeInterval to ZonedDateTimeInterval preserving instant`() {
        assertEquals(
            "1991-02-15T11:00-05:00[America/New_York]/1991-06-23T15:00-04:00[America/New_York]"
                .toZonedDateTimeInterval(),
            "1991-02-15T12:00-04:00/1991-06-23T14:00-05:00"
                .toOffsetDateTimeInterval()
                .toZonedDateTimeInterval(nyZone, PRESERVE_INSTANT)
        )
    }

    @Test
    fun `convert empty OffsetDateTimeInterval to ZonedDateTimeInterval preserving local time`() {
        assertEquals(
            ZonedDateTimeInterval.EMPTY,
            OffsetDateTimeInterval.EMPTY.toZonedDateTimeInterval(nyZone, PRESERVE_LOCAL_TIME)
        )
    }

    @Test
    fun `convert unbounded OffsetDateTimeInterval to ZonedDateTimeInterval preserving local time`() {
        assertEquals(
            ZonedDateTimeInterval.UNBOUNDED,
            OffsetDateTimeInterval.UNBOUNDED.toZonedDateTimeInterval(nyZone, PRESERVE_LOCAL_TIME)
        )
    }

    @Test
    fun `convert half-bounded OffsetDateTimeInterval to ZonedDateTimeInterval preserving local time`() {
        assertEquals(
            "../1991-06-23T14:00-04:00[America/New_York]".toZonedDateTimeInterval(),
            "../1991-06-23T14:00-05:00".toOffsetDateTimeInterval().toZonedDateTimeInterval(nyZone, PRESERVE_LOCAL_TIME)
        )
        assertEquals(
            "1991-02-15T12:00-05:00[America/New_York]/..".toZonedDateTimeInterval(),
            "1991-02-15T12:00-04:00/..".toOffsetDateTimeInterval().toZonedDateTimeInterval(nyZone, PRESERVE_LOCAL_TIME)
        )
    }

    @Test
    fun `convert bounded OffsetDateTimeInterval to ZonedDateTimeInterval preserving local time`() {
        assertEquals(
            "1991-02-15T12:00-05:00[America/New_York]/1991-06-23T14:00-04:00[America/New_York]"
                .toZonedDateTimeInterval(),
            "1991-02-15T12:00-04:00/1991-06-23T14:00-05:00"
                .toOffsetDateTimeInterval()
                .toZonedDateTimeInterval(nyZone, PRESERVE_LOCAL_TIME)
        )
    }

    @Test
    fun `convert empty DateRange to InstantInterval`() {
        assertEquals(InstantInterval.EMPTY, DateRange.EMPTY.toInstantIntervalAt(TimeZone.UTC))
    }

    @Test
    fun `convert unbounded DateRange to InstantInterval`() {
        assertEquals(
            InstantInterval.UNBOUNDED,
            DateRange.UNBOUNDED.toInstantIntervalAt(TimeZone.UTC)
        )
    }

    @Test
    fun `convert half-bounded DateRange to InstantInterval`() {
        val dateRange1 = Date(1968, 10, 5)..Date.MAX

        assertEquals(
            "1968-10-05T04:00Z".toInstant() until Instant.MAX,
            dateRange1.toInstantIntervalAt(nyZone)
        )

        val dateRange2 = Date.MIN..Date(2000, 1, 3)

        assertEquals(
            Instant.MIN until "2000-01-04T05:00Z".toInstant(),
            dateRange2.toInstantIntervalAt(nyZone)
        )
    }

    @Test
    fun `convert bounded DateRange to InstantInterval`() {
        val dateRange = Date(1968, 10, 5)..Date(2000, 1, 3)

        assertEquals(
            "1968-10-05T04:00Z".toInstant() until "2000-01-04T05:00Z".toInstant(),
            dateRange.toInstantIntervalAt(nyZone)
        )
    }

    @Test
    fun `convert empty DateTimeInterval to InstantInterval`() {
        assertEquals(InstantInterval.EMPTY, DateTimeInterval.EMPTY.toInstantIntervalAt(TimeZone.UTC))
    }

    @Test
    fun `convert unbounded DateTimeInterval to InstantInterval`() {
        assertEquals(
            InstantInterval.UNBOUNDED,
            DateTimeInterval.UNBOUNDED.toInstantIntervalAt(TimeZone.UTC)
        )
    }

    @Test
    fun `convert half-bounded DateTimeInterval to InstantInterval`() {
        val dateTimeInterval1 = DateTime(1968, 10, 5, 10, 0) until DateTime.MAX

        assertEquals(
            "1968-10-05T14:00Z".toInstant() until Instant.MAX,
            dateTimeInterval1.toInstantIntervalAt(nyZone)
        )

        val dateTimeInterval2 = DateTime.MIN until DateTime(2000, 1, 3, 10, 0)

        assertEquals(
            Instant.MIN until "2000-01-03T15:00Z".toInstant(),
            dateTimeInterval2.toInstantIntervalAt(nyZone)
        )
    }

    @Test
    fun `convert bounded DateTimeInterval to InstantInterval`() {
        val dateTimeInterval = DateTime(1968, 10, 5, 10, 0) until
            DateTime(2000, 1, 3, 10, 0)

        assertEquals(
            "1968-10-05T14:00Z".toInstant() until "2000-01-03T15:00Z".toInstant(),
            dateTimeInterval.toInstantIntervalAt(nyZone)
        )
    }

    @Test
    fun `convert empty OffsetDateTimeInterval to InstantInterval`() {
        assertEquals(InstantInterval.EMPTY, OffsetDateTimeInterval.EMPTY.toInstantInterval())
    }

    @Test
    fun `convert unbounded OffsetDateTimeInterval to InstantInterval`() {
        assertEquals(
            InstantInterval.UNBOUNDED,
            OffsetDateTimeInterval.UNBOUNDED.toInstantInterval()
        )
    }

    @Test
    fun `convert half-bounded OffsetDateTimeInterval to InstantInterval`() {
        val offsetDateTimeInterval1 = "1968-10-05T05:00-05:00".toOffsetDateTime() until OffsetDateTime.MAX

        assertEquals(
            "1968-10-05T10:00Z".toInstant() until Instant.MAX,
            offsetDateTimeInterval1.toInstantInterval()
        )

        val offsetDateTimeInterval2 = OffsetDateTime.MIN until "2000-01-03T10:00-05:00".toOffsetDateTime()

        assertEquals(
            Instant.MIN until "2000-01-03T15:00Z".toInstant(),
            offsetDateTimeInterval2.toInstantInterval()
        )
    }

    @Test
    fun `convert bounded OffsetDateTimeInterval to InstantInterval`() {
        val offsetDateTimeInterval =
            "1968-10-05T05:00-05:00".toOffsetDateTime() until "2000-01-03T10:00-05:00".toOffsetDateTime()

        assertEquals(
            "1968-10-05T10:00Z".toInstant() until "2000-01-03T15:00Z".toInstant(),
            offsetDateTimeInterval.toInstantInterval()
        )
    }

    @Test
    fun `convert empty ZonedDateTimeInterval to InstantInterval`() {
        assertEquals(InstantInterval.EMPTY, ZonedDateTimeInterval.EMPTY.toInstantInterval())
    }

    @Test
    fun `convert unbounded ZonedDateTimeInterval to InstantInterval`() {
        assertEquals(InstantInterval.UNBOUNDED, ZonedDateTimeInterval.UNBOUNDED.toInstantInterval())
    }

    @Test
    fun `convert half-bounded ZonedDateTimeInterval to InstantInterval`() {
        val zonedDateTimeInterval1 =
            "1968-10-05T05:00-05:00".toZonedDateTime() until ZonedDateTimeInterval.UNBOUNDED.endExclusive

        assertEquals(
            "1968-10-05T10:00Z".toInstant() until Instant.MAX,
            zonedDateTimeInterval1.toInstantInterval()
        )

        val zonedDateTimeInterval2 =
            ZonedDateTimeInterval.UNBOUNDED.start until "2000-01-03T10:00-05:00".toZonedDateTime()

        assertEquals(
            Instant.MIN until "2000-01-03T15:00Z".toInstant(),
            zonedDateTimeInterval2.toInstantInterval()
        )
    }

    @Test
    fun `convert bounded ZonedDateTimeInterval to InstantInterval`() {
        val zonedDateTimeInterval =
            "1968-10-05T05:00-05:00".toZonedDateTime() until "2000-01-03T10:00-05:00".toZonedDateTime()

        assertEquals(
            "1968-10-05T10:00Z".toInstant() until "2000-01-03T15:00Z".toInstant(),
            zonedDateTimeInterval.toInstantInterval()
        )
    }
}