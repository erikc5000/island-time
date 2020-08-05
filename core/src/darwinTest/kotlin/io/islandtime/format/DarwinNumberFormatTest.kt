package io.islandtime.format

import io.islandtime.locale.toLocale
import kotlin.test.Test
import kotlin.test.assertEquals

class DarwinNumberFormatTest {
    @Test
    fun `Locale_numberStyle uses default chars when bidi marks are present`() {
        val locale = "ar_EG".toLocale()

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