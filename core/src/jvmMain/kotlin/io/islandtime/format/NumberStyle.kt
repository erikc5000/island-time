package io.islandtime.format

import io.islandtime.locale.Locale
import java.text.DecimalFormatSymbols

actual val Locale.numberStyle: NumberStyle
    get() {
        return DecimalFormatSymbols.getInstance(this).let { symbols ->
            NumberStyle(
                zeroDigit = symbols.zeroDigit,
                plusSign = '+',
                minusSign = symbols.minusSign,
                decimalSeparator = symbols.decimalSeparator
            )
        }
    }
