package io.islandtime.format

import io.islandtime.locale.Locale

/**
 * The parsing or formatting behavior surrounding a number's sign.
 */
enum class SignStyle {
    NEGATIVE_ONLY,
    NEVER,
    ALWAYS
}

/**
 * Defines the set of characters that should be used when parsing or formatting numbers.
 *
 * @property zeroDigit The character that represents zero.
 * @property plusSign A list of allowed plus sign characters. The first element will be used when formatting.
 * @property minusSign A list of allowed minus sign characters. The first element will be used when formatting.
 * @property decimalSeparator A list of allowed decimal separator characters. The first element will be used when
 *                            formatting
 */
data class NumberStyle(
    val zeroDigit: Char,
    val plusSign: List<Char>,
    val minusSign: List<Char>,
    val decimalSeparator: List<Char>
) {
    init {
        require(plusSign.isNotEmpty()) { "At least one plus sign character is required" }
        require(minusSign.isNotEmpty()) { "At least one minus sign character is required" }
        require(decimalSeparator.isNotEmpty()) { "At least one decimal separator character is required" }
    }

    companion object {
        /**
         * A locale-agnostic set of characters, matching those allowed in the date-time formats defined in ISO-8601.
         *
         * - Zero: '0'
         * - Plus sign: '+'
         * - Minus sign: '-' or '−'
         * - Decimal separator: '.' or ','
         */
        val DEFAULT = NumberStyle(
            zeroDigit = '0',
            plusSign = listOf('+'),
            minusSign = listOf('-', '−'),
            decimalSeparator = listOf('.', ',')
        )
    }
}

/**
 * The [NumberStyle] associated with this locale.
 */
expect val Locale.numberStyle: NumberStyle