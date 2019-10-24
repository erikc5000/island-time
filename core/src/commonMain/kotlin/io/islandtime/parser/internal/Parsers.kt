package io.islandtime.parser.internal

import io.islandtime.parser.*

internal object EmptyDateTimeParser : DateTimeParser() {
    override fun parse(context: DateTimeParseContext, text: CharSequence, position: Int): Int {
        return position
    }
}

internal class CompositeDateTimeParser(
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

internal class OptionalDateTimeParser(
    private val childParser: DateTimeParser
) : DateTimeParser() {

    override fun parse(context: DateTimeParseContext, text: CharSequence, position: Int): Int {
        if (position >= text.length) {
            return position
        }

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

internal class AnyOfDateTimeParser(
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

internal class CharLiteralParser(
    private val char: Char,
    private val onParsed: List<DateTimeParseResult.() -> Unit>
) : DateTimeParser() {

    override fun parse(context: DateTimeParseContext, text: CharSequence, position: Int): Int {
        return if (position >= text.length) {
            position.inv()
        } else {
            val charFound = text[position]

            if (char == charFound) {
                onParsed.forEach { it(context.result) }
                position + 1
            } else {
                position.inv()
            }
        }
    }

    override val isLiteral: Boolean get() = true
}

internal class StringLiteralParser(
    private val string: String,
    private val onParsed: List<DateTimeParseResult.() -> Unit>
) : DateTimeParser() {

    override fun parse(context: DateTimeParseContext, text: CharSequence, position: Int): Int {
        return if (position >= text.length ||
            string.length > text.length - position ||
            text.subSequence(position, position + string.length) != string
        ) {
            position.inv()
        } else {
            onParsed.forEach { it(context.result) }
            position + string.length
        }
    }

    override val isLiteral: Boolean get() = true
}

internal class StringParser(
    private val length: IntRange,
    private val onEachChar: List<DateTimeParseResult.(char: Char, index: Int) -> StringParseAction>,
    private val onParsed: List<DateTimeParseResult.(parsed: String) -> Unit>
) : DateTimeParser() {

    override fun parse(context: DateTimeParseContext, text: CharSequence, position: Int): Int {
        if (position >= text.length) {
            return position.inv()
        }

        var currentPosition = position

        while (currentPosition < text.length &&
            (length.isEmpty() || currentPosition - position <= length.last)
        ) {
            if (onEachChar.any {
                    it(
                        context.result,
                        text[currentPosition],
                        currentPosition
                    ) == StringParseAction.REJECT_AND_STOP
                }
            ) {
                break
            }
            currentPosition++
        }

        if (!length.isEmpty() && currentPosition - position !in length) {
            return currentPosition.inv()
        }

        onParsed.forEach { it(context.result, text.substring(position, currentPosition)) }
        return currentPosition
    }
}

internal class SignParser(
    private val onParsed: List<DateTimeParseResult.(parsed: Int) -> Unit>
) : DateTimeParser() {

    override fun parse(context: DateTimeParseContext, text: CharSequence, position: Int): Int {
        return if (position >= text.length) {
            position.inv()
        } else {
            val chars = context.settings.chars

            when (text[position]) {
                in chars.plusSign -> {
                    onParsed.forEach { it(context.result, 1) }
                    position + 1
                }
                in chars.minusSign -> {
                    onParsed.forEach { it(context.result, -1) }
                    position + 1
                }
                else -> position.inv()
            }
        }
    }
}

internal class DecimalSeparatorParser(
    private val onParsed: List<DateTimeParseResult.() -> Unit>
) : DateTimeParser() {

    override fun parse(context: DateTimeParseContext, text: CharSequence, position: Int): Int {
        val chars = context.settings.chars

        return if (position < text.length && text[position] in chars.decimalSeparator) {
            onParsed.forEach { it(context.result) }
            position + 1
        } else {
            position.inv()
        }
    }
}

internal abstract class AbstractNumberParser(
    private val signStyle: SignStyle?
) : DateTimeParser() {

    protected fun parseSign(
        chars: DateTimeParserSettings.Chars,
        text: CharSequence,
        position: Int
    ): ParseSignResult {
        return when (text[position]) {
            in chars.plusSign -> when (signStyle) {
                SignStyle.NEVER, SignStyle.NEGATIVE_ONLY -> ParseSignResult.ERROR
                else -> ParseSignResult.POSITIVE
            }
            in chars.minusSign -> when (signStyle) {
                SignStyle.NEVER -> ParseSignResult.ERROR
                else -> ParseSignResult.NEGATIVE
            }
            else -> when (signStyle) {
                SignStyle.ALWAYS -> ParseSignResult.ERROR
                else -> ParseSignResult.ABSENT
            }
        }
    }

    protected enum class ParseSignResult {
        POSITIVE,
        NEGATIVE,
        ABSENT,
        ERROR
    }
}

internal class FixedLengthNumberParser(
    private val length: Int,
    private val onParsed: List<DateTimeParseResult.(parsed: Long) -> Unit>,
    signStyle: SignStyle?
) : AbstractNumberParser(signStyle) {

    init {
        require(length in 1..MAX_LONG_DIGITS) { "length must be from 1-19" }
    }

    override fun parse(context: DateTimeParseContext, text: CharSequence, position: Int): Int {
        val textLength = text.length
        var currentPosition = position

        if (currentPosition >= textLength) {
            return currentPosition.inv()
        }

        val signResult = parseSign(context.settings.chars, text, currentPosition)

        if (signResult == ParseSignResult.ERROR) {
            return currentPosition.inv()
        } else if (signResult != ParseSignResult.ABSENT) {
            currentPosition++
        }

        var value = 0L

        for (i in length downTo 1) {
            if (currentPosition >= text.length) {
                return currentPosition.inv()
            }

            val char = text[currentPosition]
            val digit = char.toDigit(context.settings.numberConverter)

            if (digit < 0) {
                return currentPosition.inv()
            }

            value += digit * FACTOR[i]
            currentPosition++
        }

        if (signResult == ParseSignResult.NEGATIVE) {
            value = -value
        }

        onParsed.forEach { it(context.result, value) }
        return currentPosition
    }
}

internal class VariableLengthNumberParser(
    private val minLength: Int,
    private val maxLength: Int,
    private val onParsed: List<DateTimeParseResult.(parsed: Long) -> Unit>,
    signStyle: SignStyle?
) : AbstractNumberParser(signStyle) {

    init {
        require(minLength <= maxLength) { "minLength must be <= maxLength" }
        require(minLength in 1..MAX_LONG_DIGITS) { "minLength must be from 1-19" }
        require(maxLength in 1..MAX_LONG_DIGITS) { "maxLength must be from 1-19" }
    }

    override fun parse(context: DateTimeParseContext, text: CharSequence, position: Int): Int {
        val textLength = text.length
        var currentPosition = position

        if (currentPosition >= textLength) {
            return currentPosition.inv()
        }

        val settings = context.settings
        val signResult = parseSign(settings.chars, text, currentPosition)

        if (signResult == ParseSignResult.ERROR) {
            return currentPosition.inv()
        } else if (signResult != ParseSignResult.ABSENT) {
            currentPosition++
        }

        var numberLength = 0

        for (i in currentPosition until textLength) {
            if (text[i].toDigit(settings.numberConverter) < 0) {
                break
            }
            numberLength++
        }

        if (numberLength < minLength) {
            return (currentPosition + numberLength).inv()
        } else if (numberLength > maxLength) {
            return (currentPosition + maxLength).inv()
        }

        var value = 0L

        for (i in numberLength downTo 1) {
            val char = text[currentPosition]
            val digit = char.toDigit(settings.numberConverter)
            value += digit * FACTOR[i]
            currentPosition++
        }

        if (signResult == ParseSignResult.NEGATIVE) {
            value = -value
        }

        onParsed.forEach { it(context.result, value) }
        return currentPosition
    }
}

internal class DecimalNumberParser(
    private val minWholeLength: Int,
    private val maxWholeLength: Int,
    private val minFractionLength: Int,
    private val maxFractionLength: Int,
    private val fractionScale: Int,
    signStyle: SignStyle?,
    private val onParsed: List<DateTimeParseResult.(whole: Long, fraction: Long) -> Unit>
) : AbstractNumberParser(signStyle) {

    init {
        require(minWholeLength <= maxWholeLength) { "minWholeLength must be <= maxWholeLength" }
        require(minWholeLength in 1..MAX_LONG_DIGITS) { "minWholeLength must be from 1-19" }
        require(maxWholeLength in 1..MAX_LONG_DIGITS) { "maxWholeLength must be from 1-19" }

        require(minFractionLength <= maxFractionLength) { "minFractionLength must be <= maxFractionLength" }
        require(minFractionLength in 0..9) { "minFractionLength must be from 0-9" }
        require(maxFractionLength in 0..9) { "maxFractionLength must be from 0-9" }

        require(fractionScale in 1..9) { "fractionScale must be from 1-9" }
    }

    override fun parse(context: DateTimeParseContext, text: CharSequence, position: Int): Int {
        val textLength = text.length
        var currentPosition = position

        if (currentPosition >= textLength) {
            return currentPosition.inv()
        }

        val settings = context.settings
        val signResult = parseSign(settings.chars, text, currentPosition)

        if (signResult == ParseSignResult.ERROR) {
            return currentPosition.inv()
        } else if (signResult != ParseSignResult.ABSENT) {
            currentPosition++
        }

        var wholeNumberLength = 0

        for (i in currentPosition until textLength) {
            if (text[i].toDigit(settings.numberConverter) < 0) {
                break
            }
            wholeNumberLength++
        }

        if (wholeNumberLength < minWholeLength) {
            return (currentPosition + wholeNumberLength).inv()
        } else if (wholeNumberLength > maxWholeLength) {
            return (currentPosition + maxWholeLength).inv()
        }

        var wholeResult = 0L

        for (i in wholeNumberLength downTo 1) {
            val char = text[currentPosition]
            val digit = char.toDigit(settings.numberConverter)
            wholeResult += digit * FACTOR[i]
            currentPosition++
        }

        if (signResult == ParseSignResult.NEGATIVE) {
            wholeResult = -wholeResult
        }

        if (currentPosition < textLength && maxFractionLength > 0) {
            if (text[currentPosition] in settings.chars.decimalSeparator) {
                currentPosition++

                if (currentPosition >= textLength) {
                    return currentPosition.inv()
                }

                var fractionNumberLength = 0

                for (i in currentPosition until textLength) {
                    if (text[i].toDigit(settings.numberConverter) < 0) {
                        break
                    }
                    fractionNumberLength++
                }

                if (fractionNumberLength < minFractionLength) {
                    return (currentPosition + fractionNumberLength).inv()
                } else if (fractionNumberLength > maxFractionLength) {
                    return (currentPosition + maxFractionLength).inv()
                }

                var fractionResult = 0L

                for (i in fractionScale downTo fractionScale - fractionNumberLength + 1) {
                    val char = text[currentPosition]
                    val digit = char.toDigit(settings.numberConverter)
                    fractionResult += digit * FACTOR[i]
                    currentPosition++
                }

                if (signResult == ParseSignResult.NEGATIVE) {
                    fractionResult = -fractionResult
                }

                onParsed.forEach { it(context.result, wholeResult, fractionResult) }
                return currentPosition
            } else if (minFractionLength > 0) {
                return currentPosition.inv()
            }
        } else if (minFractionLength > 0) {
            return currentPosition.inv()
        }

        onParsed.forEach { it(context.result, wholeResult, 0L) }
        return currentPosition
    }
}

internal class FractionParser internal constructor(
    private val minLength: Int,
    private val maxLength: Int,
    private val scale: Int,
    private val onParsed: List<DateTimeParseResult.(parsed: Long) -> Unit>
) : DateTimeParser() {

    init {
        require(minLength <= maxLength) { "minLength must be <= maxLength" }
        require(minLength in 1..9) { "minLength must be from 1-9" }
        require(maxLength in 1..9) { "maxLength must be from 1-9" }

        require(scale in 1..9) { "scale must be from 1-9" }
    }

    override fun parse(context: DateTimeParseContext, text: CharSequence, position: Int): Int {
        val textLength = text.length
        var currentPosition = position

        if (currentPosition >= textLength) {
            return currentPosition.inv()
        }

        val settings = context.settings
        var numberLength = 0

        for (i in currentPosition until textLength) {
            if (text[i].toDigit(settings.numberConverter) < 0) {
                break
            }
            numberLength++
        }

        if (numberLength < minLength) {
            return (currentPosition + numberLength).inv()
        } else if (numberLength > maxLength) {
            return (currentPosition + maxLength).inv()
        }

        var value = 0L

        for (i in scale downTo scale - numberLength + 1) {
            val char = text[currentPosition]
            val digit = char.toDigit(settings.numberConverter)
            value += digit * FACTOR[i]
            currentPosition++
        }

        onParsed.forEach { it(context.result, value) }
        return currentPosition
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

private fun Char.toDigit(numberConverter: DateTimeParserSettings.NumberConverter) =
    numberConverter.convertToDigit(this)