package io.islandtime.darwin

import io.islandtime.Month
import io.islandtime.YearMonth
import io.islandtime.format.DateTimeFormatProvider
import io.islandtime.formatter.TemporalFormatter
import io.islandtime.locale.toLocale
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("PrivatePropertyName")
class DarwinDateTimeFormatProviderTest : AbstractIslandTimeTest() {
    private val en_US = "en-US".toLocale()
    private val en_US_ca_japanese = "en-US-u-ca-japanese".toLocale()
    private val de_DE = "de-DE".toLocale()

    @Test
    fun `formatter from year-month skeleton`() {
        val yearMonth = YearMonth(2020, Month.MARCH)

        listOf(
            en_US to "March 2020",
            en_US_ca_japanese to "March 2020",
            de_DE to "März 2020"
        ).forEach { (locale, expectedResult) ->
            val formatter = DateTimeFormatProvider.getFormatterFor("MMMMy", locale)

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
            val formatter = DateTimeFormatProvider.getFormatterFor("RR", locale)

            assertEquals(
                expectedResult,
                formatter?.format(yearMonth, TemporalFormatter.Settings(locale = locale))
            )
        }
    }
}
