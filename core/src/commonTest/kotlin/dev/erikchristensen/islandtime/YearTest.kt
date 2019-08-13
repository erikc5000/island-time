package dev.erikchristensen.islandtime

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class YearTest {
    @Test
    fun `isLeapYear() returns false for common years`() {
        assertFalse { isLeapYear(2001) }
        assertFalse { isLeapYear(1900) }
        assertFalse { isLeapYear(1965) }
    }

    @Test
    fun `isLeapYear() returns true for leap years`() {
        assertTrue { isLeapYear(2000) }
        assertTrue { isLeapYear(1964) }
    }
}