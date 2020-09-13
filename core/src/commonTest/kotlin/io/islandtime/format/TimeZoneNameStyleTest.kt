package io.islandtime.format

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TimeZoneNameStyleTest {
    @Test
    fun `isShort returns true only for short styles`() {
        assertTrue { TimeZoneNameStyle.SHORT_STANDARD.isShort }
        assertTrue { TimeZoneNameStyle.SHORT_DAYLIGHT.isShort }
        assertTrue { TimeZoneNameStyle.SHORT_GENERIC.isShort }
        assertFalse { TimeZoneNameStyle.LONG_STANDARD.isShort }
        assertFalse { TimeZoneNameStyle.LONG_DAYLIGHT.isShort }
        assertFalse { TimeZoneNameStyle.LONG_GENERIC.isShort }
    }

    @Test
    fun `isLong returns true only for long styles`() {
        assertFalse { TimeZoneNameStyle.SHORT_STANDARD.isLong }
        assertFalse { TimeZoneNameStyle.SHORT_DAYLIGHT.isLong }
        assertFalse { TimeZoneNameStyle.SHORT_GENERIC.isLong }
        assertTrue { TimeZoneNameStyle.LONG_STANDARD.isLong }
        assertTrue { TimeZoneNameStyle.LONG_DAYLIGHT.isLong }
        assertTrue { TimeZoneNameStyle.LONG_GENERIC.isLong }
    }

    @Test
    fun `isStandard returns true only for standard styles`() {
        assertTrue { TimeZoneNameStyle.LONG_STANDARD.isStandard }
        assertTrue { TimeZoneNameStyle.SHORT_STANDARD.isStandard }
        assertFalse { TimeZoneNameStyle.LONG_DAYLIGHT.isStandard }
        assertFalse { TimeZoneNameStyle.SHORT_DAYLIGHT.isStandard }
        assertFalse { TimeZoneNameStyle.LONG_GENERIC.isStandard }
        assertFalse { TimeZoneNameStyle.SHORT_GENERIC.isStandard }
    }

    @Test
    fun `isDaylight returns true only for daylight saving styles`() {
        assertTrue { TimeZoneNameStyle.LONG_DAYLIGHT.isDaylight }
        assertTrue { TimeZoneNameStyle.SHORT_DAYLIGHT.isDaylight }
        assertFalse { TimeZoneNameStyle.LONG_STANDARD.isDaylight }
        assertFalse { TimeZoneNameStyle.SHORT_STANDARD.isDaylight }
        assertFalse { TimeZoneNameStyle.LONG_GENERIC.isDaylight }
        assertFalse { TimeZoneNameStyle.SHORT_GENERIC.isDaylight }
    }

    @Test
    fun `isGeneric returns true only for generic styles`() {
        assertTrue { TimeZoneNameStyle.LONG_GENERIC.isGeneric }
        assertTrue { TimeZoneNameStyle.SHORT_GENERIC.isGeneric }
        assertFalse { TimeZoneNameStyle.LONG_STANDARD.isGeneric }
        assertFalse { TimeZoneNameStyle.SHORT_STANDARD.isGeneric }
        assertFalse { TimeZoneNameStyle.LONG_DAYLIGHT.isGeneric }
        assertFalse { TimeZoneNameStyle.SHORT_DAYLIGHT.isGeneric }
    }

    @Test
    fun `isSpecific returns true only for specifc styles`() {
        assertFalse { TimeZoneNameStyle.LONG_GENERIC.isSpecific }
        assertFalse { TimeZoneNameStyle.SHORT_GENERIC.isSpecific }
        assertTrue { TimeZoneNameStyle.LONG_STANDARD.isSpecific }
        assertTrue { TimeZoneNameStyle.SHORT_STANDARD.isSpecific }
        assertTrue { TimeZoneNameStyle.LONG_DAYLIGHT.isSpecific }
        assertTrue { TimeZoneNameStyle.SHORT_DAYLIGHT.isSpecific }
    }

    @Test
    fun `toTextStyle() returns a long or short text style`() {
        assertEquals(TextStyle.SHORT, TimeZoneNameStyle.SHORT_STANDARD.toTextStyle())
        assertEquals(TextStyle.SHORT, TimeZoneNameStyle.SHORT_DAYLIGHT.toTextStyle())
        assertEquals(TextStyle.SHORT, TimeZoneNameStyle.SHORT_GENERIC.toTextStyle())
        assertEquals(TextStyle.FULL, TimeZoneNameStyle.LONG_STANDARD.toTextStyle())
        assertEquals(TextStyle.FULL, TimeZoneNameStyle.LONG_DAYLIGHT.toTextStyle())
        assertEquals(TextStyle.FULL, TimeZoneNameStyle.LONG_GENERIC.toTextStyle())
    }
}
