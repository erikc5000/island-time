package io.islandtime.format

import io.islandtime.locale.localeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.todo

class NumberStyleTest {
    val en_US = localeOf("en-US")
    val de_DE = localeOf("de-DE")
    val hi_IN_u_nu_native = localeOf("hi-IN-u-nu-native")

    @Test
    fun `throws an exception when given any empty list`() {
        assertFailsWith<IllegalArgumentException> {
            NumberStyle.DEFAULT.copy(plusSign = emptyList())
        }
        assertFailsWith<IllegalArgumentException> {
            NumberStyle.DEFAULT.copy(minusSign = emptyList())
        }
        assertFailsWith<IllegalArgumentException> {
            NumberStyle.DEFAULT.copy(decimalSeparator = emptyList())
        }
    }

    @Test
    fun `Locale_numberStyle returns a NumberStyle based on the locale`() {
        assertEquals(
            NumberStyle(
                zeroDigit = '0',
                plusSign = listOf('+'),
                minusSign = listOf('-'),
                decimalSeparator = listOf('.')
            ),
            en_US.numberStyle
        )

        assertEquals(
            NumberStyle(
                zeroDigit = '0',
                plusSign = listOf('+'),
                minusSign = listOf('-'),
                decimalSeparator = listOf(',')
            ),
            de_DE.numberStyle
        )

        // Breaks on some JDKs
        todo {
            assertEquals(
                NumberStyle(
                    zeroDigit = '०',
                    plusSign = listOf('+'),
                    minusSign = listOf('-'),
                    decimalSeparator = listOf('.')
                ),
                hi_IN_u_nu_native.numberStyle
            )
        }
    }
}