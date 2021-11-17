package io.islandtime.measures

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.days as kotlinDays

class DaysTest {
    @Test
    fun `Days can be compared to other Days`() {
        assertTrue { 0.days < 1.days }
        assertTrue { 0.days == 0.days }
        assertTrue { 5.days > (-1).days }
    }

    @Test
    fun `toString() converts zero days to 'P0D'`() {
        assertEquals("P0D", 0.days.toString())
    }

    @Test
    fun `toString() converts to ISO-8601 period representation`() {
        assertEquals("P1D", 1.days.toString())
        assertEquals("-P1D", (-1).days.toString())
    }

    @Test
    fun `conversion to Kotlin Duration`() {
        assertEquals(0.kotlinDays, 0.days.toKotlinDuration())
        assertEquals(1.kotlinDays, 1.days.toKotlinDuration())
        assertEquals((-1).kotlinDays, (-1L).days.toKotlinDuration())
        assertEquals(Long.MIN_VALUE.kotlinDays, Long.MIN_VALUE.days.toKotlinDuration())
    }

    @Test
    fun `conversion from Kotlin Duration`() {
        assertEquals(0.days, 0.kotlinDays.toIslandDays())
        assertEquals(1.days, 1.kotlinDays.toIslandDays())
        assertEquals((-1).days, (-1).kotlinDays.toIslandDays())
        assertEquals(Long.MIN_VALUE.days, Long.MIN_VALUE.kotlinDays.toIslandDays())
    }
}
