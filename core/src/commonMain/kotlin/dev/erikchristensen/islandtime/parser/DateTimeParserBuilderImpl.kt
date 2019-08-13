package dev.erikchristensen.islandtime.parser

internal class DateTimeParserBuilderImpl : DateTimeParserBuilder {
    private val parsers = mutableListOf<DateTimeParser>()

    override fun year(length: Int, block: FixedLengthNumberParserBuilder.() -> Unit) {
        fixedNumberValue(DateTimeField.YEAR, length, block)
    }

    override fun monthNumber(length: Int, block: FixedLengthNumberParserBuilder.() -> Unit) {
        fixedNumberValue(DateTimeField.MONTH_OF_YEAR, length, block)
    }

    override fun dayOfYear(length: Int, block: FixedLengthNumberParserBuilder.() -> Unit) {
        fixedNumberValue(DateTimeField.DAY_OF_YEAR, length, block)
    }

    override fun dayOfMonth(length: Int, block: FixedLengthNumberParserBuilder.() -> Unit) {
        fixedNumberValue(DateTimeField.DAY_OF_MONTH, length, block)
    }

    override fun dayOfWeekNumber(length: Int, block: FixedLengthNumberParserBuilder.() -> Unit) {
        fixedNumberValue(DateTimeField.DAY_OF_WEEK, length, block)
    }

    override fun hourOfDay(length: Int, block: FixedLengthNumberParserBuilder.() -> Unit) {
        fixedNumberValue(DateTimeField.HOUR_OF_DAY, length, block)
    }

    override fun minuteOfHour(length: Int, block: FixedLengthNumberParserBuilder.() -> Unit) {
        fixedNumberValue(DateTimeField.MINUTE_OF_HOUR, length, block)
    }

    override fun secondOfMinute(length: Int, block: FixedLengthNumberParserBuilder.() -> Unit) {
        fixedNumberValue(DateTimeField.SECOND_OF_MINUTE, length, block)
    }

    override fun timeOffsetHours(length: Int, block: FixedLengthNumberParserBuilder.() -> Unit) {
        fixedNumberValue(DateTimeField.TIME_OFFSET_HOURS, length, block)
    }

    override fun timeOffsetMinutes(length: Int, block: FixedLengthNumberParserBuilder.() -> Unit) {
        fixedNumberValue(DateTimeField.TIME_OFFSET_MINUTES, length, block)
    }

    override fun timeOffsetSeconds(length: Int, block: FixedLengthNumberParserBuilder.() -> Unit) {
        fixedNumberValue(DateTimeField.TIME_OFFSET_SECONDS, length, block)
    }

    override fun timeOffsetSign() {
        anyOf(
            CharValueParser('+', DateTimeField.TIME_OFFSET_SIGN, 1L),
            CharValueParser('-', DateTimeField.TIME_OFFSET_SIGN, -1L)
        )
    }

    override fun timeOffsetUtc() {
        parsers += CharValueParser('Z', DateTimeField.TIME_OFFSET_UTC, 1L)
    }

    override fun literal(char: Char) {
        parsers += CharLiteralParser(char)
    }

    override fun literal(string: String) {
        parsers += StringLiteralParser(string)
    }

    override fun optional(block: DateTimeParserBuilder.() -> Unit) {
        val subParser = DateTimeParserBuilderImpl().apply { block() }.buildElement()

        if (subParser != null) {
            parsers += OptionalDateTimeParser(subParser)
        }
    }

    override fun anyOf(vararg block: DateTimeParserBuilder.() -> Unit) {
        val subParsers = block.mapNotNull { DateTimeParserBuilderImpl().apply { it() }.buildElement() }
        anyOf(*subParsers.toTypedArray())
    }

    override fun anyOf(vararg subParsers: DateTimeParser) {
        if (subParsers.isNotEmpty()) {
            parsers += AnyOfDateTimeParser(subParsers)
        }
    }

    override fun subParser(subParser: DateTimeParser) {
        parsers += subParser
    }

    fun build(): DateTimeParser {
        return buildElement() ?: throw IllegalStateException("Parser is empty")
    }

    private fun fixedNumberValue(
        field: DateTimeField,
        length: Int,
        block: FixedLengthNumberParserBuilder.() -> Unit
    ) {
        val builder = FixedLengthNumberParserBuilderImpl(field, length).apply { block() }
        parsers += builder.build()
    }

    private fun buildElement(): DateTimeParser? {
        return when (parsers.count()) {
            0 -> null
            1 -> parsers.first()
            else -> CompositeDateTimeParser(parsers)
        }
    }
}

class FixedLengthNumberParserBuilderImpl(
    private val field: DateTimeField,
    private val length: Int
) : FixedLengthNumberParserBuilder {
    private var signStyle: SignStyle? = null
    override var signExceedsLength: Boolean = false

    override fun enforceSignStyle(signStyle: SignStyle) {
        this.signStyle = signStyle
    }

    fun build(): DateTimeParser {
        return FixedLengthNumberValueParser(
            field,
            length,
            signStyle,
            signExceedsLength
        )
    }
}