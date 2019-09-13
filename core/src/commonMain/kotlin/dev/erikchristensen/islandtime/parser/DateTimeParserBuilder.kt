package dev.erikchristensen.islandtime.parser

import dev.erikchristensen.islandtime.MAX_TIME_ZONE_STRING_LENGTH

@DslMarker
annotation class DateTimeParserDsl

@DateTimeParserDsl
interface DateTimeParserBuilder {
    fun sign(builder: SignParserBuilder.() -> Unit = {})

    fun decimalSeparator(builder: LiteralParserBuilder.() -> Unit = {})

    fun wholeNumber(
        length: Int,
        builder: WholeNumberParserBuilder.() -> Unit = {}
    )

    fun wholeNumber(
        length: Int,
        field: DateTimeField,
        builder: WholeNumberParserBuilder.() -> Unit = {}
    )

    fun wholeNumber(
        length: IntRange,
        field: DateTimeField,
        builder: WholeNumberParserBuilder.() -> Unit = {}
    )

    fun wholeNumber(
        length: IntRange,
        builder: WholeNumberParserBuilder.() -> Unit = {}
    )

    fun decimalNumber(
        wholeLength: IntRange = 1..19,
        fractionLength: IntRange = 0..9,
        fractionScale: Int = 9,
        builder: DecimalNumberParserBuilder.() -> Unit = {}
    )

    fun fraction(
        length: IntRange = 1..9,
        builder: FractionParserBuilder.() -> Unit = {}
    )

    fun string(
        length: IntRange = IntRange.EMPTY,
        builder: StringParserBuilder.() -> Unit
    )

    operator fun Char.unaryPlus() {
        literal(this)
    }

    operator fun String.unaryPlus() {
        literal(this)
    }

    fun literal(char: Char, builder: LiteralParserBuilder.() -> Unit = {})
    fun literal(string: String, builder: LiteralParserBuilder.() -> Unit = {})

    fun optional(builder: DateTimeParserBuilder.() -> Unit)

    fun anyOf(vararg builders: DateTimeParserBuilder.() -> Unit)
    fun anyOf(vararg subParsers: DateTimeParser)

    fun subParser(subParser: DateTimeParser)
}

@DateTimeParserDsl
interface SignParserBuilder {
    fun onParsed(action: DateTimeParseContext.(parsed: Int) -> Unit)
}

@DateTimeParserDsl
interface WholeNumberParserBuilder {
    fun enforceSignStyle(signStyle: SignStyle)
    fun onParsed(action: DateTimeParseContext.(parsed: Long) -> Unit)
}

@DateTimeParserDsl
interface DecimalNumberParserBuilder {
    fun enforceSignStyle(signStyle: SignStyle)
    fun onParsed(action: DateTimeParseContext.(whole: Long, fraction: Long) -> Unit)
}

@DateTimeParserDsl
interface FractionParserBuilder {
    fun onParsed(action: DateTimeParseContext.(parsed: Long) -> Unit)
}

@DateTimeParserDsl
interface StringParserBuilder {
    fun onEachChar(action: DateTimeParseContext.(char: Char, index: Int) -> StringParseAction)
    fun onParsed(action: DateTimeParseContext.(parsed: String) -> Unit)
}

@DateTimeParserDsl
interface LiteralParserBuilder {
    fun onParsed(action: DateTimeParseContext.() -> Unit)
}

fun DateTimeParserBuilder.year(length: Int, builder: WholeNumberParserBuilder.() -> Unit = {}) {
    wholeNumber(length, DateTimeField.YEAR, builder)
}

fun DateTimeParserBuilder.monthOfYear(length: Int, builder: WholeNumberParserBuilder.() -> Unit = {}) {
    wholeNumber(length, DateTimeField.MONTH_OF_YEAR, builder)
}

fun DateTimeParserBuilder.dayOfYear(length: Int, builder: WholeNumberParserBuilder.() -> Unit = {}) {
    wholeNumber(length, DateTimeField.DAY_OF_YEAR, builder)
}

fun DateTimeParserBuilder.dayOfMonth(length: Int, builder: WholeNumberParserBuilder.() -> Unit = {}) {
    wholeNumber(length, DateTimeField.DAY_OF_MONTH, builder)
}

fun DateTimeParserBuilder.dayOfWeekNumber(length: Int, builder: WholeNumberParserBuilder.() -> Unit = {}) {
    wholeNumber(length, DateTimeField.DAY_OF_WEEK, builder)
}

