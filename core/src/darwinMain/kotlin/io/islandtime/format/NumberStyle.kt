package io.islandtime.format

import io.islandtime.locale.Locale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.decimalSeparator

actual val Locale.numberStyle: NumberStyle
    get() {
        val formatter = NSNumberFormatter().also { it.locale = this }

        return NumberStyle(
            zeroDigit = formatter.stringFromNumber(NSNumber(int = 0))?.singleOrNull() ?: '0',
            plusSign = listOf(formatter.plusSign.singleOrElse { '+' }),
            minusSign = listOf(formatter.minusSign.singleOrElse { '-' }),
            decimalSeparator = listOf(decimalSeparator.singleOrElse { '.' })
        )
    }

private inline fun String.singleOrElse(default: () -> Char): Char {
    return if (length == 1) {
        this[0]
    } else {
        default()
    }
}