package dev.erikchristensen.islandtime.parser

internal class DateTimeParserBuilderImpl : DateTimeParserBuilder {
    private val parsers = mutableListOf<DateTimeParser>()

    override fun sign(builder: SignParserBuilder.() -> Unit) {
        parsers += SignParserBuilderImpl().apply(builder).build()
    }

    override fun decimalSeparator(builder: LiteralParserBuilder.() -> Unit) {
        parsers += DecimalSeparatorParserBuilderImpl().apply(builder).build()
    }

    override fun wholeNumber(
        length: Int,
        builder: WholeNumberParserBuilder.() -> Unit
    ) {
        parsers += FixedLengthNumberParserBuilderImpl(length).apply(builder).build()
    }

    override fun wholeNumber(
        length: Int,
        field: DateTimeField,
        builder: WholeNumberParserBuilder.() -> Unit
    ) {
        wholeNumber(length) {
            onParsed { parsed -> result[field] = parsed }
            builder()
        }
    }

    override fun wholeNumber(length: IntRange, builder: WholeNumberParserBuilder.() -> Unit) {
        parsers += VariableLengthNumberParserBuilderImpl(length.first, length.last).apply(builder).build()
    }

    override fun wholeNumber(
        length: IntRange,
        field: DateTimeField,
        builder: WholeNumberParserBuilder.() -> Unit
    ) {
        wholeNumber(length) {
            onParsed { parsed -> result[field] = parsed }
            builder()
        }
    }

    override fun decimalNumber(
        wholeLength: IntRange,
        fractionLength: IntRange,
        fractionScale: Int,
        builder: DecimalNumberParserBuilder.() -> Unit
    ) {
        parsers += DecimalNumberParserBuilderImpl(
            wholeLength.first,
            wholeLength.last,
            fractionLength.first,
            fractionLength.last,
            fractionScale
        ).apply(builder).build()
    }

    override fun fraction(length: IntRange, builder: FractionParserBuilder.() -> Unit) {
        parsers += FractionParserBuilderImpl(length.first, length.last).apply(builder).build()
    }

    override fun string(length: IntRange, builder: StringParserBuilder.() -> Unit) {
        parsers += StringParserBuilderImpl(length).apply(builder).build()
    }

    override fun literal(char: Char, builder: LiteralParserBuilder.() -> Unit) {
        parsers += CharLiteralParserBuilderImpl(char).apply(builder).build()
    }

    override fun literal(string: String, builder: LiteralParserBuilder.() -> Unit) {
        parsers += StringLiteralParserBuilderImpl(string).apply(builder).build()
    }

    override fun optional(builder: DateTimeParserBuilder.() -> Unit) {
        val subParser = DateTimeParserBuilderImpl().apply(builder).buildElement()

        if (subParser != null) {
            parsers += OptionalDateTimeParser(subParser)
        }
    }

    override fun anyOf(vararg builders: DateTimeParserBuilder.() -> Unit) {
        val subParsers = builders.mapNotNull {
            DateTimeParserBuilderImpl().apply(it).buildElement()
        }
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

    private fun buildElement(): DateTimeParser? {
        return when (parsers.count()) {
            0 -> null
            1 -> parsers.first()
            else -> CompositeDateTimeParser(parsers)
        }
    }
}

class SignParserBuilderImpl internal constructor() : SignParserBuilder {
    private var onParsed: (DateTimeParseContext.(parsed: Int) -> Unit) = {}

    override fun onParsed(action: DateTimeParseContext.(parsed: Int) -> Unit) {
        onParsed = action
    }

    fun build(): DateTimeParser {
        return SignParser(onParsed)
    }
}

class DecimalSeparatorParserBuilderImpl internal constructor() : LiteralParserBuilder {
    private var onParsed: (DateTimeParseContext.() -> Unit) = {}

    override fun onParsed(action: DateTimeParseContext.() -> Unit) {
        onParsed = action
    }