fun DateTimeParserBuilder.hourOfDay(length: Int, builder: WholeNumberParserBuilder.() -> Unit = {}) {
    wholeNumber(length, DateTimeField.HOUR_OF_DAY, builder)
}

fun DateTimeParserBuilder.minuteOfHour(length: Int, builder: WholeNumberParserBuilder.() -> Unit = {}) {
    wholeNumber(length, DateTimeField.MINUTE_OF_HOUR, builder)
}

fun DateTimeParserBuilder.secondOfMinute(length: Int, builder: WholeNumberParserBuilder.() -> Unit = {}) {
    wholeNumber(length, DateTimeField.SECOND_OF_MINUTE, builder)
}

fun DateTimeParserBuilder.nanosecondOfSecond() {
    fraction {
        onParsed { parsed -> result[DateTimeField.NANOSECOND_OF_SECOND] = parsed }
    }
}

fun DateTimeParserBuilder.utcOffsetHours(length: Int, builder: WholeNumberParserBuilder.() -> Unit = {}) {
    wholeNumber(length, DateTimeField.UTC_OFFSET_HOURS, builder)
}

fun DateTimeParserBuilder.utcOffsetMinutes(length: Int, builder: WholeNumberParserBuilder.() -> Unit = {}) {
    wholeNumber(length, DateTimeField.UTC_OFFSET_MINUTES, builder)
}

fun DateTimeParserBuilder.utcOffsetSeconds(length: Int, builder: WholeNumberParserBuilder.() -> Unit = {}) {
    wholeNumber(length, DateTimeField.UTC_OFFSET_SECONDS, builder)
}

/**
 * Parses a number's sign and populates [DateTimeField.UTC_OFFSET_SIGN] with -1L, if negative or 1L, if positive
 */
fun DateTimeParserBuilder.utcOffsetSign() {
    sign {
        onParsed { parsed -> result[DateTimeField.UTC_OFFSET_SIGN] = parsed.toLong() }
    }
}

/**
 * Parses the character 'Z' and populates [DateTimeField.UTC_OFFSET_ZERO] with 1L if found
 */
fun DateTimeParserBuilder.utcOffsetZero() {
    literal('Z') {
        onParsed { result[DateTimeField.UTC_OFFSET_ZERO] = 1L }
    }
}

fun DateTimeParserBuilder.periodOfYears(
    length: IntRange = 1..10,
    builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length, DateTimeField.PERIOD_OF_YEARS, builder)
}

fun DateTimeParserBuilder.periodOfMonths(
    length: IntRange = 1..10,
    builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length, DateTimeField.PERIOD_OF_MONTHS, builder)
}

fun DateTimeParserBuilder.periodOfDays(
    length: IntRange = 1..10,
    builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length, DateTimeField.PERIOD_OF_DAYS, builder)
}

fun DateTimeParserBuilder.durationOfHours(
    length: IntRange = 1..19,
    builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length, DateTimeField.DURATION_OF_HOURS, builder)
}

fun DateTimeParserBuilder.durationOfMinutes(
    length: IntRange = 1..19,
    builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length, DateTimeField.DURATION_OF_MINUTES, builder)
}

fun DateTimeParserBuilder.durationOfWholeSeconds(
    length: IntRange = 1..19,
    builder: WholeNumberParserBuilder.() -> Unit = {}
) {
    wholeNumber(length, DateTimeField.DURATION_OF_SECONDS, builder)
}

fun DateTimeParserBuilder.durationOfFractionalSeconds(
    builder: DecimalNumberParserBuilder.() -> Unit = {}
) {
    decimalNumber {
        onParsed { whole, fraction ->
            result[DateTimeField.DURATION_OF_SECONDS] = whole
            result[DateTimeField.NANOSECOND_OF_SECOND] = fraction
        }
        builder()
    }
}

fun DateTimeParserBuilder.timeZoneRegion() {
    string(length = 1..MAX_TIME_ZONE_STRING_LENGTH) {
        onEachChar { char, index ->
            if (index == 0) {
                when (char) {
                    in 'A'..'Z',
                    in 'a'..'z' -> StringParseAction.ACCEPT_AND_CONTINUE
                    else -> StringParseAction.REJECT_AND_STOP
                }
            } else {
                when (char) {
                    in 'A'..'Z',
                    in 'a'..'z',
                    in '-'..'9',
                    '~',
                    '_',
                    '+' -> StringParseAction.ACCEPT_AND_CONTINUE
                    else -> StringParseAction.REJECT_AND_STOP
                }
            }
        }

        onParsed { result.timeZoneRegion = it }
    }
}