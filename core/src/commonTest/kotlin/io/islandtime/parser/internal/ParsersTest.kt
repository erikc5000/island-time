package io.islandtime.parser.internal

import io.islandtime.format.numberStyle
import io.islandtime.locale.localeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.todo

class ParsersTest {
    val en_US = localeOf("en-US")
    val hi_IN_u_nu_native = localeOf("hi-IN-u-nu-native")

    @Test
    fun `Char_toDigit() converts a character to a digit according to NumberStyle`() {
        assertEquals(0, '0'.toDigit(en_US.numberStyle))
        assertEquals(9, '9'.toDigit(en_US.numberStyle))

        // Breaks on some JDKs
        todo {
            assertEquals(0, '०'.toDigit(hi_IN_u_nu_native.numberStyle))
            assertEquals(9, '९'.toDigit(hi_IN_u_nu_native.numberStyle))
        }
    }

    @Test
    fun `Char_toDigit() returns -1 when the character isn't considered a digit`() {
        assertEquals(-1, '/'.toDigit(en_US.numberStyle))
        assertEquals(-1, ':'.toDigit(en_US.numberStyle))
    }
}