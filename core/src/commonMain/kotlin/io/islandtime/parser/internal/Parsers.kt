package io.islandtime.parser.internal

import io.islandtime.base.NumberProperty
import io.islandtime.format.DateTimeTextProvider
import io.islandtime.format.NumberStyle
import io.islandtime.format.SignStyle
import io.islandtime.format.TextStyle
import io.islandtime.internal.negateExact
import io.islandtime.internal.plusExact
import io.islandtime.parser.*

internal object EmptyDateTimeParser : DateTimeParser() {
    override fun parse(context: DateTimeParseContext, text: CharSequence, position: Int): Int {
        return position
    }

    override val isConst: Boolean get() = true
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

    override val isConst: Boolean = childParsers.all { it.isConst }
}

internal class OptionalDateTimeParser(
    private val childParser: DateTimeParser
) : DateTimeParser() {

    override fun parse(context: DateTimeParseContext, text: CharSequence, position: Int): Int {
        if (position >= text.length) {
            return position
        }

        val previousResult = if (isConst) null else context.result.deepCopy()
        val currentPosition = childParser.parse(context, text, position)

        return if (currentPosition < 0) {
            if (previousResult != null) {
                context.result = previousResult
            }
            position
        } else {
            currentPosition
        }
    }

    override val isConst: Boolean get() = childParser.isConst
}

internal class AnyOfDateTimeParser(
    private val childParsers: Array<out DateTimeParser>
) : DateTimeParser() {

    override fun parse(context: DateTimeParseContext, text: CharSequence, position: Int): Int {
        var currentPosition = position

        for (parser in childParsers) {
            val previousResult = if (isConst) null else context.result.deepCopy()
            currentPosition = parser.parse(context, text, currentPosition)

            if (currentPosition < 0) {
                if (previousResult != null) {
                    context.result = previousResult
                }
                currentPosition = position
            } else {
                return currentPosition
            }
        }

        return position.inv()
    }

    override val isConst: Boolean = childParsers.all { it.isConst }
}

internal class CaseSensitiveDateTimeParser(
    private val isCaseSensitive: Boolean,
    private val childParser: DateTimeParser
) : DateTimeParser() {

    override fun parse(context: DateTimeParseContext, text: CharSequence, position: Int): Int {
        val previousCaseSensitivity = context.isCaseSensitive
        context.isCaseSensitive = isCaseSensitive
        val currentPosition = childParser.parse(context, text, position)
        context.isCaseSensitive = previousCaseSensitivity

        return currentPosition
    }

    override val isConst: Boolean get() = childParser.isConst
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

            if (charFound.equals(char, ignoreCase = !context.isCaseSensitive)) {
                onParsed.forEach { it(context.result) }
                position + 1
            } else {
                position.inv()
            }
        }
    }

    override val isLiteral: Boolean get() = true

    override val isConst: Boolean get() = onParsed.isEmpty()
}

internal class StringLiteralParser(
    private val string: String,
    private val onParsed: List<DateTimeParseResult.() -> Unit>
) : DateTimeParser() {

    override fun parse(context: DateTimeParseContext, text: CharSequence, position: Int): Int {
        return if (position >= text.length ||
            string.length > text.length - position ||
            !text.regionMatches(position, string, 0, string.length, !context.isCaseSensitive)
        ) {
            position.inv()
        } else {
            onParsed.forEach { it(context.result) }
            position + string.length
        }
    }

    override val isLiteral: Boolean get() = true

    override val isConst: Boolean get() = onParsed.isEmpty()
}

internal class LocalizedTextParser(
    private val property: NumberProperty,
    private val styles: Set<TextStyle>,
    private val provider: DateTimeTextProvider = DateTimeTextProvider.Companion
) : DateTimeParser() {

    override fun parse(context: DateTimeParseContext, text: CharSequence, position: Int): Int {
        if (position >= text.length) {
            return position.inv()
        }

        val remainingLength = text.length - position
        val possibleValues = provider.parsableTextFor(property, styles, context.locale)

        if (possibleValues.isEmpty()) {
            return position.inv()
        }

        for ((string, value) in possibleValues) {
            if (string.length <= remainingLength &&
                text.regionMatches(position, string, 0, string.length, !context.isCaseSensitive)
            ) {
                context.result[property] = value
                return position + string.length
            }
        }

        return position.inv()
    }
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

        while (currentPosition < text.length && (length.isEmpty() || currentPosition - position <= length.last)) {
            if (onEachChar.any {
                    it(
                        context.result,
                        text[currentPosition],
                        currentPosition - position
                    ) == StringParseAction.REJECT_AND_STOP
                }
            ) {
                break
            }
            currentPosition++
        }

        if (!length.isEmpty() && currentPosition - position !in length) {
            return position.inv()
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
            val numberStyle = context.settings.numberStyle

            when (text[position]) {
                in numberStyle.plusSign -> {
                    onParsed.forEach { it(context.result, 1) }
                    position + 1
                }
                in numberStyle.minusSign -> {
                    onParsed.forEach { it(context.result, -1) }
                    position + 1
                }
                else -> position.inv()
            }
        }
    }

    override val isConst: Boolean get() = onParsed.isEmpty()
}

