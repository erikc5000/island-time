package io.islandtime.parser

data class DateTimeParserSettings(
    val chars: Chars = Chars.DEFAULT,
    val numberConverter: NumberConverter = NumberConverter.Default
) {
    data class Chars(
        val zero: List<Char>,
        val plusSign: List<Char>,
        val minusSign: List<Char>,
        val decimalSeparator: List<Char>
    ) {
        companion object {
            val DEFAULT = Chars(
                zero = listOf('0'),
                plusSign = listOf('+'),
                minusSign = listOf('-', '−'),
                decimalSeparator = listOf('.', ',')
            )
        }
    }

    interface NumberConverter {
        fun convertToDigit(char: Char): Int

        companion object Default : NumberConverter {
            override fun convertToDigit(char: Char): Int {
                val digit = char.toInt() - 48

                return if (digit > 9 || digit < 0) {
                    -1
                } else {
                    digit
                }
            }
        }
    }

    companion object {
        val DEFAULT = DateTimeParserSettings()
    }
}