package io.islandtime.format

import io.islandtime.*
import io.islandtime.base.Temporal
import io.islandtime.calendar.WeekProperty
import io.islandtime.formatter.DateTimeFormatter
import io.islandtime.formatter.TemporalFormatter
import io.islandtime.locale.Locale
import io.islandtime.locale.toLocale
import io.islandtime.measures.hours
import io.islandtime.measures.minutes
import io.islandtime.measures.seconds
import io.islandtime.parser.DateTimeParser
import io.islandtime.parser.TemporalParseException
import io.islandtime.parser.TemporalParser
import io.islandtime.properties.DateProperty
import io.islandtime.properties.TimeProperty
import io.islandtime.properties.TimeZoneProperty
import io.islandtime.properties.UtcOffsetProperty
import io.islandtime.test.AbstractIslandTimeTest
import io.islandtime.test.FakeDateTimeTextProvider
import io.islandtime.test.FakeTimeZoneNameProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.todo

@Suppress("PrivatePropertyName")
class DateTimePatternTest : AbstractIslandTimeTest(
    dateTimeTextProvider = FakeDateTimeTextProvider,
    timeZoneNameProvider = FakeTimeZoneNameProvider
) {
    private val emptyTemporal = object : Temporal {}

    private val zonedDateTime =
        Date(2020, Month.MARCH, 15) at
            Time(13, 30, 1, 2_999_999) at
            TimeZone("America/New_York")

    private val en_US = "en-US".toLocale()
    private val de_DE = "de-DE".toLocale()
    private val en_US_parserSettings = parserSettings(en_US)
    private val de_DE_parserSettings = parserSettings(de_DE)
    private val en_US_formatterSettings get() = formatterSettings(en_US)
    private val de_DE_formatterSettings get() = formatterSettings(de_DE)

    private fun parserSettings(locale: Locale) = TemporalParser.Settings(locale = locale)
    private fun formatterSettings(locale: Locale) = TemporalFormatter.Settings(locale = locale)

    private data class ParserFormatterTestData(
        val pattern: String,
        val numericValue: Long,
        val textValue: String,
        val locale: Locale = "en-US".toLocale(),
        val message: String = "pattern = $pattern formatted = $textValue locale = $locale\n"
    )

    @Test
    fun `parses and formats empty patterns`() {
        assertEquals(0, DateTimeParser("").parse("", en_US_parserSettings).propertyCount)
        assertEquals("", DateTimeFormatter("").format(emptyTemporal, en_US_formatterSettings))
    }

    @Test
    fun `parses and formats non-letter characters without escaping`() {
        listOf(
            "." to ".",
            ", " to ", ",
            "\\/?!#[]" to "\\/?!#[]"
        ).forEach { (pattern, value) ->
            assertEquals(0, DateTimeParser(pattern).parse(value, en_US_parserSettings).propertyCount)
            assertEquals(value, DateTimeFormatter(pattern).format(emptyTemporal, en_US_formatterSettings))
        }
    }

    @Test
    fun `parses and formats escaped literals`() {
        listOf(
            "'A'" to "A",
            "'A' " to "A ",
            "'A much longer literal'." to "A much longer literal."
        ).forEach { (pattern, value) ->
            assertEquals(0, DateTimeParser(pattern).parse(value, en_US_parserSettings).propertyCount)
            assertEquals(value, DateTimeFormatter(pattern).format(emptyTemporal, en_US_formatterSettings))
        }
    }

    @Test
    fun `parses and formats literals with escaped apostrophe`() {
        listOf(
            "''." to "'.",
            "'''t'" to "'t",
            "''''" to "'",
            "''''''" to "''",
            "'''' " to "' "
        ).forEach { (pattern, value) ->
            assertEquals(0, DateTimeParser(pattern).parse(value, en_US_parserSettings).propertyCount)
            assertEquals(value, DateTimeFormatter(pattern).format(emptyTemporal, en_US_formatterSettings))
        }
    }

    @Test
    fun `throws an exception when the pattern ends with an unterminated literal`() {
        expectFailsWith<IllegalArgumentException>(
            "'T",
            "'T ",
            "'''''''"
        )
    }

    @Test
    fun `parses and formats era correctly`() {
        val year = Year(2020)

        listOf(
            "G" to "AD",
            "GG" to "AD",
            "GGG" to "AD",
            "GGGG" to "Anno Domini",
            "GGGGG" to "A"
        ).forEach { (pattern, value) ->
            val message = "pattern = $pattern value = $value\n"

            val parsed = DateTimeParser(pattern).parse(value, en_US_parserSettings)
            assertEquals(1, parsed.propertyCount, message)
            assertEquals(1, parsed[DateProperty.Era], message)

            assertEquals(value, DateTimeFormatter(pattern).format(year, en_US_formatterSettings), message)
        }
    }

    @Test
    fun `throws an exception when era letter count is invalid`() {
        expectFailsWith<IllegalArgumentException>("GGGGGG")
    }

    @Test
    fun `parses and formats year of era correctly`() {
        val year = Year(2020)

        listOf(
            "y" to "2020",
            "yy" to "20",
            "yyy" to "2020",
            "yyyy" to "2020",
            "yyyyy" to "02020",
            "yyyyyyyyyyyyyyyyyyy" to "0000000000000002020"
        ).forEach { (pattern, value) ->
            val message = "pattern = $pattern value = $value\n"

            val parsed = DateTimeParser(pattern).parse(value, en_US_parserSettings)
            assertEquals(1, parsed.propertyCount, message)
            assertEquals(2020, parsed[DateProperty.YearOfEra], message)

            assertEquals(value, DateTimeFormatter(pattern).format(year, en_US_formatterSettings), message)
        }
    }

    @Test
    fun `throws an exception when year of era letter count would overflow`() {
        expectFailsWith<IllegalArgumentException>("yyyyyyyyyyyyyyyyyyyy")
    }

    @Test
    fun `parses and formats week-based year correctly`() {
        listOf(
            "Y" to "2020",
            "YY" to "20",
            "YYY" to "2020",
            "YYYY" to "2020",
            "YYYYY" to "02020",
            "YYYYYYYYYYYYYYYYYYY" to "0000000000000002020"
        ).forEach { (pattern, value) ->
            val message = "pattern = $pattern value = $value\n"

            val parsed = DateTimeParser(pattern).parse(value, en_US_parserSettings)
            assertEquals(1, parsed.propertyCount, message)
            assertEquals(2020, parsed[WeekProperty.LocalizedWeekBasedYear], message)

            assertEquals(value, DateTimeFormatter(pattern).format(zonedDateTime, en_US_formatterSettings), message)
        }
    }

    @Test
    fun `throws an exception when week-based year letter count would overflow`() {
        expectFailsWith<IllegalArgumentException>("YYYYYYYYYYYYYYYYYYYY")
    }

    @Test
    fun `parses and formats positive year correctly`() {
        val year = Year(2020)

        listOf(
            "u" to "2020",
            "uu" to "2020",
            "uuu" to "2020",
            "uuuu" to "2020",
            "uuuuu" to "02020",
            "uuuuuuuuuuuuuuuuuuu" to "0000000000000002020"
        ).forEach { (pattern, value) ->
            val message = "pattern = $pattern value = $value\n"

            val parsed = DateTimeParser(pattern).parse(value, en_US_parserSettings)
            assertEquals(1, parsed.propertyCount, message)
            assertEquals(2020, parsed[DateProperty.Year], message)

            assertEquals(value, DateTimeFormatter(pattern).format(year, en_US_formatterSettings), message)
        }
    }

    @Test
    fun `parses and formats negative year correctly`() {
        val year = Year(-123)

        listOf(
            "u" to "-123",
            "uu" to "-123",
            "uuu" to "-123",
            "uuuu" to "-0123",
            "uuuuu" to "-00123",
            "uuuuuuuuuuuuuuuuuuu" to "-0000000000000000123"
        ).forEach { (pattern, value) ->
            val message = "pattern = $pattern value = $value\n"

            val parsed = DateTimeParser(pattern).parse(value, en_US_parserSettings)
            assertEquals(1, parsed.propertyCount, message)
            assertEquals(-123, parsed[DateProperty.Year], message)

            assertEquals(value, DateTimeFormatter(pattern).format(year, en_US_formatterSettings), message)
        }
    }

    @Test
    fun `throws an exception when year letter count would overflow`() {
        expectFailsWith<IllegalArgumentException>("uuuuuuuuuuuuuuuuuuuu")
    }

    @Test
    fun `parses and formats format month correctly`() {
        listOf(
            "M" to "1",
            "MM" to "01",
            "MMM" to "Jan (SHORT)",
            "MMMM" to "January (FULL)",
            "MMMMM" to "J (NARROW)"
        ).forEach { (pattern, value) ->
            val message = "pattern = $pattern value = $value\n"

            if (value == "J (NARROW)") {
                // Unable to disambiguate between January, June, and July
                assertFailsWith<TemporalParseException>(message) {
                    DateTimeParser(pattern).parse(value, en_US_parserSettings)
                }
            } else {
                val parsed = DateTimeParser(pattern).parse(value, en_US_parserSettings)
                assertEquals(1, parsed.propertyCount, message)
                assertEquals(1, parsed[DateProperty.MonthOfYear], message)
            }

            assertEquals(value, DateTimeFormatter(pattern).format(Month.JANUARY, en_US_formatterSettings), message)
        }
    }

    @Test
    fun `throws an exception when format month letter count is invalid`() {
        expectFailsWith<IllegalArgumentException>("MMMMMM")
    }

    @Test
    fun `parses and formats standalone month correctly`() {
        listOf(
            "L" to "1",
            "LL" to "01",
            "LLL" to "Jan (SHORT_STANDALONE)",
            "LLLL" to "January (FULL_STANDALONE)",
            "LLLLL" to "J (NARROW_STANDALONE)"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(Month.JANUARY, en_US_formatterSettings))
        }
    }

    @Test
    fun `throws an exception when standalone month letter count is invalid`() {
        expectFailsWith<IllegalArgumentException>("LLLLLL")
    }

    @Test
    fun `parses and formats week of week-based year correctly`() {
        listOf(
            ParserFormatterTestData(pattern = "w", numericValue = 12, textValue = "12", locale = en_US),
            ParserFormatterTestData(pattern = "ww", numericValue = 12, textValue = "12", locale = en_US),
            ParserFormatterTestData(pattern = "w", numericValue = 11, textValue = "11", locale = de_DE),
            ParserFormatterTestData(pattern = "ww", numericValue = 11, textValue = "11", locale = de_DE),
        ).forEach { (pattern, numericValue, textValue, locale, message) ->
            val parsed = DateTimeParser(pattern).parse(textValue, parserSettings(locale))
            assertEquals(1, parsed.propertyCount, message)
            assertEquals(numericValue, parsed[WeekProperty.LocalizedWeekOfWeekBasedYear], message)

            assertEquals(
                textValue,
                DateTimeFormatter(pattern).format(zonedDateTime, formatterSettings(locale)),
                message
            )
        }
    }

    @Test
    fun `throws an exception when week of week-based year letter count is invalid`() {
        expectFailsWith<IllegalArgumentException>("www")
    }

    @Test
    fun `parses and formats week of month correctly`() {
        listOf(
            ParserFormatterTestData(pattern = "W", numericValue = 3, textValue = "3", locale = en_US),
            ParserFormatterTestData(pattern = "WW", numericValue = 3, textValue = "03", locale = en_US),
            ParserFormatterTestData(pattern = "W", numericValue = 2, textValue = "2", locale = de_DE),
            ParserFormatterTestData(pattern = "WW", numericValue = 2, textValue = "02", locale = de_DE),
        ).forEach { (pattern, numericValue, textValue, locale, message) ->
            val parsed = DateTimeParser(pattern).parse(textValue, parserSettings(locale))
            assertEquals(1, parsed.propertyCount, message)
            assertEquals(numericValue, parsed[WeekProperty.LocalizedWeekOfMonth], message)

            assertEquals(
                textValue,
                DateTimeFormatter(pattern).format(zonedDateTime, formatterSettings(locale)),
                message
            )
        }
    }

    @Test
    fun `throws an exception when week of month letter count is invalid`() {
        expectFailsWith<IllegalArgumentException>("WWW")
    }

    @Test
    fun `parses and formats day of month correctly`() {
        listOf(
            "d" to "15",
            "dd" to "15"
        ).forEach { (pattern, value) ->
            val message = "pattern = $pattern value = $value\n"

            val parsed = DateTimeParser(pattern).parse(value, en_US_parserSettings)
            assertEquals(1, parsed.propertyCount, message)
            assertEquals(15, parsed[DateProperty.DayOfMonth], message)

            assertEquals(value, DateTimeFormatter(pattern).format(zonedDateTime, en_US_formatterSettings), message)
        }
    }

    @Test
    fun `throws an exception when day of month letter count is invalid`() {
        expectFailsWith<IllegalArgumentException>("ddd")
    }

    @Test
    fun `parses and formats day of year correctly`() {
        listOf(
            "D" to "75",
            "DD" to "75",
            "DDD" to "075"
        ).forEach { (pattern, value) ->
            val message = "pattern = $pattern value = $value\n"

            val parsed = DateTimeParser(pattern).parse(value, en_US_parserSettings)
            assertEquals(1, parsed.propertyCount, message)
            assertEquals(75, parsed[DateProperty.DayOfYear], message)

            assertEquals(value, DateTimeFormatter(pattern).format(zonedDateTime, en_US_formatterSettings), message)
        }
    }

    @Test
    fun `throws an exception when day of year letter count is invalid`() {
        expectFailsWith<IllegalArgumentException>("DDDD")
    }

    @Test
    fun `parses and formats day of week in month correctly`() {
        // FIXME: Test parser here too
        assertEquals("1", DateTimeFormatter("F").format(zonedDateTime.startOfMonth, en_US_formatterSettings))
        assertEquals("5", DateTimeFormatter("F").format(zonedDateTime.endOfMonth, en_US_formatterSettings))
        assertEquals("3", DateTimeFormatter("F").format(zonedDateTime, en_US_formatterSettings))
    }

    @Test
    fun `throws an exception when day of week in month letter count is invalid`() {
        expectFailsWith<IllegalArgumentException>(
            "FF",
            "FFF",
            "FFFF",
            "FFFFF",
            "FFFFFF"
        )
    }

    @Test
    fun `parses and formats format day of week name correctly`() {
        listOf(
            "E" to "Sun (SHORT)",
            "EE" to "Sun (SHORT)",
            "EEE" to "Sun (SHORT)",
            "EEEE" to "Sunday (FULL)",
            "EEEEE" to "S (NARROW)"
        ).forEach { (pattern, value) ->
            val message = "pattern = $pattern value = $value\n"

            if (value == "S (NARROW)") {
                // Unable to disambiguate between Saturday and Sunday
                assertFailsWith<TemporalParseException>(message) {
                    DateTimeParser(pattern).parse(value, en_US_parserSettings)
                }
            } else {
                val parsed = DateTimeParser(pattern).parse(value, en_US_parserSettings)
                assertEquals(1, parsed.propertyCount, message)
                assertEquals(7, parsed[DateProperty.DayOfWeek], message)
            }

            assertEquals(value, DateTimeFormatter(pattern).format(zonedDateTime, en_US_formatterSettings), message)
        }
    }

    @Test
    fun `throws an exception when format day of week letter count is invalid`() {
        expectFailsWith<IllegalArgumentException>("EEEEEE")
    }

    @Test
    fun `parses and formats format day of week name or number correctly`() {
        listOf(
            ParserFormatterTestData(pattern = "e", numericValue = 1, textValue = "1", locale = en_US),
            ParserFormatterTestData(pattern = "ee", numericValue = 1, textValue = "01", locale = en_US),
            ParserFormatterTestData(pattern = "eee", numericValue = 7, textValue = "Sun (SHORT)", locale = en_US),
            ParserFormatterTestData(pattern = "eeee", numericValue = 7, textValue = "Sunday (FULL)", locale = en_US),
            ParserFormatterTestData(pattern = "eeeee", numericValue = 7, textValue = "S (NARROW)", locale = en_US),
            ParserFormatterTestData(pattern = "e", numericValue = 7, textValue = "7", locale = de_DE),
            ParserFormatterTestData(pattern = "ee", numericValue = 7, textValue = "07", locale = de_DE),
            ParserFormatterTestData(pattern = "eee", numericValue = 7, textValue = "Sun (SHORT)", locale = de_DE),
            ParserFormatterTestData(pattern = "eeee", numericValue = 7, textValue = "Sunday (FULL)", locale = de_DE),
            ParserFormatterTestData(pattern = "eeeee", numericValue = 7, textValue = "S (NARROW)", locale = de_DE),
        ).forEach { (pattern, numericValue, textValue, locale, message) ->
            if (textValue == "S (NARROW)") {
                // Unable to disambiguate between Saturday and Sunday
                assertFailsWith<TemporalParseException>(message) {
                    DateTimeParser(pattern).parse(textValue, parserSettings(locale))
                }
            } else {
                val parsed = DateTimeParser(pattern).parse(textValue, parserSettings(locale))
                assertEquals(1, parsed.propertyCount, message)

                if (pattern.count() < 3) {
                    assertEquals(numericValue, parsed[WeekProperty.LocalizedDayOfWeek], message)
                } else {
                    assertEquals(numericValue, parsed[DateProperty.DayOfWeek], message)
                }
            }

            assertEquals(
                textValue,
                DateTimeFormatter(pattern).format(zonedDateTime, formatterSettings(locale)),
                message
            )
        }
    }

    @Test
    fun `throws an exception when format day of week name or number letter count is invalid`() {
        expectFailsWith<IllegalArgumentException>("eeeeee")
    }

    @Test
    fun `parses and formats standalone day of week name or number correctly in en_US locale`() {
        listOf(
            ParserFormatterTestData(pattern = "c", numericValue = 1, textValue = "1", locale = en_US),
            ParserFormatterTestData(pattern = "cc", numericValue = 1, textValue = "1", locale = en_US),
            ParserFormatterTestData(
                pattern = "ccc",
                numericValue = 7,
                textValue = "Sun (SHORT_STANDALONE)",
                locale = en_US
            ),
            ParserFormatterTestData(
                pattern = "cccc",
                numericValue = 7,
                textValue = "Sunday (FULL_STANDALONE)",
                locale = en_US
            ),
            ParserFormatterTestData(
                pattern = "ccccc",
                numericValue = 7,
                textValue = "S (NARROW_STANDALONE)",
                locale = en_US
            ),
            ParserFormatterTestData(pattern = "c", numericValue = 7, textValue = "7", locale = de_DE),
            ParserFormatterTestData(pattern = "cc", numericValue = 7, textValue = "7", locale = de_DE),
            ParserFormatterTestData(
                pattern = "ccc",
                numericValue = 7,
                textValue = "Sun (SHORT_STANDALONE)",
                locale = de_DE
            ),
            ParserFormatterTestData(
                pattern = "cccc",
                numericValue = 7,
                textValue = "Sunday (FULL_STANDALONE)",
                locale = de_DE
            ),
            ParserFormatterTestData(
                pattern = "ccccc",
                numericValue = 7,
                textValue = "S (NARROW_STANDALONE)",
                locale = de_DE
            ),
        ).forEach { (pattern, numericValue, textValue, locale, message) ->
            if (textValue == "S (NARROW_STANDALONE)") {
                // Unable to disambiguate between Saturday and Sunday
                assertFailsWith<TemporalParseException>(message) {
                    DateTimeParser(pattern).parse(textValue, parserSettings(locale))
                }
            } else {
                val parsed = DateTimeParser(pattern).parse(textValue, parserSettings(locale))
                assertEquals(1, parsed.propertyCount, message)

                if (pattern.count() < 3) {
                    assertEquals(numericValue, parsed[WeekProperty.LocalizedDayOfWeek], message)
                } else {
                    assertEquals(numericValue, parsed[DateProperty.DayOfWeek], message)
                }
            }

            assertEquals(
                textValue,
                DateTimeFormatter(pattern).format(zonedDateTime, formatterSettings(locale)),
                message
            )
        }
    }

    @Test
    fun `throws an exception when standalone day of week name or number letter count is invalid`() {
        expectFailsWith<IllegalArgumentException>("cccccc")
    }

    @Test
    fun `parses and formats am-pm correctly`() {
        listOf(
            "a" to "PM",
            "aa" to "PM",
            "aaa" to "PM"
        ).forEach { (pattern, value) ->
            val message = "pattern = $pattern value = $value\n"

            val parsed = DateTimeParser(pattern).parse(value, en_US_parserSettings)
            assertEquals(1, parsed.propertyCount, message)
            assertEquals(1, parsed[TimeProperty.AmPmOfDay], message)

            assertEquals(value, DateTimeFormatter(pattern).format(zonedDateTime, en_US_formatterSettings), message)
        }
    }

    @Test
    fun `throws an exception when am-pm letter count is invalid`() {
        expectFailsWith<IllegalArgumentException>("aaaa")
    }

    @Test
    fun `parses and formats clock hour of am-pm correctly`() {
        listOf(
            "h" to "1",
            "hh" to "01"
        ).forEach { (pattern, value) ->
            val message = "pattern = $pattern value = $value\n"

            val parsed = DateTimeParser(pattern).parse(value, en_US_parserSettings)
            assertEquals(1, parsed.propertyCount, message)
            assertEquals(1, parsed[TimeProperty.ClockHourOfAmPm], message)

            assertEquals(value, DateTimeFormatter(pattern).format(zonedDateTime, en_US_formatterSettings), message)
        }
    }

    @Test
    fun `throws an exception when clock hour of am-pm letter count is invalid`() {
        expectFailsWith<IllegalArgumentException>("hhh")
    }

    @Test
    fun `parses and formats hour of day correctly`() {
        listOf(
            "H" to "13",
            "HH" to "13"
        ).forEach { (pattern, value) ->
            val message = "pattern = $pattern value = $value\n"

            val parsed = DateTimeParser(pattern).parse(value, en_US_parserSettings)
            assertEquals(1, parsed.propertyCount, message)
            assertEquals(13, parsed[TimeProperty.HourOfDay], message)

            assertEquals(value, DateTimeFormatter(pattern).format(zonedDateTime, en_US_formatterSettings), message)
        }
    }

    @Test
    fun `throws an exception when hour of day letter count is invalid`() {
        expectFailsWith<IllegalArgumentException>("HHH")
    }

    @Test
    fun `parses and formats hour of am-pm correctly`() {
        listOf(
            "K" to "1",
            "KK" to "01"
        ).forEach { (pattern, value) ->
            val message = "pattern = $pattern value = $value\n"

            val parsed = DateTimeParser(pattern).parse(value, en_US_parserSettings)
            assertEquals(1, parsed.propertyCount, message)
            assertEquals(1, parsed[TimeProperty.HourOfAmPm], message)

            assertEquals(value, DateTimeFormatter(pattern).format(zonedDateTime, en_US_formatterSettings), message)
        }
    }

    @Test
    fun `throws an exception when hour of am-pm letter count is invalid`() {
        expectFailsWith<IllegalArgumentException>("KKK")
    }

    @Test
    fun `parses and formats clock hour of day correctly`() {
        listOf(
            "k" to "13",
            "kk" to "13"
        ).forEach { (pattern, value) ->
            val message = "pattern = $pattern value = $value\n"

            val parsed = DateTimeParser(pattern).parse(value, en_US_parserSettings)
            assertEquals(1, parsed.propertyCount, message)
            assertEquals(13, parsed[TimeProperty.ClockHourOfDay], message)

            assertEquals(value, DateTimeFormatter(pattern).format(zonedDateTime, en_US_formatterSettings), message)
        }
    }

    @Test
    fun `throws an exception when clock hour of day letter count is invalid`() {
        expectFailsWith<IllegalArgumentException>("kkk")
    }

    @Test
    fun `parses and formats minute of hour correctly`() {
        listOf(
            "m" to "30",
            "mm" to "30"
        ).forEach { (pattern, value) ->
            val message = "pattern = $pattern value = $value\n"

            val parsed = DateTimeParser(pattern).parse(value, en_US_parserSettings)
            assertEquals(1, parsed.propertyCount, message)
            assertEquals(30, parsed[TimeProperty.MinuteOfHour], message)

            assertEquals(value, DateTimeFormatter(pattern).format(zonedDateTime, en_US_formatterSettings), message)
        }
    }

    @Test
    fun `throws an exception when minute of hour letter count is invalid`() {
        expectFailsWith<IllegalArgumentException>("mmm")
    }

    @Test
    fun `parses and formats fractional second correctly`() {
        listOf(
            ParserFormatterTestData(pattern = "S", numericValue = 0, textValue = "0"),
            ParserFormatterTestData(pattern = "SS", numericValue = 0, textValue = "00"),
            ParserFormatterTestData(pattern = "SSS", numericValue = 2_000_000, textValue = "002"),
            ParserFormatterTestData(pattern = "SSSS", numericValue = 2_900_000, textValue = "0029"),
            ParserFormatterTestData(pattern = "SSSSS", numericValue = 2_990_000, textValue = "00299"),
            ParserFormatterTestData(pattern = "SSSSSS", numericValue = 2_999_000, textValue = "002999"),
            ParserFormatterTestData(pattern = "SSSSSSS", numericValue = 2_999_900, textValue = "0029999"),
            ParserFormatterTestData(pattern = "SSSSSSSS", numericValue = 2_999_990, textValue = "00299999"),
            ParserFormatterTestData(pattern = "SSSSSSSSS", numericValue = 2_999_999, textValue = "002999999")
        ).forEach { (pattern, numericValue, textValue, locale, message) ->
            val parsed = DateTimeParser(pattern).parse(textValue, parserSettings(locale))
            assertEquals(1, parsed.propertyCount, message)
            assertEquals(numericValue, parsed[TimeProperty.NanosecondOfSecond], message)

            assertEquals(
                textValue,
                DateTimeFormatter(pattern).format(zonedDateTime, formatterSettings(locale)),
                message
            )
        }
    }

    @Test
    fun `throws an exception when fractional second letter count exceeds nanosecond precision`() {
        expectFailsWith<IllegalArgumentException>("SSSSSSSSSS")
    }

    @Test
    fun `parses and formats millisecond of day correctly`() {
        val time = Time(0, 0, 0, 21_000_000)

        listOf(
            "A" to "21",
            "AA" to "21",
            "AAA" to "021",
            "AAAAAAAAAAAAAAAAAAA" to "0000000000000000021"
        ).forEach { (pattern, value) ->
            val message = "pattern = $pattern value = $value\n"

            val parsed = DateTimeParser(pattern).parse(value, en_US_parserSettings)
            assertEquals(1, parsed.propertyCount, message)
            assertEquals(21, parsed[TimeProperty.MillisecondOfDay], message)

            assertEquals(value, DateTimeFormatter(pattern).format(time, en_US_formatterSettings), message)
        }
    }

    @Test
    fun `throws an exception when millisecond of day letter count overflows a Long`() {
        expectFailsWith<IllegalArgumentException>("AAAAAAAAAAAAAAAAAAAA")
    }

    @Test
    fun `parses and formats specific non-location zone correctly`() {
        listOf(
            "z" to "EDT",
            "zz" to "EDT",
            "zzz" to "EDT",
            "zzzz" to "Eastern Daylight Time"
        ).forEach { (pattern, value) ->
            val message = "pattern = $pattern value = $value\n"

            val parsed = DateTimeParser(pattern).parse(value, en_US_parserSettings)
            assertEquals(1, parsed.propertyCount, message)

            // Multiple zones map to Eastern Time. By default, the first one alphabetically is picked.
            assertEquals("America/Detroit", parsed[TimeZoneProperty.Id], message)

            assertEquals(value, DateTimeFormatter(pattern).format(zonedDateTime, en_US_formatterSettings), message)
        }
    }

    @Test
    fun `parses and formats specific non-location fixed offset zone correctly`() {
        listOf(
            "z" to "GMT-4",
            "zz" to "GMT-4",
            "zzz" to "GMT-4",
            "zzzz" to "GMT-04:00"
        ).forEach { (pattern, value) ->
            val message = "pattern = $pattern value = $value\n"

            val parsed = DateTimeParser(pattern).parse(value, en_US_parserSettings).toUtcOffset()
            assertEquals(-14400, parsed?.totalSeconds?.value)
            // FIXME!
            //assertEquals(1, parsed.propertyCount, message)
            //assertEquals("America/New_York", parsed[TimeZoneProperty.Id], message)

            assertEquals(
                value,
                DateTimeFormatter(pattern).format(zonedDateTime.withFixedOffsetZone(), en_US_formatterSettings),
                message
            )
        }
    }

    @Test
    fun `throws an exception when specific non-location zone letter count is invalid`() {
        expectFailsWith<IllegalArgumentException>("zzzzz")
    }

    @Test
    fun `parses and formats Z correctly with zero offset`() {
        listOf(
            "Z" to "+0000",
            "ZZ" to "+0000",
            "ZZZ" to "+0000",
            "ZZZZ" to "GMT",
            "ZZZZZ" to "Z"
        ).forEach { (pattern, value) ->
            assertEquals(
                value,
                DateTimeFormatter(pattern).format(
                    zonedDateTime.adjustedTo(TimeZone("Etc/UTC")),
                    en_US_formatterSettings
                )
            )
        }
    }

    @Test
    fun `parses and formats Z correctly with non-zero offset`() {
        listOf(
            "Z" to "-0400",
            "ZZ" to "-0400",
            "ZZZ" to "-0400",
            "ZZZZ" to "GMT-04:00",
            "ZZZZZ" to "-04:00"
        ).forEach { (pattern, value) ->
            assertEquals(value, DateTimeFormatter(pattern).format(zonedDateTime, en_US_formatterSettings))
        }
    }

    @Test
    fun `throws an exception when Z is used too many times`() {
        expectFailsWith<IllegalArgumentException>("ZZZZZZ")
    }

    @Test
    fun `parses and formats generic non-location zone correctly`() {
        listOf(
            "v" to "ET",
            "vvvv" to "Eastern Time"
        ).forEach { (pattern, value) ->
            assertEquals(value, DateTimeFormatter(pattern).format(zonedDateTime, en_US_formatterSettings))
        }
    }

    @Test
    fun `throws an exception when generic non-location zone letter count is invalid`() {
        expectFailsWith<IllegalArgumentException>("vv", "vvv", "vvvvv")
    }

    @Test
    fun `parses time zone ID correctly`() {
        val parsed1 = DateTimeParser("VV").parse("America/New_York", en_US_parserSettings)
        assertEquals(1, parsed1.propertyCount)
        assertEquals("America/New_York", parsed1[TimeZoneProperty.Id])

        todo {
            val parsed2 = DateTimeParser("VV").parse("-04:00", en_US_parserSettings)
            assertEquals(1, parsed2.propertyCount)
            assertEquals("-04:00", parsed2[TimeZoneProperty.Id])
        }
    }

    @Test
    fun `formats time zone ID correctly`() {
        assertEquals(
            "America/New_York",
            DateTimeFormatter("VV").format(zonedDateTime, en_US_formatterSettings)
        )

        assertEquals(
            "-04:00",
            DateTimeFormatter("VV").format(
                zonedDateTime.withFixedOffsetZone(),
                en_US_formatterSettings
            )
        )
    }

    @Test
    fun `throws an exception when time zone ID letter count is invalid`() {
        expectFailsWith<IllegalArgumentException>("V", "VVV", "VVVV")
    }

    @Test
    fun `parses and formats offset with Z correctly`() {
        val offset = UtcOffset((-4).hours, 0.minutes, (-1).seconds)

        listOf(
            "X" to "-04",
            "XX" to "-0400",
            "XXX" to "-04:00",
            "XXXX" to "-040001",
            "XXXXX" to "-04:00:01"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(offset, en_US_formatterSettings))
        }

        listOf(
            "X",
            "XX",
            "XXX",
            "XXXX",
            "XXXXX"
        ).forEach { pattern ->
            assertEquals(
                "Z",
                DateTimeFormatter(pattern).format(UtcOffset.ZERO, en_US_formatterSettings)
            )
        }
    }

    @Test
    fun `throws an exception when offset with Z letter count is invalid`() {
        expectFailsWith<IllegalArgumentException>("XXXXXX")
    }

    @Test
    fun `parses and formats offset without Z correctly`() {
        val offset = UtcOffset((-4).hours, 0.minutes, (-1).seconds)

        listOf(
            "x" to "-04",
            "xx" to "-0400",
            "xxx" to "-04:00",
            "xxxx" to "-040001",
            "xxxxx" to "-04:00:01"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(offset, en_US_formatterSettings))
        }

        listOf(
            "x" to "+00",
            "xx" to "+0000",
            "xxx" to "+00:00",
            "xxxx" to "+0000",
            "xxxxx" to "+00:00"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(UtcOffset.ZERO, en_US_formatterSettings))
        }
    }

    @Test
    fun `throws an exception when offset without Z letter count is invalid`() {
        expectFailsWith<IllegalArgumentException>("xxxxxx")
    }

    @Test
    fun `parses and formats localized offset of zero correctly`() {
        listOf(
            "O" to "GMT",
            "OOOO" to "GMT"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(UtcOffset.ZERO, en_US_formatterSettings))
        }
    }

    @Test
    fun `parses and formats localized offset with only hours correctly`() {
        val offset = UtcOffset(4.hours)

        listOf(
            "O" to "GMT+4",
            "OOOO" to "GMT+04:00"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(offset, en_US_formatterSettings))
        }
    }

    @Test
    fun `parses and formats localized offset with hours and minutes correctly`() {
        val offset = UtcOffset((-17).hours, (-30).minutes)

        listOf(
            "O" to "GMT-17:30",
            "OOOO" to "GMT-17:30"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(offset, en_US_formatterSettings))
        }
    }

    @Test
    fun `parses and formats localized offset with hours, minute, and seconds correctly`() {
        val offset = UtcOffset((-4).hours, 0.minutes, (-1).seconds)

        listOf(
            "O" to "GMT-4:00:01",
            "OOOO" to "GMT-04:00:01"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(offset, en_US_formatterSettings))
        }
    }

    @Test
    fun `throws an exception when localized offset letter count is invalid`() {
        expectFailsWith<IllegalArgumentException>("OO", "OOO", "OOOOO")
    }

    @Test
    fun `parses a full ISO pattern correctly`() {
        val parser = DateTimeParser("uuuu-MM-dd'T'HH:mm:ss.SSSxxx[VV]")

        val result = parser.parse("2020-03-15T13:30:01.002-04:00[America/New_York]", en_US_parserSettings)
        assertEquals(11, result.propertyCount)
        assertEquals(2020, result[DateProperty.Year])
        assertEquals(3, result[DateProperty.MonthOfYear])
        assertEquals(15, result[DateProperty.DayOfMonth])
        assertEquals(13, result[TimeProperty.HourOfDay])
        assertEquals(30, result[TimeProperty.MinuteOfHour])
        assertEquals(1, result[TimeProperty.SecondOfMinute])
        assertEquals(2_000_000, result[TimeProperty.NanosecondOfSecond])
        assertEquals(-1, result[UtcOffsetProperty.Sign])
        assertEquals(4, result[UtcOffsetProperty.Hours])
        assertEquals(0, result[UtcOffsetProperty.Minutes])
        assertEquals("America/New_York", result[TimeZoneProperty.Id])
    }

    @Test
    fun `formats a full ISO pattern correctly`() {
        val formatter = DateTimeFormatter("uuuu-MM-dd'T'HH:mm:ss.SSSxxx[VV]")

        assertEquals(
            "2020-03-15T13:30:01.002-04:00[America/New_York]",
            formatter.format(zonedDateTime, en_US_formatterSettings)
        )
    }

    private inline fun <reified T : Throwable> expectFailsWith(vararg patterns: String) {
        for (pattern in patterns) {
            assertFailsWith<T>("DateTimeFormatter") { DateTimeFormatter(pattern) }
            assertFailsWith<T>("DateTimeParser") { DateTimeParser(pattern) }
        }
    }
}