internal abstract class AbstractNumberParser(
    private val signStyle: SignStyle?
) : DateTimeParser() {

    protected fun parseSign(
        numberStyle: NumberStyle,
        text: CharSequence,
        position: Int
    ): ParseSignResult {
        return when (text[position]) {
            in numberStyle.plusSign -> when (signStyle) {
                SignStyle.NEVER, SignStyle.NEGATIVE_ONLY -> ParseSignResult.ERROR
                else -> ParseSignResult.POSITIVE
            }
            in numberStyle.minusSign -> when (signStyle) {
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

        val signResult = parseSign(context.settings.numberStyle, text, currentPosition)

        if (signResult == ParseSignResult.ERROR) {
            return currentPosition.inv()
        } else if (signResult != ParseSignResult.ABSENT) {
            currentPosition++
        }

        var value = 0L

        try {
            for (i in length downTo 1) {
                if (currentPosition >= text.length) {
                    return currentPosition.inv()
                }

                val char = text[currentPosition]
                val digit = char.toDigit(context.settings.numberStyle)

                if (digit < 0) {
                    return currentPosition.inv()
                }

                value = value plusExact digit * FACTOR[i]
                currentPosition++
            }

            if (signResult == ParseSignResult.NEGATIVE) {
                value = value.negateExact()
            }
        } catch (e: ArithmeticException) {
            throw DateTimeParseException("Parsed number exceeds the max Long value", text.toString(), position, e)
        }

        onParsed.forEach { it(context.result, value) }
        return currentPosition
    }

    override val isConst: Boolean get() = onParsed.isEmpty()
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
        val signResult = parseSign(settings.numberStyle, text, currentPosition)

        if (signResult == ParseSignResult.ERROR) {
            return currentPosition.inv()
        } else if (signResult != ParseSignResult.ABSENT) {
            currentPosition++
        }

        var numberLength = 0

        for (i in currentPosition until textLength) {
            if (text[i].toDigit(settings.numberStyle) < 0) {
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

        try {
            for (i in numberLength downTo 1) {
                val char = text[currentPosition]
                val digit = char.toDigit(settings.numberStyle)
                value = value plusExact digit * FACTOR[i]
                currentPosition++
            }

            if (signResult == ParseSignResult.NEGATIVE) {
                value = value.negateExact()
            }
        } catch (e: ArithmeticException) {
            throw DateTimeParseException("Parsed number exceeds the max Long value", text.toString(), position, e)
        }

        onParsed.forEach { it(context.result, value) }
        return currentPosition
    }

    override val isConst: Boolean get() = onParsed.isEmpty()
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
        require(minWholeLength in 0..MAX_LONG_DIGITS) { "minWholeLength must be from 0-19" }
        require(maxWholeLength in 0..MAX_LONG_DIGITS) { "maxWholeLength must be from 0-19" }

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
        val signResult = parseSign(settings.numberStyle, text, currentPosition)

        if (signResult == ParseSignResult.ERROR) {
            return currentPosition.inv()
        } else if (signResult != ParseSignResult.ABSENT) {
            currentPosition++
        }

        var wholeNumberLength = 0

        for (i in currentPosition until textLength) {
            if (text[i].toDigit(settings.numberStyle) < 0) {
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

        try {
            for (i in wholeNumberLength downTo 1) {
                val char = text[currentPosition]
                val digit = char.toDigit(settings.numberStyle)
                wholeResult = wholeResult plusExact digit * FACTOR[i]
                currentPosition++
            }

            if (signResult == ParseSignResult.NEGATIVE) {
                wholeResult = wholeResult.negateExact()
            }
        } catch (e: ArithmeticException) {
            throw DateTimeParseException("Parsed number exceeds the max Long value", text.toString(), position, e)
        }

        if (currentPosition < textLength &&
            maxFractionLength > 0 &&
            text[currentPosition] in settings.numberStyle.decimalSeparator
        ) {
            currentPosition++

            if (currentPosition >= textLength) {
                return currentPosition.inv()
            }

            var fractionNumberLength = 0

            for (i in currentPosition until textLength) {
                if (text[i].toDigit(settings.numberStyle) < 0) {
                    break
                }
                fractionNumberLength++
            }

            return when {
                fractionNumberLength < minFractionLength -> (currentPosition + fractionNumberLength).inv()
                fractionNumberLength > maxFractionLength -> (currentPosition + maxFractionLength).inv()
                fractionNumberLength == 0 && wholeNumberLength == 0 -> currentPosition.inv()
                else -> {
                    var fractionResult = 0L

                    for (i in fractionScale downTo fractionScale - fractionNumberLength + 1) {
                        val char = text[currentPosition]
                        val digit = char.toDigit(settings.numberStyle)
                        fractionResult += digit * FACTOR[i]
                        currentPosition++
                    }

                    if (signResult == ParseSignResult.NEGATIVE) {
                        fractionResult = -fractionResult
                    }

                    onParsed.forEach { it(context.result, wholeResult, fractionResult) }
                    currentPosition
                }
            }
        }

        return if (minFractionLength > 0 || wholeNumberLength == 0) {
            currentPosition.inv()
        } else {
            onParsed.forEach { it(context.result, wholeResult, 0L) }
            currentPosition
        }
    }

    override val isConst: Boolean get() = onParsed.isEmpty()
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

internal fun Char.toDigit(numberStyle: NumberStyle): Int {
    val digit = this - numberStyle.zeroDigit
    return if (digit in 0..9) digit else -1
}