    fun build(): DateTimeParser {
        return DecimalSeparatorParser(onParsed)
    }
}

abstract class WholeNumberParserBuilderImpl internal constructor() : WholeNumberParserBuilder {
    protected var onParsed: DateTimeParseContext.(parsed: Long) -> Unit = {}
    protected var signStyle: SignStyle? = null

    override fun enforceSignStyle(signStyle: SignStyle) {
        this.signStyle = signStyle
    }

    override fun onParsed(action: DateTimeParseContext.(parsed: Long) -> Unit) {
        onParsed = action
    }
}

class FixedLengthNumberParserBuilderImpl internal constructor(
    private val length: Int
) : WholeNumberParserBuilderImpl() {

    fun build(): DateTimeParser {
        return FixedLengthNumberParser(
            length,
            onParsed,
            signStyle
        )
    }
}

class VariableLengthNumberParserBuilderImpl internal constructor(
    private val minLength: Int,
    private val maxLength: Int
) : WholeNumberParserBuilderImpl() {

    fun build(): DateTimeParser {
        return VariableLengthNumberParser(
            minLength,
            maxLength,
            onParsed,
            signStyle
        )
    }
}

class DecimalNumberParserBuilderImpl internal constructor(
    private val minWholeLength: Int,
    private val maxWholeLength: Int,
    private val minFractionLength: Int,
    private val maxFractionLength: Int,
    private val fractionScale: Int
) : DecimalNumberParserBuilder {

    private var signStyle: SignStyle? = null
    private var onParsed: DateTimeParseContext.(whole: Long, fraction: Long) -> Unit = { _, _ -> }

    override fun enforceSignStyle(signStyle: SignStyle) {
        this.signStyle = signStyle
    }

    override fun onParsed(action: DateTimeParseContext.(whole: Long, fraction: Long) -> Unit) {
        onParsed = action
    }

    fun build(): DateTimeParser {
        return DecimalNumberParser(
            minWholeLength,
            maxWholeLength,
            minFractionLength,
            maxFractionLength,
            fractionScale,
            signStyle,
            onParsed
        )
    }
}

class FractionParserBuilderImpl internal constructor(
    private val minLength: Int,
    private val maxLength: Int
) : FractionParserBuilder {
    private var onParsed: DateTimeParseContext.(parsed: Long) -> Unit = {}

    override fun onParsed(action: DateTimeParseContext.(parsed: Long) -> Unit) {
        onParsed = action
    }

    fun build(): DateTimeParser {
        return FractionParser(
            minLength,
            maxLength,
            onParsed
        )
    }
}

class StringParserBuilderImpl internal constructor(
    private val length: IntRange
) : StringParserBuilder {
    private var onEachChar: DateTimeParseContext.(char: Char, index: Int) -> StringParseAction =
        { _, _ -> StringParseAction.REJECT_AND_STOP }
    private var onParsed: (DateTimeParseContext.(parsed: String) -> Unit) = {}

    override fun onEachChar(action: DateTimeParseContext.(char: Char, index: Int) -> StringParseAction) {
        onEachChar = action
    }

    override fun onParsed(action: DateTimeParseContext.(parsed: String) -> Unit) {
        onParsed = action
    }

    fun build(): DateTimeParser {
        return StringParser(
            length,
            onEachChar,
            onParsed
        )
    }
}

class CharLiteralParserBuilderImpl internal constructor(
    private val char: Char
) : LiteralParserBuilder {
    private var onParsed: (DateTimeParseContext.() -> Unit) = {}

    override fun onParsed(action: DateTimeParseContext.() -> Unit) {
        onParsed = action
    }

    fun build(): DateTimeParser {
        return CharLiteralParser(char, onParsed)
    }
}

class StringLiteralParserBuilderImpl internal constructor(
    private val string: String
) : LiteralParserBuilder {
    private var onParsed: (DateTimeParseContext.() -> Unit) = {}

    override fun onParsed(action: DateTimeParseContext.() -> Unit) {
        onParsed = action
    }

    fun build(): DateTimeParser {
        return StringLiteralParser(string, onParsed)
    }
}