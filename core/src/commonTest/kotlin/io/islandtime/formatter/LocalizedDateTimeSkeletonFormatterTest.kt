package io.islandtime.formatter

import io.islandtime.Month
import io.islandtime.Year
import io.islandtime.YearMonth
import io.islandtime.base.TemporalPropertyException
import io.islandtime.format.DateTimeFormatProvider
import io.islandtime.format.FormatStyle
import io.islandtime.locale.Locale
import io.islandtime.properties.DateProperty
import io.islandtime.test.AbstractIslandTimeTest
import io.islandtime.test.FakeDateTimeFormatProvider
import io.islandtime.test.FakeDateTimeTextProvider
import io.islandtime.test.temporalWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LocalizedDateTimeSkeletonFormatterTest : AbstractIslandTimeTest(
    dateTimeFormatProvider = FakeDateTimeFormatProviderWithSkeletonSupport,
    dateTimeTextProvider = FakeDateTimeTextProvider
) {
    @Test
    fun `throws an exception when the Temporal can't provide required properties`() {
        val formatter = LocalizedDateTimeFormatter("MMMy")
        assertFailsWith<TemporalPropertyException> { formatter.format(Year(2020)) }
    }

    @Test
    fun `returns expected output when skeleton support is available`() {
        val formatter = LocalizedDateTimeFormatter("MMMy")

        assertEquals(
            "Jul (SHORT) 2020",
            formatter.format(YearMonth(2020, Month.JULY))
        )
    }

    private object FakeDateTimeFormatProviderWithSkeletonSupport : DateTimeFormatProvider {
        override fun getFormatterFor(
            dateStyle: FormatStyle?,
            timeStyle: FormatStyle?,
            locale: Locale
        ): TemporalFormatter {
            return FakeDateTimeFormatProvider.getFormatterFor(dateStyle, timeStyle, locale)
        }

        override fun getFormatterFor(skeleton: String, locale: Locale): TemporalFormatter? {
            val pattern = when (skeleton) {
                "MMMy" -> "MMM y"
                else -> ""
            }

            return DateTimeFormatter(pattern)
        }
    }
}

class LocalizedDateTimeSkeletonFormatterNoSupportTest : AbstractIslandTimeTest(
    dateTimeFormatProvider = FakeDateTimeFormatProvider,
    dateTimeTextProvider = FakeDateTimeTextProvider
) {
    @Test
    fun `throws an exception when skeletons aren't supported`() {
        val formatter = LocalizedDateTimeFormatter("MMM")
        assertFailsWith<UnsupportedOperationException> { formatter.format(Month.JULY) }
    }

    @Test
    fun `does nothing when given an empty skeleton`() {
        val formatter = LocalizedDateTimeFormatter("")

        assertEquals(
            "",
            formatter.format(temporalWith(DateProperty.Year to 2000))
        )
    }
}
