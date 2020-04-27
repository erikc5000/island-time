package io.islandtime.format

import io.islandtime.*
import io.islandtime.base.TemporalPropertyException
import io.islandtime.locale.localeOf
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@Suppress("PrivatePropertyName")
class DateTimeFormatterTest : AbstractIslandTimeTest() {
    private val en_US = localeOf("en-US")
    private val de_DE = localeOf("de-DE")

//    @Test
//    fun `localized date only`() {
//        val date = Date(2020, Month.FEBRUARY, 1)
//        val formatter = LocalizedDateFormatter(FormatStyle.FULL)
//
//        listOf(
//            en_US to "Saturday, February 1, 2020",
//            de_DE to "Samstag, 1. Februar 2020"
//        ).forEach { (locale, expectedResult) ->
//            assertEquals(
//                expectedResult,
//                formatter.format(date, TemporalFormatter.Settings(locale = locale))
//            )
//        }
//    }
//
//    @Test
//    fun `localized time only`() {
//        val time = Time(13, 30, 30, 1)
//        val formatter = LocalizedTimeFormatter(FormatStyle.MEDIUM)
//
//        listOf(
//            en_US to "1:30:30 PM",
//            de_DE to "13:30:30"
//        ).forEach { (locale, expectedResult) ->
//            assertEquals(
//                expectedResult,
//                formatter.format(time, TemporalFormatter.Settings(locale = locale))
//            )
//        }
//    }
//
//    @Test
//    fun `localized date-time`() {
//        val zonedDateTime = Date(2020, Month.FEBRUARY, 1) at
//            Time(13, 30, 30, 1) at
//            TimeZone("America/New_York")
//
//        val formatter = LocalizedDateTimeFormatter(FormatStyle.SHORT, FormatStyle.LONG)
//
//        listOf(
//            en_US to "2/1/20 1:30:30 PM EST",
//            de_DE to "01.02.20 13:30:30 EST"
//        ).forEach { (locale, expectedResult) ->
//            assertEquals(
//                expectedResult,
//                formatter.format(zonedDateTime, TemporalFormatter.Settings(locale = locale))
//            )
//        }
//    }

    @Test
    fun `throws an exception when the Temporal can't provide required properties`() {
        val date = Date(2020, Month.FEBRUARY, 1)
        val formatter = LocalizedDateTimeFormatter(FormatStyle.FULL)

        assertFailsWith<TemporalPropertyException> {
            formatter.format(date, TemporalFormatter.Settings(locale = en_US))
        }
    }
}