package io.islandtime.measures

import io.islandtime.parser.TemporalParseException
import kotlin.test.*

class PeriodTest {
    @Test
    fun `ZERO returns a zeroed-out Period`() {
        val period = Period.ZERO

        assertEquals(0.years, period.years)
        assertEquals(0.months, period.months)
        assertEquals(0.days, period.days)
    }

    @Test
    fun `periodOf() constructs periods of years`() {
        val period = periodOf(10.years)

        assertEquals(10.years, period.years)
        assertEquals(0.months, period.months)
        assertEquals(0.days, period.days)
    }

    @Test
    fun `periodOf() constructs periods of months`() {
        val period = periodOf(15.months)

        assertEquals(0.years, period.years)
        assertEquals(15.months, period.months)
        assertEquals(0.days, period.days)
    }

    @Test
    fun `periodOf() constructs periods of weeks with equivalent days`() {
        val period = periodOf((-1).weeks)

        assertEquals(0.years, period.years)
        assertEquals(0.months, period.months)
        assertEquals((-7).days, period.days)
    }

    @Test
    fun `periodOf() constructs periods of days`() {
        val period = periodOf(100.days)

        assertEquals(0.years, period.years)
        assertEquals(0.months, period.months)
        assertEquals(100.days, period.days)
    }

    @Test
    fun `asPeriod() converts years to a period with similar years`() {
        val period = 3.years.asPeriod()

        assertEquals(3.years, period.years)
        assertEquals(0.months, period.months)
        assertEquals(0.days, period.days)
    }

    @Test
    fun `asPeriod() converts months to a period with similar months`() {
        val period = (1.years + 3.months).asPeriod()

        assertEquals(0.years, period.years)
        assertEquals(15.months, period.months)
        assertEquals(0.days, period.days)
    }

    @Test
    fun `asPeriod() converts weeks to a period with similar days`() {
        val period1 = 1.weeks.asPeriod()
        assertEquals(0.years, period1.years)
        assertEquals(0.months, period1.months)
        assertEquals(7.days, period1.days)

        val period2 = 1L.weeks.asPeriod()
        assertEquals(0.years, period2.years)
        assertEquals(0.months, period2.months)
        assertEquals(7.days, period2.days)
    }

    @Test
    fun `asPeriod() converts days to a period with similar days`() {
        val period = 10.days.asPeriod()

        assertEquals(0.years, period.years)
        assertEquals(0.months, period.months)
        assertEquals(10.days, period.days)
    }

    @Test
    fun `equality is based on equivalence of individual components`() {
        assertEquals(
            periodOf(1.years, 2.months, 0.days),
            periodOf(1.years, 2.months, 0.days)
        )

        assertNotEquals(
            periodOf(1.years, 2.months, 0.days),
            periodOf(0.years, 14.months, 0.days)
        )

        assertNotEquals(
            periodOf(1.years, 2.months, 0.days),
            periodOf(2.years, (-10).months, 0.days)
        )
    }

    @Test
    fun `can be broken down into components`() {
        val (years, months, days) = periodOf(1.years, 2.months, 3.days)
        assertEquals(1.years, years)
        assertEquals(2.months, months)
        assertEquals(3.days, days)
    }

    @Test
    fun `toString() returns 'P0D' when the period is zero`() {
        assertEquals("P0D", Period.ZERO.toString())
    }

    @Test
    fun `toString() returns a valid ISO8601 period representation of non-zero periods`() {
        listOf(
            periodOf(1.years) to "P1Y",
            periodOf((-1).years) to "P-1Y",
            periodOf(1.months) to "P1M",
            periodOf((-1).months) to "P-1M",
            periodOf(1.days) to "P1D",
            periodOf((-1).days) to "P-1D",
            periodOf(1.weeks) to "P7D",
            periodOf(10.years) to "P10Y",
            periodOf(10.years, (-12).months, 4.days) to "P10Y-12M4D",
            periodOf((-100).days) to "P-100D",
            periodOf(Int.MAX_VALUE.years, Int.MAX_VALUE.months, Int.MAX_VALUE.days) to
                "P${Int.MAX_VALUE}Y${Int.MAX_VALUE}M${Int.MAX_VALUE}D",
            periodOf(Int.MIN_VALUE.years, Int.MIN_VALUE.months, Int.MIN_VALUE.days) to
                "P${Int.MIN_VALUE}Y${Int.MIN_VALUE}M${Int.MIN_VALUE}D"
        ).forEach {
            assertEquals(it.second, it.first.toString())
        }
    }

