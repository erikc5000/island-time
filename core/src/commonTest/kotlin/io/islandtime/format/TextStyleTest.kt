package io.islandtime.format

import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TextStyleTest : AbstractIslandTimeTest() {
    @Test
    fun `isStandalone() returns true for standalone styles`() {
        assertTrue { TextStyle.FULL_STANDALONE.isStandalone() }
        assertTrue { TextStyle.SHORT_STANDALONE.isStandalone() }
        assertTrue { TextStyle.NARROW_STANDALONE.isStandalone() }
    }

    @Test
    fun `isStandalone() returns false for normal styles`() {
        assertFalse { TextStyle.FULL.isStandalone() }
        assertFalse { TextStyle.SHORT.isStandalone() }
        assertFalse { TextStyle.NARROW.isStandalone() }
    }

    @Test
    fun `asStandalone() returns a similar standalone style`() {
        assertEquals(TextStyle.FULL_STANDALONE, TextStyle.FULL.asStandalone())
        assertEquals(TextStyle.FULL_STANDALONE, TextStyle.FULL_STANDALONE.asStandalone())
        assertEquals(TextStyle.SHORT_STANDALONE, TextStyle.SHORT.asStandalone())
        assertEquals(TextStyle.SHORT_STANDALONE, TextStyle.SHORT_STANDALONE.asStandalone())
        assertEquals(TextStyle.NARROW_STANDALONE, TextStyle.NARROW.asStandalone())
        assertEquals(TextStyle.NARROW_STANDALONE, TextStyle.NARROW_STANDALONE.asStandalone())
    }

    @Test
    fun `asNormal() returns a similar normal style`() {
        assertEquals(TextStyle.FULL, TextStyle.FULL.asNormal())
        assertEquals(TextStyle.FULL, TextStyle.FULL_STANDALONE.asNormal())
        assertEquals(TextStyle.SHORT, TextStyle.SHORT.asNormal())
        assertEquals(TextStyle.SHORT, TextStyle.SHORT_STANDALONE.asNormal())
        assertEquals(TextStyle.NARROW, TextStyle.NARROW.asNormal())
        assertEquals(TextStyle.NARROW, TextStyle.NARROW_STANDALONE.asNormal())
    }
}
