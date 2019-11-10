package io.islandtime.ranges

import io.islandtime.Instant
import io.islandtime.measures.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class InstantProgressionTest {
    @Test
    fun `negative step causes an exception`() {
        val interval = Instant.UNIX_EPOCH..Instant(1L.days.inSeconds)

        assertFailsWith<IllegalArgumentException> { interval step (-1).days }
        assertFailsWith<IllegalArgumentException> { interval step (-1).hours }
        assertFailsWith<IllegalArgumentException> { interval step (-1).minutes }
        assertFailsWith<IllegalArgumentException> { interval step (-1).seconds }
        assertFailsWith<IllegalArgumentException> { interval step (-1).milliseconds }
        assertFailsWith<IllegalArgumentException> { interval step (-1).microseconds }
        assertFailsWith<IllegalArgumentException> { interval step (-1).nanoseconds }
    }

    @Test
    fun `zero step causes an exception`() {
        val interval = Instant.UNIX_EPOCH..Instant(1L.days.inSeconds)

        assertFailsWith<IllegalArgumentException> { interval step 0.days }
        assertFailsWith<IllegalArgumentException> { interval step 0.hours }
        assertFailsWith<IllegalArgumentException> { interval step 0.minutes }
        assertFailsWith<IllegalArgumentException> { interval step 0.seconds }
        assertFailsWith<IllegalArgumentException> { interval step 0.milliseconds }
        assertFailsWith<IllegalArgumentException> { interval step 0.microseconds }
        assertFailsWith<IllegalArgumentException> { interval step 0.nanoseconds }
    }

    @Test
    fun `reversed() turns a positive progression into a negative one`() {
        val start = Instant((-2L).days.inSeconds)
        val end = Instant(1L.days.inSeconds)
        val progression = start..end step 2.days
        val reversed = progression.reversed()

        assertEquals(progression.last, reversed.first)
        assertEquals(progression.first, reversed.last)
        assertFalse { reversed.isEmpty() }
        assertEquals((-2L).days.inSeconds, reversed.step)
        assertEquals(2, reversed.count())
    }

    @Test
    fun `one day step in positive progression`() {
        val start = Instant((-2L).days.inSeconds)
        val end = Instant(1L.days.inSeconds)
        val progression = start..end step 1.days

        assertEquals(start, progression.first)
        assertEquals(end, progression.last)
        assertFalse { progression.isEmpty() }
        assertEquals(1L.days.inSeconds, progression.step)
        assertEquals(4, progression.count())
    }

    @Test
    fun `one day step in negative progression`() {
        val start = Instant((-2L).days.inSeconds)
        val end = Instant(1L.days.inSeconds)
        val progression = end downTo start step 1.days

        assertEquals(end, progression.first)
        assertEquals(start, progression.last)
        assertFalse { progression.isEmpty() }
        assertEquals((-1L).days.inSeconds, progression.step)
        assertEquals(4, progression.count())
    }

    @Test
    fun `multi-day step`() {
        val start = Instant((-2L).days.inSeconds)
        val end = Instant(1L.days.inSeconds)
        val progression = start..end step 2.days

        assertEquals(start, progression.first)
        assertEquals(Instant.UNIX_EPOCH, progression.last)
        assertFalse { progression.isEmpty() }
        assertEquals(2L.days.inSeconds, progression.step)
        assertEquals(2, progression.count())
    }

    @Test
    fun `one hour step in positive progression`() {
        val start = Instant((-1L).days.inSeconds)
        val end = Instant(0L.days.inSeconds)
        val progression = start..end step 1.hours

        assertEquals(start, progression.first)
        assertEquals(end, progression.last)
        assertFalse { progression.isEmpty() }
        assertEquals(1L.hours.inSeconds, progression.step)
        assertEquals(25, progression.count())
    }

    @Test
    fun `one hour step in negative progression`() {
        val start = Instant((-1L).days.inSeconds)
        val end = Instant(0L.days.inSeconds)
        val progression = end downTo start step 1.hours

        assertEquals(end, progression.first)
        assertEquals(start, progression.last)
        assertFalse { progression.isEmpty() }
        assertEquals((-1L).hours.inSeconds, progression.step)
        assertEquals(25, progression.count())
    }

    @Test
    fun `multi-hour step`() {
        val start = Instant((-1L).days.inSeconds)
        val end = Instant(1L.hours.inSeconds)
        val progression = start..end step 2.hours

        assertEquals(start, progression.first)
        assertEquals(Instant(0L.days.inSeconds), progression.last)
        assertFalse { progression.isEmpty() }
        assertEquals(2L.hours.inSeconds, progression.step)
        assertEquals(13, progression.count())
    }

    @Test
    fun `one second step in positive progression`() {
        val start = Instant((-5L).seconds)
        val end = Instant(0L.seconds)
        val progression = start..end step 1.seconds

        assertEquals(start, progression.first)
        assertEquals(end, progression.last)
        assertFalse { progression.isEmpty() }
        assertEquals(1L.seconds, progression.step)
        assertEquals(6, progression.count())
    }

    @Test
    fun `multi-second step in negative progression`() {
        val start = Instant((-5L).seconds)
        val end = Instant(0L.seconds)
        val progression = end downTo start step 2.seconds

        assertEquals(end, progression.first)
        assertEquals(Instant((-4L).seconds), progression.last)
        assertFalse { progression.isEmpty() }
        assertEquals((-2L).seconds, progression.step)
        assertEquals(3, progression.count())
    }
}