    @Test
    fun `String_toPeriod() throws an exception when string is empty`() {
        assertFailsWith<TemporalParseException> { "".toPeriod() }
    }

    @Test
    fun `String_toPeriod() parses zero ISO-8601 period strings`() {
        assertEquals(Period.ZERO, "P0D".toPeriod())
    }

    @Test
    fun `String_toPeriod() parses valid ISO-8601 period strings`() {
        listOf(
            "P1Y13M6D" to periodOf(1.years, 13.months, 6.days),
            "P-1M10D" to periodOf((-1).months, 10.days),
            "P1D" to periodOf(1.days),
            "P1W" to periodOf(1.weeks),
            "-P1W" to periodOf((-1).weeks)
        ).forEach {
            assertEquals(it.second, it.first.toPeriod())
        }
    }

    @Test
    fun `String_toPeriod() throws an exception when the string contains duration components`() {
        listOf(
            "PT0S",
            "P1DT1H",
            "-P1DT1H",
            "PT1H",
            "-PT1H",
            "PT4S"
        ).forEach {
            assertFailsWith<TemporalParseException> { it.toPeriod() }
        }
    }

    @Test
    fun `totalMonths property combines years and months components`() {
        assertEquals(15L.months, periodOf(1.years, 3.months, 15.days).totalMonths)
        assertEquals(15L.months, periodOf((-1).years, 27.months, (-1).days).totalMonths)
        assertEquals(27917287411L.months, periodOf(Int.MAX_VALUE.years, Int.MAX_VALUE.months).totalMonths)
        assertEquals((-27917287424L).months, periodOf(Int.MIN_VALUE.years, Int.MIN_VALUE.months).totalMonths)
    }

    @Test
    fun `isZero() returns true for zeroed-out periods`() {
        assertTrue { Period.ZERO.isZero() }
    }

    @Test
    fun `isZero() returns false for non-zero periods`() {
        assertFalse { periodOf(1.days).isZero() }
    }

    @Test
    fun `isNegative() returns true if any component is less than zero`() {
        assertTrue { periodOf((-1).years, 1.months, 1.days).isNegative() }
        assertTrue { periodOf(1.years, (-1).months, 1.days).isNegative() }
        assertTrue { periodOf(1.years, 1.months, (-1).days).isNegative() }
    }

    @Test
    fun `isNegative() returns false if all components are zero or more`() {
        listOf(
            periodOf(1.years, 0.months, 0.days),
            periodOf(0.years, 1.months, 0.days),
            periodOf(0.years, 0.months, 1.days),
            Period.ZERO
        ).forEach {
            assertFalse { it.isNegative() }
        }
    }

    @Test
    fun `copy() replaces years value`() {
        assertEquals(periodOf((-1).years, 12.months), periodOf(1.years, 12.months).copy(years = (-1).years))
    }

    @Test
    fun `copy() replaces months value`() {
        assertEquals(periodOf(1.years, 12.months), periodOf(1.years, 3.months).copy(months = 12.months))
    }

    @Test
    fun `copy() replaces days value`() {
        assertEquals(Period.ZERO, periodOf(10.days).copy(days = 0.days))
    }

    @Test
    fun `normalized() converts months into whole years where possible`() {
        assertEquals(
            periodOf(1.years, 11.months, 20.days),
            periodOf(1.years, 11.months, 20.days).normalized()
        )

        assertEquals(
            periodOf(3.years, 3.months, 20.days),
            periodOf(2.years, 15.months, 20.days).normalized()
        )

        assertEquals(
            periodOf(0.years, 1.months, 20.days),
            periodOf(1.years, (-11).months, 20.days).normalized()
        )
    }

    @Test
    fun `normalized() throws an exception when overflow occurs`() {
        assertFailsWith<ArithmeticException> { periodOf(Int.MAX_VALUE.years, Int.MAX_VALUE.months).normalized() }
        assertFailsWith<ArithmeticException> { periodOf(Int.MIN_VALUE.years, Int.MIN_VALUE.months).normalized() }
    }

    @Test
    fun `unary minus changes the sign on all components`() {
        assertEquals(periodOf((-1).years, 1.months, 1.days), -periodOf(1.years, (-1).months, (-1).days))
    }

