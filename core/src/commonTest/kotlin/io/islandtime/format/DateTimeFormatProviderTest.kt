package io.islandtime.format

import io.islandtime.*
import io.islandtime.locale.localeOf
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@Suppress("PrivatePropertyName")
class DateTimeFormatProviderTest : AbstractIslandTimeTest() {
    private val en_US = localeOf("en-US")
    private val en_US_ca_japanese = localeOf("en-US-u-ca-japanese")
    private val de_DE = localeOf("de-DE")

    @Test
    fun `throws an exception when date and time style are both null`() {
        assertFailsWith<IllegalArgumentException> {
            DateTimeFormatProvider.formatterFor(null, null, en_US)
        }
    }

    @Test
    fun `formatter for date style only`() {
        val date = Date(2020, Month.FEBRUARY, 1)

        listOf(
            en_US to "Saturday, February 1, 2020",
            en_US_ca_japanese to "Saturday, February 1, 2020",
            de_DE to "Samstag, 1. Februar 2020"
        ).forEach { (locale, expectedResult) ->
            val formatter = DateTimeFormatProvider.formatterFor(
                FormatStyle.FULL,
                null,
                locale
            )

            assertEquals(
                expectedResult,
                formatter.format(date, TemporalFormatter.Settings(locale = locale))
            )
        }
    }

    @Test
    fun `formatter for time style only`() {
        val time = Time(13, 30, 30, 1)

        listOf(
            en_US to "1:30:30 PM",
            en_US_ca_japanese to "1:30:30 PM",
            de_DE to "13:30:30"
        ).forEach { (locale, expectedResult) ->
            val formatter = DateTimeFormatProvider.formatterFor(
                null,
                FormatStyle.MEDIUM,
                locale
            )

            assertEquals(
                expectedResult,
                formatter.format(time, TemporalFormatter.Settings(locale = locale))
            )
        }
    }

    @Test
    fun `formatter for date-time style`() {
        val zonedDateTime = Date(2020, Month.FEBRUARY, 1) at
            Time(13, 30, 30, 1) at
            TimeZone("America/New_York")

        listOf(
            en_US to "2/1/20 1:30:30 PM EST",
            en_US_ca_japanese to "2/1/20 1:30:30 PM EST",
            de_DE to listOf("01.02.20 13:30:30 GMT-5", "01.02.20 13:30:30 EST")
        ).forEach { (locale, expectedResult) ->
            val formatter = DateTimeFormatProvider.formatterFor(
                FormatStyle.SHORT,
                FormatStyle.LONG,
                locale
            )

            val result = formatter.format(
                zonedDateTime,
                TemporalFormatter.Settings(locale = locale)
            ).replace(",", "")

            if (expectedResult is String) {
                assertEquals(expectedResult, result)
            } else if (expectedResult is List<*>) {
                assertTrue { expectedResult.any { it == result } }
            }
        }
    }
}