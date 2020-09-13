package io.islandtime.format

import kotlin.test.*

class ContextualContextualTimeZoneNameStyleTest {
    @Test
    fun `isShort returns true only for short styles`() {
        assertTrue { ContextualTimeZoneNameStyle.SHORT_SPECIFIC.isShort }
        assertTrue { ContextualTimeZoneNameStyle.SHORT_GENERIC.isShort }
        assertFalse { ContextualTimeZoneNameStyle.LONG_SPECIFIC.isShort }
        assertFalse { ContextualTimeZoneNameStyle.LONG_GENERIC.isShort }
    }

    @Test
    fun `isLong returns true only for long styles`() {
        assertFalse { ContextualTimeZoneNameStyle.SHORT_SPECIFIC.isLong }
        assertFalse { ContextualTimeZoneNameStyle.SHORT_GENERIC.isLong }
        assertTrue { ContextualTimeZoneNameStyle.LONG_SPECIFIC.isLong }
        assertTrue { ContextualTimeZoneNameStyle.LONG_GENERIC.isLong }
    }

    @Test
    fun `isGeneric returns true only for generic styles`() {
        assertTrue { ContextualTimeZoneNameStyle.LONG_GENERIC.isGeneric }
        assertTrue { ContextualTimeZoneNameStyle.SHORT_GENERIC.isGeneric }
        assertFalse { ContextualTimeZoneNameStyle.LONG_SPECIFIC.isGeneric }
        assertFalse { ContextualTimeZoneNameStyle.SHORT_SPECIFIC.isGeneric }
    }

    @Test
    fun `isSpecific returns true only for specifc styles`() {
        assertFalse { ContextualTimeZoneNameStyle.LONG_GENERIC.isSpecific }
        assertFalse { ContextualTimeZoneNameStyle.SHORT_GENERIC.isSpecific }
        assertTrue { ContextualTimeZoneNameStyle.LONG_SPECIFIC.isSpecific }
        assertTrue { ContextualTimeZoneNameStyle.SHORT_SPECIFIC.isSpecific }
    }

    @Test
    fun `toTextStyle() returns a long or short text style`() {
        assertEquals(TextStyle.SHORT, ContextualTimeZoneNameStyle.SHORT_SPECIFIC.toTextStyle())
        assertEquals(TextStyle.SHORT, ContextualTimeZoneNameStyle.SHORT_GENERIC.toTextStyle())
        assertEquals(TextStyle.FULL, ContextualTimeZoneNameStyle.LONG_SPECIFIC.toTextStyle())
        assertEquals(TextStyle.FULL, ContextualTimeZoneNameStyle.LONG_GENERIC.toTextStyle())
    }

    @Test
    fun `toTimeZoneNameStyle() returns a similar TimeZoneNameStyle for generic styles`() {
        assertEquals(TimeZoneNameStyle.SHORT_GENERIC, ContextualTimeZoneNameStyle.SHORT_GENERIC.toTimeZoneNameStyle())
        assertEquals(TimeZoneNameStyle.LONG_GENERIC, ContextualTimeZoneNameStyle.LONG_GENERIC.toTimeZoneNameStyle())
    }

    @Test
    fun `toTimeZoneNameStyle() throws an exception for specifc styles`() {
        assertFailsWith<UnsupportedOperationException> {
            ContextualTimeZoneNameStyle.SHORT_SPECIFIC.toTimeZoneNameStyle()
        }
        assertFailsWith<UnsupportedOperationException> {
            ContextualTimeZoneNameStyle.LONG_SPECIFIC.toTimeZoneNameStyle()
        }
    }

    @Test
    fun `toTimeZoneNameStyle(daylight) returns a simular TimeZoneNameStyle style`() {
        assertEquals(
            TimeZoneNameStyle.SHORT_GENERIC,
            ContextualTimeZoneNameStyle.SHORT_GENERIC.toTimeZoneNameStyle(daylight = true)
        )
        assertEquals(
            TimeZoneNameStyle.LONG_GENERIC,
            ContextualTimeZoneNameStyle.LONG_GENERIC.toTimeZoneNameStyle(daylight = true)
        )
        assertEquals(
            TimeZoneNameStyle.SHORT_DAYLIGHT,
            ContextualTimeZoneNameStyle.SHORT_SPECIFIC.toTimeZoneNameStyle(daylight = true)
        )
        assertEquals(
            TimeZoneNameStyle.LONG_DAYLIGHT,
            ContextualTimeZoneNameStyle.LONG_SPECIFIC.toTimeZoneNameStyle(daylight = true)
        )
        assertEquals(
            TimeZoneNameStyle.SHORT_GENERIC,
            ContextualTimeZoneNameStyle.SHORT_GENERIC.toTimeZoneNameStyle(daylight = false)
        )
        assertEquals(
            TimeZoneNameStyle.LONG_GENERIC,
            ContextualTimeZoneNameStyle.LONG_GENERIC.toTimeZoneNameStyle(daylight = false)
        )
        assertEquals(
            TimeZoneNameStyle.SHORT_STANDARD,
            ContextualTimeZoneNameStyle.SHORT_SPECIFIC.toTimeZoneNameStyle(daylight = false)
        )
        assertEquals(
            TimeZoneNameStyle.LONG_STANDARD,
            ContextualTimeZoneNameStyle.LONG_SPECIFIC.toTimeZoneNameStyle(daylight = false)
        )
    }

    @Test
    fun `toTimeZoneNameStyleSet() returns all possible regular styles`() {
        assertEquals(
            setOf(TimeZoneNameStyle.SHORT_STANDARD, TimeZoneNameStyle.SHORT_DAYLIGHT),
            ContextualTimeZoneNameStyle.SHORT_SPECIFIC.toTimeZoneNameStyleSet()
        )
        assertEquals(
            setOf(TimeZoneNameStyle.SHORT_GENERIC),
            ContextualTimeZoneNameStyle.SHORT_GENERIC.toTimeZoneNameStyleSet()
        )
        assertEquals(
            setOf(TimeZoneNameStyle.LONG_STANDARD, TimeZoneNameStyle.LONG_DAYLIGHT),
            ContextualTimeZoneNameStyle.LONG_SPECIFIC.toTimeZoneNameStyleSet()
        )
        assertEquals(
            setOf(TimeZoneNameStyle.LONG_GENERIC),
            ContextualTimeZoneNameStyle.LONG_GENERIC.toTimeZoneNameStyleSet()
        )
    }

    @Test
    fun `asGeneric() converts specific to a similar generic style`() {
        assertEquals(
            ContextualTimeZoneNameStyle.SHORT_GENERIC,
            ContextualTimeZoneNameStyle.SHORT_SPECIFIC.asGeneric()
        )
        assertEquals(
            ContextualTimeZoneNameStyle.SHORT_GENERIC,
            ContextualTimeZoneNameStyle.SHORT_GENERIC.asGeneric()
        )
        assertEquals(
            ContextualTimeZoneNameStyle.LONG_GENERIC,
            ContextualTimeZoneNameStyle.LONG_SPECIFIC.asGeneric()
        )
        assertEquals(
            ContextualTimeZoneNameStyle.LONG_GENERIC,
            ContextualTimeZoneNameStyle.LONG_GENERIC.asGeneric()
        )
    }
}
