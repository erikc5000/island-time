package io.islandtime.format

import io.islandtime.locale.Locale

actual val Locale.numberStyle: NumberStyle
    get() {
        return NumberStyle(
            zeroDigit = '0',
            plusSign = listOf('+'),
            minusSign = listOf('-'),
            decimalSeparator = listOf(',')
        )
    }