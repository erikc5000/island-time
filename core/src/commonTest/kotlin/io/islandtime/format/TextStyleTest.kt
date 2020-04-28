package io.islandtime.format

import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TextStyleTest : AbstractIslandTimeTest() {
    @Test
    fun `TextStyle_isStandalone() returns true for standalone styles`() {
        assertTrue { TextStyle.FULL_STANDALONE.isStandalone() }
        assertTrue { TextStyle.SHORT_STANDALONE.isStandalone() }
        assertTrue { TextStyle.NARROW_STANDALONE.isStandalone() }
    }

    @Test
    fun `TextStyle_isStandalone() returns false for normal styles`() {
        assertFalse { TextStyle.FULL.isStandalone() }
        assertFalse { TextStyle.SHORT.isStandalone() }
        assertFalse { TextStyle.NARROW.isStandalone() }
    }
    
    @Test
    fun `TextStyle_asStandalone() returns a similar standalone style`() {
        assertEquals(TextStyle.FULL_STANDALONE, TextStyle.FULL.asStandalone())
        assertEquals(TextStyle.FULL_STANDALONE, TextStyle.FULL_STANDALONE.asStandalone())
        assertEquals(TextStyle.SHORT_STANDALONE, TextStyle.SHORT.asStandalone())
        assertEquals(TextStyle.SHORT_STANDALONE, TextStyle.SHORT_STANDALONE.asStandalone())
        assertEquals(TextStyle.NARROW_STANDALONE, TextStyle.NARROW.asStandalone())
        assertEquals(TextStyle.NARROW_STANDALONE, TextStyle.NARROW_STANDALONE.asStandalone())
    }

    @Test
    fun `TextStyle_asNormal() returns a similar normal style`() {
        assertEquals(TextStyle.FULL, TextStyle.FULL.asNormal())
        assertEquals(TextStyle.FULL, TextStyle.FULL_STANDALONE.asNormal())
        assertEquals(TextStyle.SHORT, TextStyle.SHORT.asNormal())
        assertEquals(TextStyle.SHORT, TextStyle.SHORT_STANDALONE.asNormal())
        assertEquals(TextStyle.NARROW, TextStyle.NARROW.asNormal())
        assertEquals(TextStyle.NARROW, TextStyle.NARROW_STANDALONE.asNormal())
    }

    @Test
    fun `TimeZoneTextStyle_isShort() returns true for short styles`() {
        assertTrue { TimeZoneTextStyle.SHORT_STANDARD.isShort() }
        assertTrue { TimeZoneTextStyle.SHORT_DAYLIGHT_SAVING.isShort() }
        assertTrue { TimeZoneTextStyle.SHORT_GENERIC.isShort() }
    }

    @Test
    fun `TimeZoneTextStyle_isShort() returns false for normal styles`() {
        assertFalse { TimeZoneTextStyle.STANDARD.isShort() }
        assertFalse { TimeZoneTextStyle.DAYLIGHT_SAVING.isShort() }
        assertFalse { TimeZoneTextStyle.GENERIC.isShort() }
    }
    
    @Test
    fun `TimeZoneStyle_isStandard() returns true for standard styles`() {
        assertTrue { TimeZoneTextStyle.STANDARD.isStandard() }
        assertTrue { TimeZoneTextStyle.SHORT_STANDARD.isStandard() }
    }

    @Test
    fun `TimeZoneStyle_isStandard() returns false for non-standard styles`() {
        assertFalse { TimeZoneTextStyle.DAYLIGHT_SAVING.isStandard() }
        assertFalse { TimeZoneTextStyle.SHORT_DAYLIGHT_SAVING.isStandard() }
        assertFalse { TimeZoneTextStyle.GENERIC.isStandard() }
        assertFalse { TimeZoneTextStyle.SHORT_GENERIC.isStandard() }
    }

    @Test
    fun `TimeZoneStyle_isDaylightSaving() returns true for daylight saving styles`() {
        assertTrue { TimeZoneTextStyle.DAYLIGHT_SAVING.isDaylightSaving() }
        assertTrue { TimeZoneTextStyle.SHORT_DAYLIGHT_SAVING.isDaylightSaving() }
    }

    @Test
    fun `TimeZoneStyle_isDaylightSaving() returns false for non-standard styles`() {
        assertFalse { TimeZoneTextStyle.STANDARD.isDaylightSaving() }
        assertFalse { TimeZoneTextStyle.SHORT_STANDARD.isDaylightSaving() }
        assertFalse { TimeZoneTextStyle.GENERIC.isDaylightSaving() }
        assertFalse { TimeZoneTextStyle.SHORT_GENERIC.isDaylightSaving() }
    }

    @Test
    fun `TimeZoneStyle_isGeneric() returns true for standard styles`() {
        assertTrue { TimeZoneTextStyle.GENERIC.isGeneric() }
        assertTrue { TimeZoneTextStyle.SHORT_GENERIC.isGeneric() }
    }

    @Test
    fun `TimeZoneStyle_isGeneric() returns false for non-standard styles`() {
        assertFalse { TimeZoneTextStyle.STANDARD.isGeneric() }
        assertFalse { TimeZoneTextStyle.SHORT_STANDARD.isGeneric() }
        assertFalse { TimeZoneTextStyle.DAYLIGHT_SAVING.isGeneric() }
        assertFalse { TimeZoneTextStyle.SHORT_DAYLIGHT_SAVING.isGeneric() }
    }
}