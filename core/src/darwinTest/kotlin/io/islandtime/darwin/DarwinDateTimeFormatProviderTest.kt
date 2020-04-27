package io.islandtime.darwin

import io.islandtime.Month
import io.islandtime.YearMonth
import io.islandtime.format.DateTimeFormatProvider
import io.islandtime.format.TemporalFormatter
import io.islandtime.locale.localeOf
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("PrivatePropertyName")
class DarwinDateTimeFormatProviderTest : AbstractIslandTimeTest() {
    private val en_US = localeOf("en-US")
    private val en_US_ca_japanese = localeOf("en-US-u-ca-japanese")
    private val de_DE = localeOf("de-DE")

    @Test
    fun `formatter from year-month skeleton`() {
        val yearMonth = YearMonth(2020, Month.MARCH)

        listOf(
            en_US to "March 2020",
            en_US_ca_japanese to "March 2020",
            de_DE to "März 2020"
        ).forEach { (locale, expectedResult) ->
            val formatter = DateTimeFormatProvider.formatterFor("MMMMy", locale)

            assertEquals(
                expectedResult,
                formatter?.format(yearMonth, TemporalFormatter.Settings(locale = locale))
            )
        }
    }

    @Test
    fun `an invalid skeleton returns an empty formatter`() {
        val yearMonth = YearMonth(2020, Month.MARCH)

        listOf(
            en_US to "",
            en_US_ca_japanese to "",
            de_DE to ""
        ).forEach { (locale, expectedResult) ->
            val formatter = DateTimeFormatProvider.formatterFor("RR", locale)

            assertEquals(
                expectedResult,
                formatter?.format(yearMonth, TemporalFormatter.Settings(locale = locale))
            )
        }
    }
}