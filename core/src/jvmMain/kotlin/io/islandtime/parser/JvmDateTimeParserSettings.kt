package io.islandtime.parser

import io.islandtime.locale.Locale
import java.text.DecimalFormatSymbols

actual val Locale.numberParserStyle: NumberParserStyle
    get() {
        val symbols = DecimalFormatSymbols.getInstance(this)

        return NumberParserStyle(
            zeroDigit = symbols.zeroDigit,
            plusSign = listOf('+'),
            minusSign = listOf(symbols.minusSign),
            decimalSeparator = listOf(symbols.decimalSeparator)
        )
    }