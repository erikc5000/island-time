package io.islandtime.parser

import io.islandtime.locale.localeFor
import kotlin.test.Test
import kotlin.test.assertEquals

class DateTimeParserSettingsTest {
    val en_US = localeFor("en-US")
    val de_DE = localeFor("de-DE")
    val hi_IN_u_nu_native = localeFor("hi-IN-u-nu-native")

    @Test
    fun `Locale_numberParserStyle returns a NumberParserStyle based on the locale`() {
        assertEquals(
            NumberParserStyle(
                zeroDigit = '0',
                plusSign = listOf('+'),
                minusSign = listOf('-'),
                decimalSeparator = listOf('.')
            ),
            en_US.numberParserStyle
        )

        assertEquals(
            NumberParserStyle(
                zeroDigit = '0',
                plusSign = listOf('+'),
                minusSign = listOf('-'),
                decimalSeparator = listOf(',')
            ),
            de_DE.numberParserStyle
        )

        assertEquals(
            NumberParserStyle(
                zeroDigit = '०',
                plusSign = listOf('+'),
                minusSign = listOf('-'),
                decimalSeparator = listOf('.')
            ),
            hi_IN_u_nu_native.numberParserStyle
        )
    }

    @Test
    fun `Char_toDigit() converts a character to a digit according to NumberParserStyle`() {
        assertEquals(0, '0'.toDigit(en_US.numberParserStyle))
        assertEquals(9, '9'.toDigit(en_US.numberParserStyle))

        assertEquals(0, '०'.toDigit(hi_IN_u_nu_native.numberParserStyle))
        assertEquals(9, '९'.toDigit(hi_IN_u_nu_native.numberParserStyle))
    }

    @Test
    fun `Char_toDigit() returns -1 when the character isn't considered a digit`() {
        assertEquals(-1, '/'.toDigit(en_US.numberParserStyle))
        assertEquals(-1, ':'.toDigit(en_US.numberParserStyle))
    }
}