package io.islandtime.format

import io.islandtime.*
import io.islandtime.base.Temporal
import io.islandtime.locale.localeOf
import io.islandtime.measures.hours
import io.islandtime.measures.minutes
import io.islandtime.measures.seconds
import io.islandtime.test.AbstractIslandTimeTest
import io.islandtime.test.TestDateTimeTextProvider
import io.islandtime.test.TestTimeZoneTextProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.todo

@Suppress("PrivatePropertyName")
class DateTimePatternTest : AbstractIslandTimeTest(
    testDateTimeTextProvider = TestDateTimeTextProvider,
    testTimeZoneTextProvider = TestTimeZoneTextProvider
) {
    private val emptyTemporal = object : Temporal {}

    private val zonedDateTime =
        Date(2020, Month.MARCH, 15) at
        Time(13, 30, 1, 2_999_999) at
        TimeZone("America/New_York")

    private val en_US_settings = TemporalFormatter.Settings(locale = localeOf("en-US"))

    @Test
    fun `parses empty patterns`() {
        assertEquals("", DateTimeFormatter("").format(emptyTemporal, en_US_settings))
    }

    @Test
    fun `parses non-letter characters without escaping`() {
        listOf(
            "." to ".",
            ", " to ", ",
            "\\/?!#[]" to "\\/?!#[]"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(emptyTemporal, en_US_settings))
        }
    }

    @Test
    fun `parses escaped literals`() {
        listOf(
            "'A'" to "A",
            "'A' " to "A ",
            "'A much longer literal'." to "A much longer literal."
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(emptyTemporal, en_US_settings))
        }
    }

    @Test
    fun `parses literals with escaped apostrophe`() {
        listOf(
            "''." to "'.",
            "'''t'" to "'t",
            "''''" to "'",
            "''''''" to "''",
            "'''' " to "' "
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(emptyTemporal, en_US_settings))
        }
    }

    @Test
    fun `throws an exception when the pattern ends with an unterminated literal`() {
        listOf(
            "'T",
            "'T ",
            "'''''''"
        ).forEach {
            assertFailsWith<IllegalArgumentException> {
                DateTimeFormatter(it).format(emptyTemporal, en_US_settings)
            }
        }
    }

    @Test
    fun `formats era correctly`() {
        val year = Year(2020)

        listOf(
            "G" to "AD",
            "GG" to "AD",
            "GGG" to "AD",
            "GGGG" to "Anno Domini"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(year, en_US_settings))
        }
    }

    @Test
    fun `throws an exception when era letter count is invalid`() {
        assertFailsWith<IllegalArgumentException> {
            DateTimeFormatter("GGGGGG").format(Year(2020), en_US_settings)
        }
    }

    @Test
    fun `formats year of era correctly`() {
        val year = Year(2020)

        listOf(
            "y" to "2020",
            "yy" to "20",
            "yyy" to "2020",
            "yyyy" to "2020",
            "yyyyy" to "02020",
            "yyyyyyyyyyyyyyyyyyy" to "0000000000000002020"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(year, en_US_settings))
        }
    }

    @Test
    fun `throws an exception when year of era letter count would overflow`() {
        assertFailsWith<IllegalArgumentException> {
            DateTimeFormatter("yyyyyyyyyyyyyyyyyyyy").format(Year(2020), en_US_settings)
        }
    }

    @Test
    fun `formats positive year correctly`() {
        val year = Year(2020)

        listOf(
            "u" to "2020",
            "uu" to "2020",
            "uuu" to "2020",
            "uuuu" to "2020",
            "uuuuu" to "02020",
            "uuuuuuuuuuuuuuuuuuu" to "0000000000000002020"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(year, en_US_settings))
        }
    }

    @Test
    fun `formats negative year correctly`() {
        val year = Year(-123)

        listOf(
            "u" to "-123",
            "uu" to "-123",
            "uuu" to "-123",
            "uuuu" to "-0123",
            "uuuuu" to "-00123",
            "uuuuuuuuuuuuuuuuuuu" to "-0000000000000000123"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(year, en_US_settings))
        }
    }

    @Test
    fun `throws an exception when year letter count would overflow`() {
        assertFailsWith<IllegalArgumentException> {
            DateTimeFormatter("uuuuuuuuuuuuuuuuuuuu").format(Year(2020), en_US_settings)
        }
    }

    @Test
    fun `formats format month correctly`() {
        listOf(
            "M" to "1",
            "MM" to "01",
            "MMM" to "Jan (SHORT)",
            "MMMM" to "January (FULL)",
            "MMMMM" to "J (NARROW)"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(Month.JANUARY, en_US_settings))
        }
    }

    @Test
    fun `throws an exception when format month letter count is invalid`() {
        assertFailsWith<IllegalArgumentException> {
            DateTimeFormatter("MMMMMM").format(Month.APRIL, en_US_settings)
        }
    }

    @Test
    fun `formats standalone month correctly`() {
        listOf(
            "L" to "1",
            "LL" to "01",
            "LLL" to "Jan (SHORT_STANDALONE)",
            "LLLL" to "January (FULL_STANDALONE)",
            "LLLLL" to "J (NARROW_STANDALONE)"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(Month.JANUARY, en_US_settings))
        }
    }

    @Test
    fun `throws an exception when standalone month letter count is invalid`() {
        assertFailsWith<IllegalArgumentException> {
            DateTimeFormatter("LLLLLL").format(Month.APRIL, en_US_settings)
        }
    }

    @Test
    fun `formats day of month correctly`() {
        listOf(
            "d" to "15",
            "dd" to "15"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(zonedDateTime, en_US_settings))
        }
    }

    @Test
    fun `throws an exception when day of month letter count is invalid`() {
        assertFailsWith<IllegalArgumentException> {
            DateTimeFormatter("ddd").format(zonedDateTime, en_US_settings)
        }
    }

    @Test
    fun `formats day of year correctly`() {
        listOf(
            "D" to "75",
            "DD" to "75",
            "DDD" to "075"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(zonedDateTime, en_US_settings))
        }
    }

    @Test
    fun `throws an exception when day of year letter count is invalid`() {
        assertFailsWith<IllegalArgumentException> {
            DateTimeFormatter("DDDD").format(zonedDateTime, en_US_settings)
        }
    }

    @Test
    fun `formats format day of week name correctly`() {
        listOf(
            "E" to "Sun (SHORT)",
            "EE" to "Sun (SHORT)",
            "EEE" to "Sun (SHORT)",
            "EEEE" to "Sunday (FULL)",
            "EEEEE" to "S (NARROW)"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(zonedDateTime, en_US_settings))
        }
    }

    @Test
    fun `throws an exception when format day of week letter count is invalid`() {
        assertFailsWith<IllegalArgumentException> {
            DateTimeFormatter("EEEEEE").format(zonedDateTime, en_US_settings)
        }
    }

    @Test
    fun `formats format day of week name or number correctly`() {
        listOf(
            "e" to "7",
            "ee" to "07",
            "eee" to "Sun (SHORT)",
            "eeee" to "Sunday (FULL)",
            "eeeee" to "S (NARROW)"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(zonedDateTime, en_US_settings))
        }
    }

    @Test
    fun `throws an exception when format day of week name or number letter count is invalid`() {
        assertFailsWith<IllegalArgumentException> {
            DateTimeFormatter("eeeeee").format(zonedDateTime, en_US_settings)
        }
    }

    @Test
    fun `formats standalone day of week name or number correctly`() {
        listOf(
            "c" to "7",
            "cc" to "7",
            "ccc" to "Sun (SHORT_STANDALONE)",
            "cccc" to "Sunday (FULL_STANDALONE)",
            "ccccc" to "S (NARROW_STANDALONE)"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(zonedDateTime, en_US_settings))
        }
    }

    @Test
    fun `throws an exception when standalone day of week name or number letter count is invalid`() {
        assertFailsWith<IllegalArgumentException> {
            DateTimeFormatter("cccccc").format(zonedDateTime, en_US_settings)
        }
    }

    @Test
    fun `formats am-pm correctly`() {
        listOf(
            "a" to "PM",
            "aa" to "PM",
            "aaa" to "PM"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(zonedDateTime, en_US_settings))
        }
    }

    @Test
    fun `throws an exception when am-pm letter count is invalid`() {
        assertFailsWith<IllegalArgumentException> {
            DateTimeFormatter("aaaa").format(zonedDateTime, en_US_settings)
        }
    }

    @Test
    fun `formats clock hour of am-pm correctly`() {
        listOf(
            "h" to "1",
            "hh" to "01"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(zonedDateTime, en_US_settings))
        }
    }

    @Test
    fun `throws an exception when clock hour of am-pm letter count is invalid`() {
        assertFailsWith<IllegalArgumentException> {
            DateTimeFormatter("hhh").format(zonedDateTime, en_US_settings)
        }
    }

    @Test
    fun `formats hour of day correctly`() {
        listOf(
            "H" to "13",
            "HH" to "13"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(zonedDateTime, en_US_settings))
        }
    }

    @Test
    fun `throws an exception when hour of day letter count is invalid`() {
        assertFailsWith<IllegalArgumentException> {
            DateTimeFormatter("HHH").format(zonedDateTime, en_US_settings)
        }
    }

    @Test
    fun `formats hour of am-pm correctly`() {
        listOf(
            "K" to "1",
            "KK" to "01"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(zonedDateTime, en_US_settings))
        }
    }

    @Test
    fun `throws an exception when hour of am-pm letter count is invalid`() {
        assertFailsWith<IllegalArgumentException> {
            DateTimeFormatter("KKK").format(zonedDateTime, en_US_settings)
        }
    }

    @Test
    fun `formats clock hour of day correctly`() {
        listOf(
            "k" to "13",
            "kk" to "13"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(zonedDateTime, en_US_settings))
        }
    }

    @Test
    fun `throws an exception when clock hour of day letter count is invalid`() {
        assertFailsWith<IllegalArgumentException> {
            DateTimeFormatter("kkk").format(zonedDateTime, en_US_settings)
        }
    }

    @Test
    fun `formats minute of hour correctly`() {
        listOf(
            "m" to "30",
            "mm" to "30"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(zonedDateTime, en_US_settings))
        }
    }

    @Test
    fun `throws an exception when minute of hour letter count is invalid`() {
        assertFailsWith<IllegalArgumentException> {
            DateTimeFormatter("mmm").format(zonedDateTime, en_US_settings)
        }
    }

    @Test
    fun `formats fractional second correctly`() {
        todo {
            listOf(
                "S" to "0",
                "SS" to "00",
                "SSS" to "000",
                "SSSS" to "0000",
                "SSSSS" to "00000",
                "SSSSSS" to "000000",
                "SSSSSSS" to "0000000",
                "SSSSSSSS" to "00000000",
                "SSSSSSSSS" to "000000001"
            ).forEach { (pattern, result) ->
                assertEquals(
                    result,
                    DateTimeFormatter(pattern).format(zonedDateTime, en_US_settings)
                )
            }
        }
    }

    @Test
    fun `throws an exception when fractional second letter count exceeds nanosecond precision`() {
        todo {
            assertFailsWith<IllegalArgumentException> {
                DateTimeFormatter("SSSSSSSSSS").format(zonedDateTime, en_US_settings)
            }
        }
    }

    @Test
    fun `formats millisecond of day correctly`() {
        val time = Time(0, 0, 0, 21_000_000)

        listOf(
            "A" to "21",
            "AA" to "21",
            "AAA" to "021",
            "AAAAAAAAAAAAAAAAAAA" to "0000000000000000021"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(time, en_US_settings))
        }
    }

    @Test
    fun `throws an exception when millisecond of day letter count overflows a Long`() {
        assertFailsWith<IllegalArgumentException> {
            DateTimeFormatter("AAAAAAAAAAAAAAAAAAAA").format(zonedDateTime, en_US_settings)
        }
    }

    @Test
    fun `formats specific non-location zone correctly`() {
        listOf(
            "z" to "EDT",
            "zz" to "EDT",
            "zzz" to "EDT",
            "zzzz" to "Eastern Daylight Time"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(zonedDateTime, en_US_settings))
        }
    }

    @Test
    fun `throws an exception when specific non-location zone letter count is invalid`() {
        assertFailsWith<IllegalArgumentException> {
            DateTimeFormatter("zzzzz").format(zonedDateTime, en_US_settings)
//            DateTimePattern("zzzz").toFormatter().format()
//            DateTimeSkeleton("MMMy").toFormatter()
//            dateTimeFormatter {
//                usePattern("zzzz")
//            }.format()
        }
    }

    @Test
    fun `formats generic non-location zone correctly`() {
        listOf(
            "v" to "ET",
            "vvvv" to "Eastern Time"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(zonedDateTime, en_US_settings))
        }
    }

    @Test
    fun `throws an exception when generic non-location zone letter count is invalid`() {
        listOf("vv", "vvv", "vvvvv").forEach {
            assertFailsWith<IllegalArgumentException> {
                DateTimeFormatter(it).format(zonedDateTime, en_US_settings)
            }
        }
    }

    @Test
    fun `formats time zone ID correctly`() {
        assertEquals(
            "America/New_York",
            DateTimeFormatter("VV").format(zonedDateTime, en_US_settings)
        )

        assertEquals(
            "-04:00",
            DateTimeFormatter("VV").format(
                zonedDateTime.withFixedOffsetZone(),
                en_US_settings
            )
        )
    }

    @Test
    fun `throws an exception when time zone ID letter count is invalid`() {
        listOf("V", "VVV", "VVVV").forEach {
            assertFailsWith<IllegalArgumentException> {
                DateTimeFormatter(it).format(zonedDateTime, en_US_settings)
            }
        }
    }

    @Test
    fun `formats offset with Z correctly`() {
        val offset = UtcOffset((-4).hours, 0.minutes, (-1).seconds)

        listOf(
            "X" to "-04",
            "XX" to "-0400",
            "XXX" to "-04:00",
            "XXXX" to "-040001",
            "XXXXX" to "-04:00:01"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(offset, en_US_settings))
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
                DateTimeFormatter(pattern).format(UtcOffset.ZERO, en_US_settings)
            )
        }
    }

    @Test
    fun `throws an exception when offset with Z letter count is invalid`() {
        assertFailsWith<IllegalArgumentException> {
            DateTimeFormatter("XXXXXX").format(zonedDateTime, en_US_settings)
        }
    }

    @Test
    fun `formats offset without Z correctly`() {
        val offset = UtcOffset((-4).hours, 0.minutes, (-1).seconds)

        listOf(
            "x" to "-04",
            "xx" to "-0400",
            "xxx" to "-04:00",
            "xxxx" to "-040001",
            "xxxxx" to "-04:00:01"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(offset, en_US_settings))
        }

        listOf(
            "x" to "+00",
            "xx" to "+0000",
            "xxx" to "+00:00",
            "xxxx" to "+0000",
            "xxxxx" to "+00:00"
        ).forEach { (pattern, result) ->
            assertEquals(result, DateTimeFormatter(pattern).format(UtcOffset.ZERO, en_US_settings))
        }
    }

    @Test
    fun `throws an exception when offset without Z letter count is invalid`() {
        assertFailsWith<IllegalArgumentException> {
            DateTimeFormatter("xxxxxx").format(zonedDateTime, en_US_settings)
        }
    }

    @Test
    fun `formats a full ISO pattern correctly`() {
        val formatter = DateTimeFormatter("yyyy-MM-dd'T'HH:mm:ss.SSSxxx[VV]")

        assertEquals(
            "2020-03-15T13:30:01.002-04:00[America/New_York]",
            formatter.format(zonedDateTime, en_US_settings)
        )
    }
}