    @Test
    fun `unary minus throws an exception when overflow occurs`() {
        listOf(
            periodOf(Int.MIN_VALUE.years),
            periodOf(Int.MIN_VALUE.months),
            periodOf(Int.MIN_VALUE.days),
            periodOf(Int.MIN_VALUE.years, Int.MIN_VALUE.months),
            periodOf(Int.MIN_VALUE.years, Int.MIN_VALUE.days),
            periodOf(Int.MIN_VALUE.years, Int.MIN_VALUE.months, Int.MIN_VALUE.days)
        ).forEach {
            assertFailsWith<ArithmeticException> { -it }
        }
    }

    @Test
    fun `adding a period combines each component individually`() {
        assertEquals(
            periodOf(2.years, 18.months, 35.days),
            periodOf((-1).years, 2.months, (-1).days) + periodOf(3.years, 16.months, 36.days)
        )
    }

    @Test
    fun `subtracting a period combines each component individually`() {
        assertEquals(
            periodOf((-4).years, (-14).months, (-37).days),
            periodOf((-1).years, 2.months, (-1).days) - periodOf(3.years, 16.months, 36.days)
        )
    }

    @Test
    fun `adding years affects only years component`() {
        assertEquals(
            periodOf(1.years, 9.months),
            periodOf(9.months) + 1.years
        )
    }

    @Test
    fun `subtracting years affects only years component`() {
        assertEquals(
            periodOf((-1).years, 9.months),
            periodOf(9.months) - 1.years
        )
    }

    @Test
    fun `adding months affects only months component`() {
        assertEquals(
            periodOf(1.years, 13.months),
            periodOf(1.years, (-1).months) + 14.months
        )
    }

    @Test
    fun `subtracting months affects only months component`() {
        assertEquals(
            periodOf(1.years, (-12).months),
            periodOf(1.years) - 12.months
        )
    }

    @Test
    fun `adding weeks affects only days component`() {
        assertEquals(
            periodOf(1.years, 1.months, 6.days),
            periodOf(1.years, 1.months, (-1).days) + 1.weeks
        )
    }

    @Test
    fun `subtracting weeks affects only days component`() {
        assertEquals(
            periodOf(1.years, 1.months, (-6).days),
            periodOf(1.years, 1.months, 1.days) - 1.weeks
        )
    }

    @Test
    fun `adding days affects only days component`() {
        assertEquals(
            periodOf(1.years, 1.months, 1.days),
            periodOf(1.years, 1.months, (-1).days) + 2.days
        )
    }

    @Test
    fun `subtracting days affects only days component`() {
        assertEquals(
            periodOf(1.years, 1.months),
            periodOf(1.years, 1.months, 1.days) - 1.days
        )
    }
    
    @Test
    fun `adding or subtracting years causes an exception on overflow`() {
        assertFailsWith<ArithmeticException> { periodOf(Int.MAX_VALUE.years) + 1.years }
        assertFailsWith<ArithmeticException> { periodOf(Int.MIN_VALUE.years) - 1.years }
        assertFailsWith<ArithmeticException> { periodOf(1.years) + Int.MAX_VALUE.years }
        assertFailsWith<ArithmeticException> { periodOf((-1).years) + Int.MIN_VALUE.years }
    }

    @Test
    fun `adding or subtracting months causes an exception on overflow`() {
        assertFailsWith<ArithmeticException> { periodOf(Int.MAX_VALUE.months) + 1.months }
        assertFailsWith<ArithmeticException> { periodOf(Int.MIN_VALUE.months) - 1.months }
        assertFailsWith<ArithmeticException> { periodOf(1.months) + Int.MAX_VALUE.months }
        assertFailsWith<ArithmeticException> { periodOf((-1).months) + Int.MIN_VALUE.months }
    }

    @Test
    fun `adding or subtracting weeks causes an exception on overflow`() {
        assertFailsWith<ArithmeticException> { periodOf(Int.MAX_VALUE.days) + 1.weeks }
        assertFailsWith<ArithmeticException> { periodOf(Int.MIN_VALUE.days) - 1.weeks }
        assertFailsWith<ArithmeticException> { periodOf(1.days) + Int.MAX_VALUE.weeks }
        assertFailsWith<ArithmeticException> { periodOf((-1).days) + Int.MIN_VALUE.weeks }
    }

    @Test
    fun `adding or subtracting days causes an exception on overflow`() {
        assertFailsWith<ArithmeticException> { periodOf(Int.MAX_VALUE.days) + 1.days }
        assertFailsWith<ArithmeticException> { periodOf(Int.MIN_VALUE.days) - 1.days }
        assertFailsWith<ArithmeticException> { periodOf(1.days) + Int.MAX_VALUE.days }
        assertFailsWith<ArithmeticException> { periodOf((-1).days) + Int.MIN_VALUE.days }
    }

