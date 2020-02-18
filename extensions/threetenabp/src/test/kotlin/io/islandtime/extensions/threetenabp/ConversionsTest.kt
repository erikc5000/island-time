package io.islandtime.extensions.threetenabp

import io.islandtime.Month
import io.islandtime.Date
import io.islandtime.measures.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ConversionsTest {
    @Test
    fun `converts Date to Java LocalDate`() {
        val islandDate = Date(2019, Month.MAY, 3)
        val javaDate = islandDate.toJavaLocalDate()

        assertEquals(islandDate.year, javaDate.year)
        assertEquals(islandDate.monthNumber, javaDate.monthValue)
        assertEquals(islandDate.dayOfMonth, javaDate.dayOfMonth)
    }

    @Test
    fun `converts Java LocalDate to Date`() {
        val javaDate = org.threeten.bp.LocalDate.of(2019, org.threeten.bp.Month.MAY, 3)
        val islandDate = javaDate.toIslandDate()

        assertEquals(javaDate.year, islandDate.year)
        assertEquals(javaDate.monthValue, islandDate.monthNumber)
        assertEquals(javaDate.dayOfMonth, islandDate.dayOfMonth)
    }

    @Test
    fun `converts days to Java Duration`() {
        assertEquals(org.threeten.bp.Duration.ZERO, 0.days.toJavaDuration())
        assertEquals(org.threeten.bp.Duration.ZERO, 0L.days.toJavaDuration())
        assertEquals(1L, 1.days.toJavaDuration().toDays())
        assertEquals(-1L, (-1L).days.toJavaDuration().toDays())
    }

    @Test
    fun `converts hours to Java Duration`() {
        assertEquals(org.threeten.bp.Duration.ZERO, 0.hours.toJavaDuration())
        assertEquals(org.threeten.bp.Duration.ZERO, 0L.hours.toJavaDuration())
        assertEquals(1L, 1.hours.toJavaDuration().toHours())
        assertEquals(-1L, (-1L).hours.toJavaDuration().toHours())
    }

    @Test
    fun `converts minutes to Java Duration`() {
        assertEquals(org.threeten.bp.Duration.ZERO, 0.minutes.toJavaDuration())
        assertEquals(org.threeten.bp.Duration.ZERO, 0L.minutes.toJavaDuration())
        assertEquals(1L, 1.minutes.toJavaDuration().toMinutes())
        assertEquals(-1L, (-1L).minutes.toJavaDuration().toMinutes())
    }

    @Test
    fun `converts seconds to Java Duration`() {
        assertEquals(org.threeten.bp.Duration.ZERO, 0.seconds.toJavaDuration())
        assertEquals(org.threeten.bp.Duration.ZERO, 0L.seconds.toJavaDuration())
        assertEquals(1L, 1.seconds.toJavaDuration().seconds)
        assertEquals(-1L, (-1L).seconds.toJavaDuration().seconds)
    }

    @Test
    fun `converts milliseconds to Java Duration`() {
        assertEquals(org.threeten.bp.Duration.ZERO, 0.milliseconds.toJavaDuration())
        assertEquals(org.threeten.bp.Duration.ZERO, 0L.milliseconds.toJavaDuration())
        assertEquals(1_000_000_000L, 1_000_000_000.milliseconds.toJavaDuration().toMillis())
        assertEquals(-1L, (-1L).milliseconds.toJavaDuration().toMillis())
    }

    @Test
    fun `converts microseconds to Java Duration`() {
        assertEquals(org.threeten.bp.Duration.ZERO, 0.microseconds.toJavaDuration())
        assertEquals(org.threeten.bp.Duration.ZERO, 0L.microseconds.toJavaDuration())
        assertEquals(1_000_000_000L, 1_000_000_000.microseconds.toJavaDuration().toNanos() / 1000)
        assertEquals(-1L, (-1L).microseconds.toJavaDuration().toNanos() / 1000)
    }

    @Test
    fun `converts nanoseconds to Java Duration`() {
        assertEquals(org.threeten.bp.Duration.ZERO, 0.nanoseconds.toJavaDuration())
        assertEquals(org.threeten.bp.Duration.ZERO, 0L.nanoseconds.toJavaDuration())
        assertEquals(1_000_000_000L, 1_000_000_000.nanoseconds.toJavaDuration().toNanos())
        assertEquals(-1L, (-1L).nanoseconds.toJavaDuration().toNanos())
    }
}