package dev.erikchristensen.islandtime.parser

enum class TextStyle {
    FULL,
    LONG,
    MEDIUM,
    SHORT
}

enum class SignStyle {
    NEGATIVE_ONLY,
    NEVER,
    ALWAYS
}

enum class DecimalSeparator {
    DOT,
    COMMA,
    DOT_OR_COMMA
}

enum class DateTimeField {
    YEAR,
    MONTH_OF_YEAR,
    DAY_OF_YEAR,
    DAY_OF_MONTH,
    DAY_OF_WEEK,
    HOUR_OF_DAY,
    MINUTE_OF_HOUR,
    SECOND_OF_MINUTE,
    NANO_OF_SECOND,
    TIME_OFFSET_SIGN,
    TIME_OFFSET_HOURS,
    TIME_OFFSET_MINUTES,
    TIME_OFFSET_SECONDS,
    TIME_OFFSET_UTC,
    PERIOD_OF_YEARS,
    PERIOD_OF_MONTHS,
    PERIOD_OF_WEEKS,
    PERIOD_OF_DAYS,
    DURATION_OF_HOURS,
    DURATION_OF_MINUTES,
    DURATION_OF_SECONDS,
}

inline class DateTimeParseResult(
    private val fields: MutableMap<DateTimeField, Long> = hashMapOf()
) {
    fun deepCopy() = DateTimeParseResult().apply { fields.putAll(this@DateTimeParseResult.fields) }

    operator fun set(field: DateTimeField, value: Long) {
        fields[field] = value
    }

    operator fun get(field: DateTimeField): Long? {
        return fields[field]
    }
}

class DateTimeParseContext internal constructor() {
    var result = DateTimeParseResult()
    // var locale: Locale
    // var mode: DateTimeParseMode
}

abstract class DateTimeParser internal constructor() {
    fun parse(text: CharSequence): DateTimeParseResult {
        val context = DateTimeParseContext()
        val endPosition = parse(context, text, 0)

        if (endPosition < 0) {
            val actualPosition = -endPosition + 1
            throw DateTimeParseException("Parsing failed at index $actualPosition", text.toString(), actualPosition)
        } else if (endPosition < text.length) {
            throw DateTimeParseException("Unexpected character at index $endPosition", text.toString(), endPosition)
        }

        return context.result
    }

    internal abstract fun parse(context: DateTimeParseContext, text: CharSequence, position: Int): Int
}

class CompositeDateTimeParser internal constructor(
    private val childParsers: List<DateTimeParser>
) : DateTimeParser() {

    override fun parse(context: DateTimeParseContext, text: CharSequence, position: Int): Int {
        var currentPosition = position

        for (parser in childParsers) {
            currentPosition = parser.parse(context, text, currentPosition)

            if (currentPosition < 0) {
                break
            }
        }

        return currentPosition
    }
}

class OptionalDateTimeParser internal constructor(
    private val childParser: DateTimeParser
) : DateTimeParser() {

    override fun parse(context: DateTimeParseContext, text: CharSequence, position: Int): Int {
        val previousResult = context.result.deepCopy()
        val currentPosition = childParser.parse(context, text, position)

        return if (currentPosition < 0) {
            context.result = previousResult
            position
        } else {
            currentPosition
        }
    }
}

class AnyOfDateTimeParser internal constructor(
    private val childParsers: Array<out DateTimeParser>
) : DateTimeParser() {

    override fun parse(context: DateTimeParseContext, text: CharSequence, position: Int): Int {
        var currentPosition = position

        for (parser in childParsers) {
            val previousResult = context.result.deepCopy()
            currentPosition = parser.parse(context, text, currentPosition)

            if (currentPosition < 0) {
                context.result = previousResult
                currentPosition = position
            } else {
                return currentPosition
            }
        }

        return position.inv()
    }
}

class CharLiteralParser internal constructor(
    private vararg val acceptableChars: Char
) : DateTimeParser() {

    override fun parse(context: DateTimeParseContext, text: CharSequence, position: Int): Int {
        return if (position >= text.length) {
            position.inv()
        } else {
            val charFound = text[position]

            if (acceptableChars.any { it == charFound }) {
                position + 1
            } else {
                position.inv()
            }
        }
    }
}

