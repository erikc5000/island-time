package io.islandtime.format

import io.islandtime.DateTimeException
import io.islandtime.base.DateProperty
import io.islandtime.test.temporalWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class WholeNumberFormatterTest {
    @Test
    fun `throws an exception when minLength is greater than maxLength`() {
        assertFailsWith<IllegalArgumentException> {
            temporalFormatter { wholeNumber(DateProperty.Year, 2, 1) }
        }

        assertFailsWith<IllegalArgumentException> {
            temporalFormatter { wholeNumber(DateProperty.Year, 1, 0) }
        }
    }

    @Test
    fun `throws an exception when minLength is out of range`() {
        assertFailsWith<IllegalArgumentException> {
            temporalFormatter { wholeNumber(DateProperty.Year, 0, 1) }
        }

        assertFailsWith<IllegalArgumentException> {
            temporalFormatter { wholeNumber(DateProperty.Year, 20, 20) }
        }
    }

    @Test
    fun `throws an exception when maxLength is out of range`() {
        assertFailsWith<IllegalArgumentException> {
            temporalFormatter { wholeNumber(DateProperty.Year, 1, 20) }
        }

        assertFailsWith<IllegalArgumentException> {
            temporalFormatter { wholeNumber(DateProperty.Year, 0, 0) }
        }
    }

    @Test
    fun `formats variable length numbers`() {
        val formatter = temporalFormatter { wholeNumber(DateProperty.Year) }

        listOf(
            0L to "0",
            1 to "1",
            -1L to "-1",
            Long.MAX_VALUE to "9223372036854775807",
            Long.MIN_VALUE to "-9223372036854775808"
        ).forEach { (value, expectedResult) ->
            assertEquals(
                expectedResult,
                formatter.format(temporalWith(DateProperty.Year to value))
            )
        }
    }

    @Test
    fun `formats fixed length numbers`() {
        val formatter = temporalFormatter { wholeNumber(DateProperty.Year, 19) }

        listOf(
            0L to "0000000000000000000",
            1 to "0000000000000000001",
            -1L to "-0000000000000000001",
            Long.MAX_VALUE to "9223372036854775807",
            Long.MIN_VALUE to "-9223372036854775808"
        ).forEach { (value, expectedResult) ->
            assertEquals(
                expectedResult,
                formatter.format(temporalWith(DateProperty.Year to value))
            )
        }
    }

    @Test
    fun `throws an exception when maxLength is exceeded by the value being formatted`() {
        val formatter = temporalFormatter { wholeNumber(DateProperty.Year, 2) }

        assertFailsWith<DateTimeException> {
            formatter.format(temporalWith(DateProperty.Year to 100L))
        }
    }

    @Test
    fun `respects ALWAYS sign style when formatting`() {
        val formatter = temporalFormatter {
            wholeNumber(DateProperty.Year, 19) {
                signStyle = SignStyle.ALWAYS
            }
        }

        listOf(
            0L to "+0000000000000000000",
            1 to "+0000000000000000001",
            -1L to "-0000000000000000001",
            Long.MAX_VALUE to "+9223372036854775807",
            Long.MIN_VALUE to "-9223372036854775808"
        ).forEach { (value, expectedResult) ->
            assertEquals(
                expectedResult,
                formatter.format(temporalWith(DateProperty.Year to value))
            )
        }
    }

    @Test
    fun `formats non-negative numbers when the NEVER sign style is used`() {
        val formatter = temporalFormatter {
            wholeNumber(DateProperty.Year) {
                signStyle = SignStyle.NEVER
            }
        }

        assertEquals(
            "0",
            formatter.format(temporalWith(DateProperty.Year to 0L))
        )
    }

    @Test
    fun `throws an exception when the NEVER sign style is used and a number is negative`() {
        val formatter = temporalFormatter {
            wholeNumber(DateProperty.Year) {
                signStyle = SignStyle.NEVER
            }
        }

        assertFailsWith<DateTimeException> {
            formatter.format(temporalWith(DateProperty.Year to -1L))
        }
    }

    @Test
    fun `respects the number style when formatting`() {
        val formatter = temporalFormatter { wholeNumber(DateProperty.Year) }
        val numberStyle = NumberStyle.DEFAULT.copy(zeroDigit = '०')
        val settings = TemporalFormatter.Settings(numberStyle = numberStyle)

        assertEquals(
            "२०२०",
            formatter.format(temporalWith(DateProperty.Year to 2020L), settings)
        )
    }
}