package io.islandtime.format.internal

import io.islandtime.format.DateTimeFormatBuilder
import io.islandtime.format.FormatOption
import io.islandtime.format.IsoFormat
import io.islandtime.format.TextStyle

internal fun parseDateTimePatternTo(
    builder: DateTimeFormatBuilder,
    pattern: CharSequence
): Unit = with(builder) {
    var position = 0

    while (position < pattern.length) {
        when (val char = pattern[position]) {
            in 'A'..'Z',
            in 'a'..'z' -> {
                val startPosition = position++
                while (position < pattern.length && pattern[position] == char) {
                    position++
                }
                val count = position - startPosition
                parsePatternLetter(char, count)
            }
            '\'' -> {
                val startPosition = position++

                while (position < pattern.length) {
                    if (pattern[position] == '\'') {
                        if (position + 1 < pattern.length && pattern[position + 1] == '\'') {
                            position++
                        } else {
                            break
                        }
                    }
                    position++
                }

                require(position < pattern.length) {
                    "The pattern '$pattern' ends with an unterminated literal"
                }

                val literalString = pattern.substring(startPosition + 1, position)

                if (literalString.isEmpty()) {
                    literal('\'')
                } else {
                    literal(literalString.replace("''", "'"))
                }

                position++
            }
            else -> {
                literal(char)
                position++
            }
        }
    }
}

private fun DateTimeFormatBuilder.parsePatternLetter(letter: Char, count: Int) {
    when (letter) {
        'G' -> parseEra(letter, count)
        'y' -> parseYearOfEra(count)
        'u' -> parseYear(count)
        // Not supported yet
        // 'Q' -> QUARTER_OF_YEAR
        // 'q' -> QUARTER_OF_YEAR
        'M' -> parseFormatMonth(letter, count)
        'L' -> parseStandaloneMonth(letter, count)
        'd' -> parseDayOfMonth(letter, count)
        'D' -> parseDayOfYear(letter, count)
        // Not supported yet
        // 'F' -> ALIGNED_DAY_OF_WEEK_IN_MONTH
        'E' -> parseFormatDayOfWeekName(letter, count)
        'e' -> parseFormatDayOfWeekNumberOrName(letter, count)
        'c' -> parseStandaloneDayOfWeekNumberOrName(letter, count)
        'a' -> parseAmPm(letter, count)
        'h' -> parseClockHourOfAmPm(letter, count)
        'H' -> parseHourOfDay(letter, count)
        'K' -> parseHourOfAmPm(letter, count)
        'k' -> parseClockHourOfDay(letter, count)
        'm' -> parseMinuteOfHour(letter, count)
        's' -> parseSecondOfMinute(letter, count)
        'S' -> parseFractionalSecond(count)
        'A' -> parseMillisecondOfDay(count)
        'z' -> parseSpecificNonLocationTimeZone(letter, count)
        'Z' -> when (count) {
            in 1..3 -> offset(format = IsoFormat.BASIC, useUtcDesignatorWhenZero = false)
            4 -> localizedOffset(TextStyle.FULL)
            5 -> offset(format = IsoFormat.EXTENDED, useUtcDesignatorWhenZero = true)
            else -> throwTooManyLettersException(letter)
        }
        'O' -> parseLocalizedOffset(letter, count)
        'v' -> parseGenericNonLocationTimeZone(letter, count)
        'V' -> parseTimeZoneId(letter, count)
        'X' -> parseOffset(letter, count, useUtcDesignatorWhenZero = true)
        'x' -> parseOffset(letter, count, useUtcDesignatorWhenZero = false)
        // Java-specific
        // 'n' -> NANO_OF_SECOND
        // 'N' -> NANO_OF_DAY
        else -> throw IllegalArgumentException("'$letter' is not supported")
    }
}

private fun DateTimeFormatBuilder.parseEra(letter: Char, count: Int) {
    when (count) {
        in 1..3 -> era(TextStyle.SHORT)
        4 -> era(TextStyle.FULL)
        5 -> era(TextStyle.NARROW)
        else -> throwTooManyLettersException(letter)
    }
}

private fun DateTimeFormatBuilder.parseYearOfEra(count: Int) {
    when (count) {
        2 -> twoDigitYearOfEra()
        else -> yearOfEra(count)
    }
}

private fun DateTimeFormatBuilder.parseYear(count: Int) {
    year(count)
}

private fun DateTimeFormatBuilder.parseFormatMonth(letter: Char, count: Int) {
    when (count) {
        1, 2 -> monthNumber(count..2)
        3 -> monthName(TextStyle.SHORT)
        4 -> monthName(TextStyle.FULL)
        5 -> monthName(TextStyle.NARROW)
        else -> throwTooManyLettersException(letter)
    }
}

private fun DateTimeFormatBuilder.parseStandaloneMonth(letter: Char, count: Int) {
    when (count) {
        1, 2 -> monthNumber(count..2)
        3 -> monthName(TextStyle.SHORT_STANDALONE)
        4 -> monthName(TextStyle.FULL_STANDALONE)
        5 -> monthName(TextStyle.NARROW_STANDALONE)
        else -> throwTooManyLettersException(letter)
    }
}

private fun DateTimeFormatBuilder.parseDayOfMonth(letter: Char, count: Int) {
    parseNumberPattern(letter, count, 2, ::dayOfMonth)
}

private fun DateTimeFormatBuilder.parseDayOfYear(letter: Char, count: Int) {
    parseNumberPattern(letter, count, 3, ::dayOfYear)
}

private fun DateTimeFormatBuilder.parseFormatDayOfWeekName(letter: Char, count: Int) {
    when (count) {
        in 1..3 -> dayOfWeekName(TextStyle.SHORT)
        4 -> dayOfWeekName(TextStyle.FULL)
        5 -> dayOfWeekName(TextStyle.NARROW)
        else -> throwTooManyLettersException(letter)
    }
}

