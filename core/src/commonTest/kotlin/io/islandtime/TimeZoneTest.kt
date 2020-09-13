package io.islandtime

import io.islandtime.format.TimeZoneNameStyle
import io.islandtime.locale.toLocale
import io.islandtime.measures.hours
import io.islandtime.test.AbstractIslandTimeTest
import io.islandtime.zone.TimeZoneRulesException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TimeZoneTest : AbstractIslandTimeTest() {
    @Test
    fun `can be created from an id of 'Z'`() {
        val zone = TimeZone("Z")
        assertEquals(UtcOffset.ZERO, (zone as TimeZone.FixedOffset).offset)
    }

    @Test
    fun `can be created from a fixed offset id`() {
        assertEquals(UtcOffset(18.hours).asTimeZone(), TimeZone("+18:00"))
        assertEquals(UtcOffset.ZERO.asTimeZone(), TimeZone("-00:00"))
        assertEquals(UtcOffset.ZERO.asTimeZone(), TimeZone("+00:00"))
        assertEquals(UtcOffset((-18).hours).asTimeZone(), TimeZone("-18:00"))
    }

    @Test
    fun `throws an exception when created with an improperly formatted offset id`() {
        listOf(
            "+1800",
            "-1800",
            "-18",
            "+18",
            "+1",
            "-1",
            "-",
            "+",
            "- ",
            "+ "
        ).forEach {
            assertFailsWith<DateTimeException> { TimeZone(it) }
            assertFailsWith<DateTimeException> { TimeZone.FixedOffset(it) }
        }
    }

    @Test
    fun `throws an exception when created with an offset that's outside the valid range`() {
        listOf(
            "+18:00:01",
            "-18:00:01",
            "-18:01",
            "+18:01"
        ).forEach {
            assertFailsWith<DateTimeException> { TimeZone(it) }
            assertFailsWith<DateTimeException> { TimeZone.FixedOffset(it) }
        }
    }

    @Test
    fun `throws an exception when region identifier has less than 2 characters`() {
        listOf("U", "G", "").forEach {
            assertFailsWith<DateTimeException> { TimeZone(it) }
        }
    }

    @Test
    fun `can be created from a region id, regardless of validity`() {
        listOf("Etc/UTC", "America/New_York", "America/Boston").forEach {
            assertEquals(TimeZone.Region(it), TimeZone(it))
        }
    }

    @Test
    fun `validated() throws an exception if a region-based time zone has an invalid ID`() {
        assertFailsWith<TimeZoneRulesException> { TimeZone("America/Boston").validated() }
    }

    @Test
    fun `validated() does nothing if the time zone is valid`() {
        listOf(
            "America/New_York",
            "+18:00",
            "Z"
        ).forEach {
            val zone = TimeZone(it)
            assertEquals(zone, zone.validated())
        }
    }

    @Test
    fun `localizedName() and displayName() get localized text from the provider`() {
        assertEquals(
            "Greenwich Mean Time",
            TimeZone("Europe/London").localizedName(TimeZoneNameStyle.LONG_STANDARD, "en-GB".toLocale())
        )
        assertEquals(
            "Greenwich Mean Time",
            TimeZone("Europe/London").displayName(TimeZoneNameStyle.LONG_STANDARD, "en-GB".toLocale())
        )
    }

    @Test
    fun `displayName() returns localized GMT format on a fixed offset zone`() {
        assertEquals(
            "GMT+01:00",
            TimeZone("+01:00").displayName(TimeZoneNameStyle.LONG_STANDARD, "en-GB".toLocale())
        )
        assertEquals(
            "GMT+1",
            TimeZone("+01:00").displayName(TimeZoneNameStyle.SHORT_STANDARD, "en-GB".toLocale())
        )
    }

    @Test
    fun `displayName() returns the ID on an invalid zone`() {
        assertEquals(
            "America/Buffalo",
            TimeZone("America/Buffalo").displayName(TimeZoneNameStyle.LONG_STANDARD, "en-US".toLocale())
        )
    }

    @Test
    fun `normalized() turns a fixed region-based zone into a fixed offset zone`() {
        mapOf(
            "Etc/UTC" to UtcOffset.ZERO,
            "Etc/GMT+6" to UtcOffset((-6).hours),
            "Etc/GMT-12" to UtcOffset(12.hours)
        ).forEach {
            assertEquals(TimeZone.FixedOffset(it.value), TimeZone(it.key).normalized())
        }
    }

    @Test
    fun `normalized() has no effect on a fixed region-based zone`() {
        listOf(
            "+00:00",
            "-05:00",
            "+14:00"
        ).forEach {
            assertEquals(TimeZone.FixedOffset(it), TimeZone.FixedOffset(it).normalized())
        }
    }
}
