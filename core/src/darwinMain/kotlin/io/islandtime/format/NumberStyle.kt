package io.islandtime.format

import io.islandtime.locale.Locale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.decimalSeparator

actual val Locale.numberStyle: NumberStyle
    get() {
        val formatter = NSNumberFormatter().also { it.locale = this }

        return NumberStyle(
            zeroDigit = formatter.stringFromNumber(NSNumber(int = 0))?.firstOrNull() ?: '0',
            plusSign = listOf(formatter.plusSign.firstOrNull() ?: '+'),
            minusSign = listOf(formatter.minusSign.firstOrNull() ?: '-'),
            decimalSeparator = listOf(decimalSeparator.firstOrNull() ?: '.')
        )
    }