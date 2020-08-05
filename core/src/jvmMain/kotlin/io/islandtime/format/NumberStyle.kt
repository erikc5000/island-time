package io.islandtime.format

import io.islandtime.locale.Locale
import java.text.DecimalFormatSymbols

actual val Locale.numberStyle: NumberStyle
    get() {
        val symbols = DecimalFormatSymbols.getInstance(this)

        return NumberStyle(
            zeroDigit = symbols.zeroDigit,
            plusSign = listOf('+'),
            minusSign = listOf(symbols.minusSign),
            decimalSeparator = listOf(symbols.decimalSeparator)
        )
    }