class StringLiteralParser internal constructor(
    private val string: String
) : DateTimeParser() {

    override fun parse(context: DateTimeParseContext, text: CharSequence, position: Int): Int {
        return if (position >= text.length || text.subSequence(position, position + string.length) != string) {
            position.inv()
        } else {
            position + string.length
        }
    }
}

class CharValueParser internal constructor(
    private val char: Char,
    private val field: DateTimeField,
    private val value: Long
) : DateTimeParser() {

    override fun parse(context: DateTimeParseContext, text: CharSequence, position: Int): Int {
        return if (position >= text.length) {
            position.inv()
        } else {
            val charFound = text[position]

            if (char == charFound) {
                context.result[field] = value
                position + 1
            } else {
                position.inv()
            }
        }
    }
}

class FixedLengthNumberValueParser internal constructor(
    private val field: DateTimeField,
    private val length: Int,
    private val signStyle: SignStyle? = null,
    private val signExceedsLength: Boolean = false
) : DateTimeParser() {

    init {
        require(length in 1..MAX_LONG_DIGITS) { "length must be from 1-19" }
    }

    override fun parse(context: DateTimeParseContext, text: CharSequence, position: Int): Int {
        val textLength = text.length
        var currentPosition = position

        if (currentPosition >= textLength) {
            return currentPosition.inv()
        }

        val signResult = parseSign(text, currentPosition)

        if (signResult == ParseSignResult.ERROR) {
            return currentPosition.inv()
        } else if (signResult != ParseSignResult.ABSENT) {
            currentPosition++
        }

        val remainingLength = if (signExceedsLength || signResult == ParseSignResult.ABSENT) {
            length
        } else {
            length - 1
        }

        var value = 0L

        for (i in remainingLength downTo 1) {
            if (currentPosition >= text.length) {
                return currentPosition.inv()
            }

            val char = text[currentPosition]
            val digit = char.toDigit()

            if (digit < 0) {
                return currentPosition.inv()
            }

            value += digit * FACTOR[i]
            currentPosition++
        }

        context.result[field] = if (signResult == ParseSignResult.NEGATIVE) -value else value
        return currentPosition
    }

    private fun parseSign(text: CharSequence, position: Int): ParseSignResult {
        return when (text[position]) {
            '+' -> when (signStyle) {
                SignStyle.NEVER, SignStyle.NEGATIVE_ONLY -> ParseSignResult.ERROR
                else -> ParseSignResult.POSITIVE
            }
            '-' -> when (signStyle) {
                SignStyle.NEVER -> ParseSignResult.ERROR
                else -> ParseSignResult.NEGATIVE
            }
            else -> when (signStyle) {
                SignStyle.ALWAYS -> ParseSignResult.ERROR
                else -> ParseSignResult.ABSENT
            }
        }
    }

    private enum class ParseSignResult {
        POSITIVE,
        NEGATIVE,
        ABSENT,
        ERROR
    }
}

private const val MAX_LONG_DIGITS = 19

private val FACTOR = arrayOf(
    0L,
    1L,
    10L,
    100L,
    1_000L,
    10_000L,
    100_000L,
    1_000_000L,
    10_000_000L,
    100_000_000L,
    1_000_000_000L,
    10_000_000_000L,
    100_000_000_000L,
    1_000_000_000_000L,
    10_000_000_000_000L,
    100_000_000_000_000L,
    1_000_000_000_000_000L,
    10_000_000_000_000_000L,
    100_000_000_000_000_000L,
    1_000_000_000_000_000_000L
)

private fun Char.toDigit(): Int {
    val digit = toInt() - 48

    return if (digit > 9 || digit < 0) {
        -1
    } else {
        digit
    }
}

fun dateTimeParser(block: DateTimeParserBuilder.() -> Unit): DateTimeParser {
    return DateTimeParserBuilderImpl().run {
        block()
        build()
    }
}