package io.islandtime.measures

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime
import kotlin.time.days as kotlinDays

class DaysTest {
    @Test
    fun `IntDays can be compared to other IntDays`() {
        assertTrue { 0.days < 1.days }
        assertTrue { 0.days == 0.days }
        assertTrue { 5.days > (-1).days }
    }

    @Test
    fun `LongDays can be compared to other LongDays`() {
        assertTrue { 0L.days < 1L.days }
        assertTrue { 0L.days == 0L.days }
        assertTrue { 5L.days > (-1L).days }
    }

    @Test
    fun `toLongDays() converts IntDays to LongDays`() {
        assertEquals(2L.days, 2.days.toLongDays())
    }

    @Test
    fun `toIntDays() converts LongDays to IntDays`() {
        assertEquals(2.days, 2L.days.toIntDays())
    }

    @Test
    fun `IntDays_toString() converts zero days to 'P0D'`() {
        assertEquals("P0D", 0.days.toString())
    }

    @Test
    fun `IntDays_toString() converts to ISO-8601 period representation`() {
        assertEquals("P1D", 1.days.toString())
        assertEquals("-P1D", (-1).days.toString())
    }

    @Test
    fun `LongDays_toString() converts zero days to 'P0D'`() {
        assertEquals("P0D", 0L.days.toString())
    }

    @Test
    fun `LongDays_toString() converts to ISO-8601 period representation`() {
        assertEquals("P1D", 1L.days.toString())
        assertEquals("-P1D", (-1L).days.toString())
    }

    @ExperimentalTime
    @Test
    fun `conversion to Kotlin Duration`() {
        assertEquals(0.kotlinDays, 0.days.toKotlinDuration())
        assertEquals(1.kotlinDays, 1.days.toKotlinDuration())
        assertEquals((-1).kotlinDays, (-1L).days.toKotlinDuration())
        assertEquals(Long.MIN_VALUE.kotlinDays, Long.MIN_VALUE.days.toKotlinDuration())
    }

    @ExperimentalTime
    @Test
    fun `conversion from Kotlin Duration`() {
        assertEquals(0L.days, 0.kotlinDays.toIslandDays())
        assertEquals(1L.days, 1.kotlinDays.toIslandDays())
        assertEquals((-1L).days, (-1L).kotlinDays.toIslandDays())
        assertEquals(Long.MIN_VALUE.days, Long.MIN_VALUE.kotlinDays.toIslandDays())
    }
}