private fun DateTimeFormatBuilder.parseFormatDayOfWeekNumberOrName(letter: Char, count: Int) {
    when (count) {
        1, 2 -> dayOfWeekNumber(count)
        3 -> dayOfWeekName(TextStyle.SHORT)
        4 -> dayOfWeekName(TextStyle.FULL)
        5 -> dayOfWeekName(TextStyle.NARROW)
        else -> throwTooManyLettersException(letter)
    }
}

private fun DateTimeFormatBuilder.parseStandaloneDayOfWeekNumberOrName(letter: Char, count: Int) {
    when (count) {
        1, 2 -> dayOfWeekNumber(1)
        3 -> dayOfWeekName(TextStyle.SHORT_STANDALONE)
        4 -> dayOfWeekName(TextStyle.FULL_STANDALONE)
        5 -> dayOfWeekName(TextStyle.NARROW_STANDALONE)
        else -> throwTooManyLettersException(letter)
    }
}

private fun DateTimeFormatBuilder.parseAmPm(letter: Char, count: Int) {
    if (count <= 3) {
        amPm()
    } else {
        throwTooManyLettersException(letter)
    }
}

private fun DateTimeFormatBuilder.parseClockHourOfAmPm(letter: Char, count: Int) {
    parseNumberPattern(letter, count, 2, ::clockHourOfAmPm)
}

private fun DateTimeFormatBuilder.parseHourOfDay(letter: Char, count: Int) {
    parseNumberPattern(letter, count, 2, ::hourOfDay)
}

private fun DateTimeFormatBuilder.parseHourOfAmPm(letter: Char, count: Int) {
    parseNumberPattern(letter, count, 2, ::hourOfAmPm)
}

private fun DateTimeFormatBuilder.parseClockHourOfDay(letter: Char, count: Int) {
    parseNumberPattern(letter, count, 2, ::clockHourOfDay)
}

private fun DateTimeFormatBuilder.parseMinuteOfHour(letter: Char, count: Int) {
    parseNumberPattern(letter, count, 2, ::minuteOfHour)
}

private fun DateTimeFormatBuilder.parseSecondOfMinute(letter: Char, count: Int) {
    parseNumberPattern(letter, count, 2, ::secondOfMinute)
}

private fun DateTimeFormatBuilder.parseFractionalSecond(count: Int) {
    nanosecondOfSecond(count)
}

private fun DateTimeFormatBuilder.parseMillisecondOfDay(count: Int) {
    millisecondOfDay(count)
}

private fun DateTimeFormatBuilder.parseSpecificNonLocationTimeZone(letter: Char, count: Int) {
    when {
        count <= 3 -> timeZoneName(TextStyle.SHORT, generic = false)
        count == 4 -> timeZoneName(TextStyle.FULL, generic = false)
        else -> throwTooManyLettersException(letter)
    }
}

private fun DateTimeFormatBuilder.parseGenericNonLocationTimeZone(letter: Char, count: Int) {
    when (count) {
        1 -> timeZoneName(TextStyle.SHORT, generic = true)
        4 -> timeZoneName(TextStyle.FULL, generic = true)
        else -> throw IllegalArgumentException("'$letter' must be used 1 or 4 times")
    }
}

private fun DateTimeFormatBuilder.parseTimeZoneId(letter: Char, count: Int) {
    when (count) {
        2 -> timeZoneId()
        else -> throw IllegalArgumentException("'$letter' can be used 2 times only")
    }
}

private fun DateTimeFormatBuilder.parseOffset(
    letter: Char,
    count: Int,
    useUtcDesignatorWhenZero: Boolean
) {
    when (count) {
        1 -> offset(
            format = IsoFormat.BASIC,
            useUtcDesignatorWhenZero = useUtcDesignatorWhenZero,
            minutes = FormatOption.OPTIONAL,
            seconds = FormatOption.NEVER
        )
        2 -> offset(
            format = IsoFormat.BASIC,
            useUtcDesignatorWhenZero = useUtcDesignatorWhenZero,
            minutes = FormatOption.ALWAYS,
            seconds = FormatOption.NEVER
        )
        3 -> offset(
            format = IsoFormat.EXTENDED,
            useUtcDesignatorWhenZero = useUtcDesignatorWhenZero,
            minutes = FormatOption.ALWAYS,
            seconds = FormatOption.NEVER
        )
        4 -> offset(
            format = IsoFormat.BASIC,
            useUtcDesignatorWhenZero = useUtcDesignatorWhenZero,
            minutes = FormatOption.ALWAYS,
            seconds = FormatOption.OPTIONAL
        )
        5 -> offset(
            format = IsoFormat.EXTENDED,
            useUtcDesignatorWhenZero = useUtcDesignatorWhenZero,
            minutes = FormatOption.ALWAYS,
            seconds = FormatOption.OPTIONAL
        )
        else -> throwTooManyLettersException(letter)
    }
}

private fun DateTimeFormatBuilder.parseLocalizedOffset(letter: Char, count: Int) {
    when (count) {
        1 -> localizedOffset(TextStyle.SHORT)
        4 -> localizedOffset(TextStyle.FULL)
        else -> throw IllegalArgumentException("'$letter' must be used 1 or 4 times")
    }
}

private inline fun parseNumberPattern(
    letter: Char,
    count: Int,
    maxDigits: Int,
    block: (Int, Int) -> Unit
) {
    when {
        count <= maxDigits -> block(count, maxDigits)
        else -> throwTooManyLettersException(letter)
    }
}

private fun throwTooManyLettersException(letter: Char): Nothing {
    throw IllegalArgumentException("'$letter' is used too many times")
}