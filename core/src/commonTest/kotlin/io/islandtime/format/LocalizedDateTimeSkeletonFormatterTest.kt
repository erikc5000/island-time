package io.islandtime.format

import io.islandtime.Month
import io.islandtime.Year
import io.islandtime.YearMonth
import io.islandtime.base.DateProperty
import io.islandtime.base.TemporalPropertyException
import io.islandtime.locale.Locale
import io.islandtime.test.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LocalizedDateTimeSkeletonFormatterTest : AbstractIslandTimeTest(
    testDateTimeFormatProvider = FakeDateTimeFormatProviderWithSkeletonSupport,
    testDateTimeTextProvider = FakeDateTimeTextProvider
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
        override fun formatterFor(
            dateStyle: FormatStyle?,
            timeStyle: FormatStyle?,
            locale: Locale
        ): TemporalFormatter {
            return FakeDateTimeFormatProvider.formatterFor(dateStyle, timeStyle, locale)
        }

        override fun formatterFor(skeleton: String, locale: Locale): TemporalFormatter? {
            val pattern = when (skeleton) {
                "MMMy" -> "MMM y"
                else -> ""
            }

            return DateTimeFormatter(pattern)
        }
    }
}

class LocalizedDateTimeSkeletonFormatterNoSupportTest : AbstractIslandTimeTest(
    testDateTimeFormatProvider = FakeDateTimeFormatProvider,
    testDateTimeTextProvider = FakeDateTimeTextProvider
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