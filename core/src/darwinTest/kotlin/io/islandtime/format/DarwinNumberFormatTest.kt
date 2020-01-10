package io.islandtime.format

import io.islandtime.locale.localeOf
import kotlin.test.Test
import kotlin.test.assertEquals

class DarwinNumberFormatTest {
    @Test
    fun `Locale_numberStyle uses default chars when bidi marks are present`() {
        val locale = localeOf("ar_EG")

        assertEquals(
            NumberStyle(
                zeroDigit = '٠',
                plusSign = listOf('+'),
                minusSign = listOf('-'),
                decimalSeparator = listOf('٫')
            ),
            locale.numberStyle
        )
    }
}