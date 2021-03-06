package io.islandtime.parser.internal

import dev.erikchristensen.javamath2kmp.negateExact
import dev.erikchristensen.javamath2kmp.plusExact
import io.islandtime.base.NumberProperty
import io.islandtime.format.*
import io.islandtime.locale.Locale
import io.islandtime.parser.*
import io.islandtime.parser.dsl.DisambiguationAction
import io.islandtime.parser.dsl.StringParseAction
import io.islandtime.properties.TimeZoneProperty
import io.islandtime.properties.UtcOffsetProperty
import io.islandtime.zone.TimeZoneRulesProvider

internal object EmptyParser : TemporalParser() {
    override fun parse(context: MutableContext, text: CharSequence, position: Int): Int {
        return position
    }

    override val isConst: Boolean get() = true
}

internal class CompositeParser(
    private val childParsers: List<TemporalParser>
) : TemporalParser() {

    override fun parse(context: MutableContext, text: CharSequence, position: Int): Int {
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

internal class OptionalParser(
    private val childParser: TemporalParser
) : TemporalParser() {

    override fun parse(context: MutableContext, text: CharSequence, position: Int): Int {
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

internal class AnyOfParser(
    private val childParsers: Array<out TemporalParser>
) : TemporalParser() {

    override fun parse(context: MutableContext, text: CharSequence, position: Int): Int {
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

internal class CaseSensitiveParser(
    private val isCaseSensitive: Boolean,
    private val childParser: TemporalParser
) : TemporalParser() {

    override fun parse(context: MutableContext, text: CharSequence, position: Int): Int {
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
    private val onParsed: List<Context.() -> Unit>
) : TemporalParser() {

    override fun parse(context: MutableContext, text: CharSequence, position: Int): Int {
        return if (position >= text.length) {
            position.inv()
        } else {
            val charFound = text[position]

            if (charFound.equals(char, ignoreCase = !context.isCaseSensitive)) {
                onParsed.forEach { it(context) }
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
    private val onParsed: List<Context.() -> Unit>
) : TemporalParser() {

    override fun parse(context: MutableContext, text: CharSequence, position: Int): Int {
        return if (position >= text.length ||
            string.length > text.length - position ||
            !text.regionMatches(position, string, 0, string.length, !context.isCaseSensitive)
        ) {
            position.inv()
        } else {
            onParsed.forEach { it(context) }
            position + string.length
        }
    }

    override val isLiteral: Boolean get() = true

    override val isConst: Boolean get() = onParsed.isEmpty()
}

internal class LocalizedDateTimeTextParser(
    private val property: NumberProperty,
    private val styles: Set<TextStyle>,
    private val overrideProvider: DateTimeTextProvider? = null
) : TemporalParser() {

    private val provider get() = overrideProvider ?: DateTimeTextProvider.Companion

    override fun parse(context: MutableContext, text: CharSequence, position: Int): Int {
        if (position >= text.length) {
            return position.inv()
        }

        val remainingLength = text.length - position
        val possibleValues = provider.getParsableTextFor(property, styles, context.locale)

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

// TODO: Get localized GMT string
private const val GMT_STRING = "GMT"

internal sealed class LocalizedUtcOffsetParser : TemporalParser() {
    override fun parse(context: MutableContext, text: CharSequence, position: Int): Int {
        if (text.length - position < GMT_STRING.length ||
            !text.regionMatches(position, GMT_STRING, 0, GMT_STRING.length, !context.isCaseSensitive)
        ) {
            return position.inv()
        }

        val currentPosition = position + GMT_STRING.length

        if (currentPosition < text.length) {
            val signChar = text[currentPosition]

            if (signChar == '-' || signChar == '+') {
                val result = parseOffset(signChar, context, text, currentPosition + 1)

                if (result >= 0) {
                    return result
                }
            }
        }

        context.result[UtcOffsetProperty.TotalSeconds] = 0L
        return currentPosition
    }

    protected abstract fun parseOffset(
        signChar: Char,
        context: MutableContext,
        text: CharSequence,
        position: Int
    ): Int
}

internal object LongLocalizedUtcOffsetParser : LocalizedUtcOffsetParser() {
    override fun parseOffset(
        signChar: Char,
        context: MutableContext,
        text: CharSequence,
        position: Int
    ): Int {
        if (position + 2 >= text.length) {
            return position.inv()
        }

        var currentPosition = position
        val hour1 = text[currentPosition].toDigit()
        val hour2 = text[currentPosition + 1].toDigit()

        if (hour1 < 0 || hour2 < 0) {
            return currentPosition.inv()
        }

        val hours = hour1 * 10 + hour2
        currentPosition += 2

        if (currentPosition + 2 >= text.length || text[currentPosition] != ':') {
            return currentPosition.inv()
        }

        val minute1 = text[currentPosition + 1].toDigit()
        val minute2 = text[currentPosition + 2].toDigit()

        if (minute1 < 0 || minute2 < 0) {
            return currentPosition.inv()
        }

        val minutes = minute1 * 10 + minute2
        currentPosition += 3

        context.result[UtcOffsetProperty.Sign] = if (signChar == '-') -1 else 1
        context.result[UtcOffsetProperty.Hours] = hours.toLong()
        context.result[UtcOffsetProperty.Minutes] = minutes.toLong()

        if (currentPosition + 2 < text.length && text[currentPosition] == ':') {
            val second1 = text[currentPosition + 1].toDigit()
            val second2 = text[currentPosition + 2].toDigit()

            if (second1 >= 0 && second2 >= 0) {
                val seconds = second1 * 10 + second2
                context.result[UtcOffsetProperty.Seconds] = seconds.toLong()
                currentPosition += 3
            }
        }

        return currentPosition
    }
}

internal object ShortLocalizedUtcOffsetParser : LocalizedUtcOffsetParser() {
    override fun parseOffset(
        signChar: Char,
        context: MutableContext,
        text: CharSequence,
        position: Int
    ): Int {
        if (position >= text.length) {
            return position.inv()
        }

        var currentPosition = position
        var hours = text[currentPosition].toDigit()

        if (hours < 0) {
            return currentPosition.inv()
        }

        currentPosition++

        if (currentPosition < text.length) {
            val char = text[currentPosition]

            if (char.isDigit()) {
                hours = hours * 10 + char.toDigit()
                currentPosition++
            }

            if (currentPosition + 2 < text.length && text[currentPosition] == ':') {
                val minute1 = text[currentPosition + 1].toDigit()
                val minute2 = text[currentPosition + 2].toDigit()

                if (minute1 >= 0 && minute2 >= 0) {
                    val minutes = minute1 * 10 + minute2
                    context.result[UtcOffsetProperty.Minutes] = minutes.toLong()
                    currentPosition += 3

                    if (currentPosition + 2 < text.length && text[currentPosition] == ':') {
                        val second1 = text[currentPosition + 1].toDigit()
                        val second2 = text[currentPosition + 2].toDigit()

                        if (second1 >= 0 && second2 >= 0) {
                            val seconds = second1 * 10 + second2
                            context.result[UtcOffsetProperty.Seconds] = seconds.toLong()
                            currentPosition += 3
                        }
                    }
                }
            }
        }

        context.result[UtcOffsetProperty.Sign] = if (signChar == '-') -1 else 1
        context.result[UtcOffsetProperty.Hours] = hours.toLong()
        return currentPosition
    }
}

private fun Char.isDigit(): Boolean = this in '0'..'9'

private typealias TimeZoneNameToIdList = List<Pair<String, List<String>>>

private val DESCENDING_LENGTH_COMPARATOR =
    compareByDescending<Pair<String, List<String>>> { it.first.length }.thenBy { it.first }

internal class TimeZoneNameParser(
    private val styles: Set<TimeZoneNameStyle>,
    private val disambiguate: DisambiguationAction
) : TemporalParser() {

    init {
        require(styles.isNotEmpty()) { "At least one style is required" }
    }

    override fun parse(context: MutableContext, text: CharSequence, position: Int): Int {
        if (position >= text.length) {
            return position.inv()
        }

        val remainingLength = text.length - position
        val regionIds = TimeZoneRulesProvider.availableRegionIds
        val possibleNames = getNameToIdList(regionIds, styles, context.locale)

        val found: Pair<String, List<String>>? = possibleNames.firstOrNull { (name) ->
            name.length <= remainingLength &&
                text.regionMatches(position, name, 0, name.length, !context.isCaseSensitive)
        }

        if (found == null || found.first.startsWith(GMT_STRING, ignoreCase = !context.isCaseSensitive)) {
            val resultingPosition = ShortLocalizedUtcOffsetParser.parse(context, text, position)

            if (resultingPosition > 0) {
                return resultingPosition
            }
        }

        if (found != null) {
            val (name, ids) = found

            context.result[TimeZoneProperty.Id] = if (ids.size == 1) {
                ids.first()
            } else {
                disambiguate(context, name, ids) ?: return position.inv()
            }

            return position + name.length
        }

        return position.inv()
    }

    // TODO: Optimize this
    @OptIn(ExperimentalStdlibApi::class)
    private fun getNameToIdList(
        regionIds: Iterable<String>,
        styles: Set<TimeZoneNameStyle>,
        locale: Locale
    ): TimeZoneNameToIdList {
        return buildMap<String, MutableSet<String>> {
            for (id in regionIds) {
                get(id)?.let { it += id } ?: put(id, mutableSetOf(id))

                for (style in styles) {
                    val name = TimeZoneNameProvider.getNameFor(id, style, locale) ?: continue

                    if (name.isNotEmpty()) {
                        get(name)?.let { it += id } ?: put(name, mutableSetOf(id))
                    }
                }
            }
        }
            .map { (name, ids) -> name to ids.sorted() }
            .sortedWith(DESCENDING_LENGTH_COMPARATOR)
    }
}

internal class TextParser(
    private val minLength: Int,
    private val maxLength: Int,
    private val onEachChar: List<Context.(char: Char, index: Int) -> StringParseAction>,
    private val onParsed: List<Context.(parsed: String) -> Unit>
) : TemporalParser() {

    init {
        require(minLength >= 0) { "minLength cannot be less than 0" }
        require(minLength <= maxLength) { "minLength cannot be greater than maxLength" }
    }

    override fun parse(context: MutableContext, text: CharSequence, position: Int): Int {
        if (position >= text.length) {
            return position.inv()
        }

        var currentPosition = position

        while (currentPosition < text.length && (currentPosition - position <= maxLength)) {
            if (onEachChar.any {
                    it(
                        context,
                        text[currentPosition],
                        currentPosition - position
                    ) == StringParseAction.REJECT_AND_STOP
                }
            ) {
                break
            }
            currentPosition++
        }

        if (currentPosition - position !in minLength..maxLength) {
            return position.inv()
        }

        onParsed.forEach { it(context, text.substring(position, currentPosition)) }
        return currentPosition
    }
}

internal class SignParser(
    private val onParsed: List<Context.(parsed: Int) -> Unit>
) : TemporalParser() {

    override fun parse(context: MutableContext, text: CharSequence, position: Int): Int {
        return if (position >= text.length) {
            position.inv()
        } else {
            val numberStyle = context.numberStyle

            when (text[position]) {
                in numberStyle.plusSign -> {
                    onParsed.forEach { it(context, 1) }
                    position + 1
                }
                in numberStyle.minusSign -> {
                    onParsed.forEach { it(context, -1) }
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
) : TemporalParser() {

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
    private val onParsed: List<Context.(parsed: Long) -> Unit>,
    signStyle: SignStyle?
) : AbstractNumberParser(signStyle) {

    init {
        require(length in 1..MAX_LONG_DIGITS) { "length must be from 1-19" }
    }

    override fun parse(context: MutableContext, text: CharSequence, position: Int): Int {
        val textLength = text.length
        var currentPosition = position

        if (currentPosition >= textLength) {
            return currentPosition.inv()
        }

        val signResult = parseSign(context.numberStyle, text, currentPosition)

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
                val digit = char.toDigit(context.numberStyle)

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
            throw TemporalParseException("Parsed number exceeds the max Long value", text.toString(), position, e)
        }

        onParsed.forEach { it(context, value) }
        return currentPosition
    }

    override val isConst: Boolean get() = onParsed.isEmpty()
}

internal class VariableLengthNumberParser(
    private val minLength: Int,
    private val maxLength: Int,
    private val onParsed: List<Context.(parsed: Long) -> Unit>,
    signStyle: SignStyle?
) : AbstractNumberParser(signStyle) {

    init {
        require(minLength <= maxLength) { "minLength must be <= maxLength" }
        require(minLength in 1..MAX_LONG_DIGITS) { "minLength must be from 1-19" }
        require(maxLength in 1..MAX_LONG_DIGITS) { "maxLength must be from 1-19" }
    }

    override fun parse(context: MutableContext, text: CharSequence, position: Int): Int {
        val textLength = text.length
        var currentPosition = position

        if (currentPosition >= textLength) {
            return currentPosition.inv()
        }

        val signResult = parseSign(context.numberStyle, text, currentPosition)

        if (signResult == ParseSignResult.ERROR) {
            return currentPosition.inv()
        } else if (signResult != ParseSignResult.ABSENT) {
            currentPosition++
        }

        var numberLength = 0

        for (i in currentPosition until textLength) {
            if (text[i].toDigit(context.numberStyle) < 0) {
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
                val digit = char.toDigit(context.numberStyle)
                value = value plusExact digit * FACTOR[i]
                currentPosition++
            }

            if (signResult == ParseSignResult.NEGATIVE) {
                value = value.negateExact()
            }
        } catch (e: ArithmeticException) {
            throw TemporalParseException("Parsed number exceeds the max Long value", text.toString(), position, e)
        }

        onParsed.forEach { it(context, value) }
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
    private val onParsed: List<Context.(whole: Long, fraction: Long) -> Unit>
) : AbstractNumberParser(signStyle) {

    init {
        require(minWholeLength <= maxWholeLength) { "minWholeLength must be <= maxWholeLength" }
        require(minWholeLength in 0..MAX_LONG_DIGITS) { "minWholeLength must be from 0-19" }
        require(maxWholeLength in 1..MAX_LONG_DIGITS) { "maxWholeLength must be from 1-19" }

        require(minFractionLength <= maxFractionLength) { "minFractionLength must be <= maxFractionLength" }
        require(minFractionLength in 0..9) { "minFractionLength must be from 0-9" }
        require(maxFractionLength in 0..9) { "maxFractionLength must be from 0-9" }

        require(fractionScale in 1..9) { "fractionScale must be from 1-9" }
    }

    override fun parse(context: MutableContext, text: CharSequence, position: Int): Int {
        val textLength = text.length
        var currentPosition = position

        if (currentPosition >= textLength) {
            return currentPosition.inv()
        }

        val signResult = parseSign(context.numberStyle, text, currentPosition)

        if (signResult == ParseSignResult.ERROR) {
            return currentPosition.inv()
        } else if (signResult != ParseSignResult.ABSENT) {
            currentPosition++
        }

        var wholeNumberLength = 0

        for (i in currentPosition until textLength) {
            if (text[i].toDigit(context.numberStyle) < 0) {
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
                val digit = char.toDigit(context.numberStyle)
                wholeResult = wholeResult plusExact digit * FACTOR[i]
                currentPosition++
            }

            if (signResult == ParseSignResult.NEGATIVE) {
                wholeResult = wholeResult.negateExact()
            }
        } catch (e: ArithmeticException) {
            throw TemporalParseException("Parsed number exceeds the max Long value", text.toString(), position, e)
        }

        if (currentPosition < textLength &&
            maxFractionLength > 0 &&
            text[currentPosition] in context.numberStyle.decimalSeparator
        ) {
            currentPosition++

            if (currentPosition >= textLength) {
                return currentPosition.inv()
            }

            var fractionNumberLength = 0

            for (i in currentPosition until textLength) {
                if (text[i].toDigit(context.numberStyle) < 0) {
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
                        val digit = char.toDigit(context.numberStyle)
                        fractionResult += digit * FACTOR[i]
                        currentPosition++
                    }

                    if (signResult == ParseSignResult.NEGATIVE) {
                        fractionResult = -fractionResult
                    }

                    onParsed.forEach { it(context, wholeResult, fractionResult) }
                    currentPosition
                }
            }
        }

        return if (minFractionLength > 0 || wholeNumberLength == 0) {
            currentPosition.inv()
        } else {
            onParsed.forEach { it(context, wholeResult, 0L) }
            currentPosition
        }
    }

    override val isConst: Boolean get() = onParsed.isEmpty()
}

internal class FractionParser(
    private val minLength: Int,
    private val maxLength: Int,
    private val scale: Int,
    private val onParsed: List<Context.(fraction: Long) -> Unit>
) : TemporalParser() {

    init {
        require(minLength <= maxLength) { "minFractionLength must be <= maxFractionLength" }
        require(minLength in 1..9) { "minFractionLength must be from 1-9" }
        require(maxLength in 1..9) { "maxFractionLength must be from 1-9" }
        require(scale in 1..9) { "fractionScale must be from 1-9" }
    }

    override fun parse(context: MutableContext, text: CharSequence, position: Int): Int {
        val textLength = text.length
        var currentPosition = position

        if (currentPosition >= textLength) {
            return currentPosition.inv()
        }

        var fractionNumberLength = 0

        for (i in currentPosition until textLength) {
            if (text[i].toDigit(context.numberStyle) < 0) {
                break
            }
            fractionNumberLength++
        }

        return when {
            fractionNumberLength < minLength -> (currentPosition + fractionNumberLength).inv()
            fractionNumberLength > maxLength -> (currentPosition + maxLength).inv()
            fractionNumberLength == 0 -> currentPosition.inv()
            else -> {
                var fractionResult = 0L

                for (i in scale downTo scale - fractionNumberLength + 1) {
                    val char = text[currentPosition]
                    val digit = char.toDigit(context.numberStyle)
                    fractionResult += digit * FACTOR[i]
                    currentPosition++
                }

                onParsed.forEach { it(context, fractionResult) }
                currentPosition
            }
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

internal fun Char.toDigit(): Int {
    val digit = this - '0'
    return if (digit in 0..9) digit else -1
}
