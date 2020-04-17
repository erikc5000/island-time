package io.islandtime.jvm

import io.islandtime.*
import io.islandtime.measures.hours
import io.islandtime.test.AbstractIslandTimeTest
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.chrono.IsoChronology
import java.time.temporal.ChronoField
import java.time.temporal.TemporalQueries
import java.time.temporal.UnsupportedTemporalTypeException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class TemporalAdapterTest : AbstractIslandTimeTest() {
    @Test
    fun `Date can provide expected values`() {
        val date = Date(2019, Month.FEBRUARY, 2)
        val adapter = date.asJavaTemporalAccessor()

        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.INSTANT_SECONDS) }
        assertEquals(17929L, adapter.getLong(ChronoField.EPOCH_DAY))
        assertEquals(2019, adapter.getLong(ChronoField.YEAR))
        assertEquals(2019, adapter.getLong(ChronoField.YEAR_OF_ERA))
        assertEquals(2, adapter.getLong(ChronoField.MONTH_OF_YEAR))
        assertEquals(33, adapter.getLong(ChronoField.DAY_OF_YEAR))
        assertEquals(2, adapter.getLong(ChronoField.DAY_OF_MONTH))
        assertEquals(6, adapter.getLong(ChronoField.DAY_OF_WEEK))
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.AMPM_OF_DAY) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.HOUR_OF_DAY) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.CLOCK_HOUR_OF_DAY) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.HOUR_OF_AMPM) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.CLOCK_HOUR_OF_AMPM) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.MINUTE_OF_HOUR) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.SECOND_OF_MINUTE) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.MILLI_OF_DAY) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.MILLI_OF_SECOND) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.MICRO_OF_DAY) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.MICRO_OF_SECOND) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.NANO_OF_DAY) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.NANO_OF_SECOND) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.PROLEPTIC_MONTH) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.ALIGNED_WEEK_OF_MONTH) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.ALIGNED_WEEK_OF_YEAR) }
        assertNull(adapter.query(TemporalQueries.zoneId()))
        assertNull(adapter.query(TemporalQueries.zone()))
        assertEquals(IsoChronology.INSTANCE, adapter.query(TemporalQueries.chronology()))
        assertNull(adapter.query(TemporalQueries.offset()))
        assertEquals(LocalDate.of(2019, 2, 2), adapter.query(TemporalQueries.localDate()))
        assertNull(adapter.query(TemporalQueries.localTime()))
        assertNull(adapter.query(TemporalQueries.precision()))
    }

    @Test
    fun `Time can provide expected values`() {
        val time = Time(13, 2, 3, 400_000_000)
        val adapter = time.asJavaTemporalAccessor()

        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.INSTANT_SECONDS) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.EPOCH_DAY) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.YEAR) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.YEAR_OF_ERA) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.MONTH_OF_YEAR) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.DAY_OF_YEAR) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.DAY_OF_MONTH) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.DAY_OF_WEEK) }
        assertEquals(1, adapter.getLong(ChronoField.AMPM_OF_DAY))
        assertEquals(13, adapter.getLong(ChronoField.HOUR_OF_DAY))
        assertEquals(13, adapter.getLong(ChronoField.CLOCK_HOUR_OF_DAY))
        assertEquals(1, adapter.getLong(ChronoField.HOUR_OF_AMPM))
        assertEquals(1, adapter.getLong(ChronoField.CLOCK_HOUR_OF_AMPM))
        assertEquals(2, adapter.getLong(ChronoField.MINUTE_OF_HOUR))
        assertEquals(3, adapter.getLong(ChronoField.SECOND_OF_MINUTE))
        assertEquals(46923400L, adapter.getLong(ChronoField.MILLI_OF_DAY))
        assertEquals(400, adapter.getLong(ChronoField.MILLI_OF_SECOND))
        assertEquals(46923400000L, adapter.getLong(ChronoField.MICRO_OF_DAY))
        assertEquals(400_000, adapter.getLong(ChronoField.MICRO_OF_SECOND))
        assertEquals(46923400000000L, adapter.getLong(ChronoField.NANO_OF_DAY))
        assertEquals(400_000_000, adapter.getLong(ChronoField.NANO_OF_SECOND))
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.PROLEPTIC_MONTH) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.ALIGNED_WEEK_OF_MONTH) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.ALIGNED_WEEK_OF_YEAR) }
        assertNull(adapter.query(TemporalQueries.zoneId()))
        assertNull(adapter.query(TemporalQueries.zone()))
        assertNull(adapter.query(TemporalQueries.chronology()))
        assertNull(adapter.query(TemporalQueries.offset()))
        assertNull(adapter.query(TemporalQueries.localDate()))
        assertEquals(
            LocalTime.of(13, 2, 3, 400_000_000),
            adapter.query(TemporalQueries.localTime())
        )
        assertNull(adapter.query(TemporalQueries.precision()))
    }

    @Test
    fun `DateTime can provide expected values`() {
        val dateTime = Date(2019, Month.FEBRUARY, 2) at
            Time(13, 2, 3, 400_000_000)

        val adapter = dateTime.asJavaTemporalAccessor()

        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.INSTANT_SECONDS) }
        assertEquals(17929L, adapter.getLong(ChronoField.EPOCH_DAY))
        assertEquals(2019, adapter.getLong(ChronoField.YEAR))
        assertEquals(2019, adapter.getLong(ChronoField.YEAR_OF_ERA))
        assertEquals(2, adapter.getLong(ChronoField.MONTH_OF_YEAR))
        assertEquals(33, adapter.getLong(ChronoField.DAY_OF_YEAR))
        assertEquals(2, adapter.getLong(ChronoField.DAY_OF_MONTH))
        assertEquals(6, adapter.getLong(ChronoField.DAY_OF_WEEK))
        assertEquals(1, adapter.getLong(ChronoField.AMPM_OF_DAY))
        assertEquals(13, adapter.getLong(ChronoField.HOUR_OF_DAY))
        assertEquals(13, adapter.getLong(ChronoField.CLOCK_HOUR_OF_DAY))
        assertEquals(1, adapter.getLong(ChronoField.HOUR_OF_AMPM))
        assertEquals(1, adapter.getLong(ChronoField.CLOCK_HOUR_OF_AMPM))
        assertEquals(2, adapter.getLong(ChronoField.MINUTE_OF_HOUR))
        assertEquals(3, adapter.getLong(ChronoField.SECOND_OF_MINUTE))
        assertEquals(46923400L, adapter.getLong(ChronoField.MILLI_OF_DAY))
        assertEquals(400, adapter.getLong(ChronoField.MILLI_OF_SECOND))
        assertEquals(46923400000L, adapter.getLong(ChronoField.MICRO_OF_DAY))
        assertEquals(400_000, adapter.getLong(ChronoField.MICRO_OF_SECOND))
        assertEquals(46923400000000L, adapter.getLong(ChronoField.NANO_OF_DAY))
        assertEquals(400_000_000, adapter.getLong(ChronoField.NANO_OF_SECOND))
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.PROLEPTIC_MONTH) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.ALIGNED_WEEK_OF_MONTH) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.ALIGNED_WEEK_OF_YEAR) }
        assertNull(adapter.query(TemporalQueries.zoneId()))
        assertNull(adapter.query(TemporalQueries.zone()))
        assertEquals(IsoChronology.INSTANCE, adapter.query(TemporalQueries.chronology()))
        assertNull(adapter.query(TemporalQueries.offset()))
        assertEquals(LocalDate.of(2019, 2, 2), adapter.query(TemporalQueries.localDate()))
        assertEquals(
            LocalTime.of(13, 2, 3, 400_000_000),
            adapter.query(TemporalQueries.localTime())
        )
        assertNull(adapter.query(TemporalQueries.precision()))
    }

    @Test
    fun `OffsetDateTime can provide expected values`() {
        val offsetDateTime = Date(2019, Month.FEBRUARY, 2) at
            Time(13, 2, 3, 400_000_000) at
            UtcOffset((-5).hours)

        val adapter = offsetDateTime.asJavaTemporalAccessor()

        assertEquals(1549130523L, adapter.getLong(ChronoField.INSTANT_SECONDS))
        assertEquals(17929L, adapter.getLong(ChronoField.EPOCH_DAY))
        assertEquals(2019, adapter.getLong(ChronoField.YEAR))
        assertEquals(2019, adapter.getLong(ChronoField.YEAR_OF_ERA))
        assertEquals(2, adapter.getLong(ChronoField.MONTH_OF_YEAR))
        assertEquals(33, adapter.getLong(ChronoField.DAY_OF_YEAR))
        assertEquals(2, adapter.getLong(ChronoField.DAY_OF_MONTH))
        assertEquals(6, adapter.getLong(ChronoField.DAY_OF_WEEK))
        assertEquals(1, adapter.getLong(ChronoField.AMPM_OF_DAY))
        assertEquals(13, adapter.getLong(ChronoField.HOUR_OF_DAY))
        assertEquals(13, adapter.getLong(ChronoField.CLOCK_HOUR_OF_DAY))
        assertEquals(1, adapter.getLong(ChronoField.HOUR_OF_AMPM))
        assertEquals(1, adapter.getLong(ChronoField.CLOCK_HOUR_OF_AMPM))
        assertEquals(2, adapter.getLong(ChronoField.MINUTE_OF_HOUR))
        assertEquals(3, adapter.getLong(ChronoField.SECOND_OF_MINUTE))
        assertEquals(46923400L, adapter.getLong(ChronoField.MILLI_OF_DAY))
        assertEquals(400, adapter.getLong(ChronoField.MILLI_OF_SECOND))
        assertEquals(46923400000L, adapter.getLong(ChronoField.MICRO_OF_DAY))
        assertEquals(400_000, adapter.getLong(ChronoField.MICRO_OF_SECOND))
        assertEquals(46923400000000L, adapter.getLong(ChronoField.NANO_OF_DAY))
        assertEquals(400_000_000, adapter.getLong(ChronoField.NANO_OF_SECOND))
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.PROLEPTIC_MONTH) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.ALIGNED_WEEK_OF_MONTH) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.ALIGNED_WEEK_OF_YEAR) }
        assertNull(adapter.query(TemporalQueries.zoneId()))
        assertEquals(ZoneOffset.ofHours(-5), adapter.query(TemporalQueries.zone()))
        assertEquals(IsoChronology.INSTANCE, adapter.query(TemporalQueries.chronology()))
        assertEquals(ZoneOffset.ofHours(-5), adapter.query(TemporalQueries.offset()))
        assertEquals(LocalDate.of(2019, 2, 2), adapter.query(TemporalQueries.localDate()))
        assertEquals(
            LocalTime.of(13, 2, 3, 400_000_000),
            adapter.query(TemporalQueries.localTime())
        )
        assertNull(adapter.query(TemporalQueries.precision()))
    }

    @Test
    fun `ZonedDateTime can provide expected values`() {
        val zonedDateTime = Date(2019, Month.FEBRUARY, 2) at
            Time(13, 2, 3, 400_000_000) at
            TimeZone("America/New_York")

        val adapter = zonedDateTime.asJavaTemporalAccessor()

        assertEquals(1549130523L, adapter.getLong(ChronoField.INSTANT_SECONDS))
        assertEquals(17929L, adapter.getLong(ChronoField.EPOCH_DAY))
        assertEquals(2019, adapter.getLong(ChronoField.YEAR))
        assertEquals(2019, adapter.getLong(ChronoField.YEAR_OF_ERA))
        assertEquals(2, adapter.getLong(ChronoField.MONTH_OF_YEAR))
        assertEquals(33, adapter.getLong(ChronoField.DAY_OF_YEAR))
        assertEquals(2, adapter.getLong(ChronoField.DAY_OF_MONTH))
        assertEquals(6, adapter.getLong(ChronoField.DAY_OF_WEEK))
        assertEquals(1, adapter.getLong(ChronoField.AMPM_OF_DAY))
        assertEquals(13, adapter.getLong(ChronoField.HOUR_OF_DAY))
        assertEquals(13, adapter.getLong(ChronoField.CLOCK_HOUR_OF_DAY))
        assertEquals(1, adapter.getLong(ChronoField.HOUR_OF_AMPM))
        assertEquals(1, adapter.getLong(ChronoField.CLOCK_HOUR_OF_AMPM))
        assertEquals(2, adapter.getLong(ChronoField.MINUTE_OF_HOUR))
        assertEquals(3, adapter.getLong(ChronoField.SECOND_OF_MINUTE))
        assertEquals(46923400L, adapter.getLong(ChronoField.MILLI_OF_DAY))
        assertEquals(400, adapter.getLong(ChronoField.MILLI_OF_SECOND))
        assertEquals(46923400000L, adapter.getLong(ChronoField.MICRO_OF_DAY))
        assertEquals(400_000, adapter.getLong(ChronoField.MICRO_OF_SECOND))
        assertEquals(46923400000000L, adapter.getLong(ChronoField.NANO_OF_DAY))
        assertEquals(400_000_000, adapter.getLong(ChronoField.NANO_OF_SECOND))
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.PROLEPTIC_MONTH) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.ALIGNED_WEEK_OF_MONTH) }
        assertFailsWith<UnsupportedTemporalTypeException> { adapter.getLong(ChronoField.ALIGNED_WEEK_OF_YEAR) }
        assertEquals(ZoneId.of("America/New_York"), adapter.query(TemporalQueries.zoneId()))
        assertEquals(ZoneId.of("America/New_York"), adapter.query(TemporalQueries.zone()))
        assertEquals(IsoChronology.INSTANCE, adapter.query(TemporalQueries.chronology()))
        assertEquals(ZoneOffset.ofHours(-5), adapter.query(TemporalQueries.offset()))
        assertEquals(LocalDate.of(2019, 2, 2), adapter.query(TemporalQueries.localDate()))
        assertEquals(
            LocalTime.of(13, 2, 3, 400_000_000),
            adapter.query(TemporalQueries.localTime())
        )
        assertNull(adapter.query(TemporalQueries.precision()))
    }

    @Test
    fun `LocalDate_from()`() {
        val date = Date(2019, Month.OCTOBER, 2)

        listOf(
            date,
            date at Time.NOON,
            date at Time.NOON at UtcOffset((-5).hours),
            date at Time.NOON at TimeZone("America/New_York")
        ).forEach {
            assertEquals(
                LocalDate.of(2019, java.time.Month.OCTOBER, 2),
                LocalDate.from(it.asJavaTemporalAccessor())
            )
        }
    }

    @Test
    fun `LocalTime_from()`() {
        val time = Time(13, 2, 3, 4)

        listOf(
            time,
            time at UtcOffset((-5).hours),
            Date(2019, Month.OCTOBER, 2) at time,
            Date(2019, Month.OCTOBER, 2) at time at UtcOffset((-5).hours),
            Date(2019, Month.OCTOBER, 2) at time at TimeZone("America/New_York")
        ).forEach {
            assertEquals(
                LocalTime.of(13, 2, 3, 4),
                LocalTime.from(it.asJavaTemporalAccessor())
            )
        }
    }
}