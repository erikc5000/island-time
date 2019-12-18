package io.islandtime.parser

import io.islandtime.locale.Locale

/**
 * Settings that control the parsing behavior.
 */
data class DateTimeParserSettings(
    val numberStyle: NumberParserStyle = NumberParserStyle.DEFAULT
) {
    companion object {
        /**
         * The default parser settings.
         */
        val DEFAULT = DateTimeParserSettings()
    }
}

/**
 * Defines the set of characters that should be used when parsing numbers.
 * @property zeroDigit The character that represents zero.
 * @property plusSign A list of allowed plus sign characters.
 * @property minusSign A list of allowed minus sign characters.
 * @property decimalSeparator A list of allowed decimal separator characters.
 */
data class NumberParserStyle(
    val zeroDigit: Char,
    val plusSign: List<Char>,
    val minusSign: List<Char>,
    val decimalSeparator: List<Char>
) {
    companion object {
        /**
         * A locale-agnostic set of characters, matching those allowed in the date-time formats defined in ISO-8601.
         *
         * - Zero: '0'
         * - Plus sign: '+'
         * - Minus sign: '-' or '−'
         * - Decimal separator: '.' or ','
         */
        val DEFAULT = NumberParserStyle(
            zeroDigit = '0',
            plusSign = listOf('+'),
            minusSign = listOf('-', '−'),
            decimalSeparator = listOf('.', ',')
        )
    }
}

/**
 * The [NumberParserStyle] associated with this locale.
 */
expect val Locale.numberParserStyle: NumberParserStyle

internal fun Char.toDigit(numberParserStyle: NumberParserStyle): Int {
    val digit = this - numberParserStyle.zeroDigit
    return if (digit in 0..9) digit else -1
}