    @Test
    fun `multiplying by 1 has no effect`() {
        assertEquals(periodOf(1.days), periodOf(1.days) * 1)
    }

    @Test
    fun `multiplying a zero period by a scalar has no effect`() {
        assertEquals(Period.ZERO, Period.ZERO * 1500)
    }

    @Test
    fun `multiplying a non-zero period by a scalar multiplies each component individually`() {
        assertEquals(
            periodOf(2.years, (-2).months, 4.days),
            periodOf(1.years, (-1).months, 2.days) * 2
        )
    }

    @Test
    fun `multiplication causes an exception when overflow occurs`() {
        listOf(
            periodOf(Int.MIN_VALUE.years),
            periodOf(Int.MIN_VALUE.months),
            periodOf(Int.MIN_VALUE.days),
            periodOf(Int.MIN_VALUE.years, Int.MIN_VALUE.months),
            periodOf(Int.MIN_VALUE.years, Int.MIN_VALUE.days),
            periodOf(Int.MIN_VALUE.years, Int.MIN_VALUE.months, Int.MIN_VALUE.days)
        ).forEach {
            assertFailsWith<ArithmeticException> { it * -1 }
        }

        assertFailsWith<ArithmeticException> { periodOf(1.weeks) * Int.MAX_VALUE }
        assertFailsWith<ArithmeticException> { Int.MAX_VALUE * periodOf(2.days) }
    }

    @Test
    fun `adding a period to years returns a new period with adjusted years`() {
        assertEquals(
            periodOf(2.years, 1.months),
            1.years + periodOf(1.years, 1.months)
        )

        assertEquals(
            periodOf(Int.MAX_VALUE.years, 1.months),
            1.years + periodOf((Int.MAX_VALUE - 1).years, 1.months)
        )
    }

    @Test
    fun `adding a period to months returns a new period with adjusted months`() {
        assertEquals(
            periodOf(3.months, 4.days),
            (-2).months + periodOf(5.months, 4.days)
        )

        assertEquals(
            periodOf(Int.MAX_VALUE.months, 1.days),
            1.months + periodOf((Int.MAX_VALUE - 1).months, 1.days)
        )
    }

    @Test
    fun `adding a period to weeks returns a new period with adjusted days`() {
        assertEquals(
            periodOf(1.years, 33.days),
            5.weeks + periodOf(1.years, (-2).days)
        )

        assertEquals(
            periodOf(1.years, (-37).days),
            (-5L).weeks + periodOf(1.years, (-2).days)
        )
    }

    @Test
    fun `adding a period to days returns a new period with adjusted days`() {
        assertEquals(
            periodOf(1.years, 3.days),
            5.days + periodOf(1.years, (-2).days)
        )

        assertEquals(
            periodOf(1.years, 3.days),
            5L.days + periodOf(1.years, (-2).days)
        )

        assertEquals(
            periodOf(1.months, Int.MAX_VALUE.days),
            1.days + periodOf(1.months, (Int.MAX_VALUE - 1).days)
        )
    }

    @Test
    fun `subtracting a period from years returns a new period with adjusted years`() {
        assertEquals(
            periodOf((-1).months),
            1.years - periodOf(1.years, 1.months)
        )

        assertEquals(
            periodOf((-1).months),
            1L.years - periodOf(1.years, 1.months)
        )
    }

    @Test
    fun `subtracting a period from months returns a new period with adjusted months`() {
        assertEquals(
            periodOf((-4).days),
            2.months - periodOf(2.months, 4.days)
        )

        assertEquals(
            periodOf((-4).days),
            2L.months - periodOf(2.months, 4.days)
        )
    }

    @Test
    fun `subtracting a period from weeks returns a new period with adjusted days`() {
        assertEquals(
            periodOf((-1).years, 37.days),
            5.weeks - periodOf(1.years, (-2).days)
        )

        assertEquals(
            periodOf((-1).years, (-33).days),
            (-5L).weeks - periodOf(1.years, (-2).days)
        )
    }

    @Test
    fun `subtracting a period from days returns a new period with adjusted days`() {
        assertEquals(
            periodOf((-1).years, 7.days),
            5.days - periodOf(1.years, (-2).days)
        )

        assertEquals(
            periodOf((-1).years, 7.days),
            5L.days - periodOf(1.years, (-2).days)
        )
    }
}