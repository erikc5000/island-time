package io.islandtime.parser.internal

import io.islandtime.base.NumberProperty
import io.islandtime.format.SignStyle
import io.islandtime.parser.TemporalParser
import io.islandtime.parser.dsl.*

internal class SignParserBuilderImpl : SignParserBuilder {
    private val onParsed = mutableListOf<TemporalParser.Context.(parsed: Int) -> Unit>()

    override fun onParsed(action: TemporalParser.Context.(parsed: Int) -> Unit) {
        onParsed += action
    }

    fun build(): TemporalParser {
        return SignParser(onParsed)
    }
}

internal class WholeNumberParserBuilderImpl(
    private val minLength: Int,
    private val maxLength: Int
) : WholeNumberParserBuilder {

    private val onParsed = mutableListOf<TemporalParser.Context.(parsed: Long) -> Unit>()
    private var signStyle: SignStyle? = null

    override fun enforceSignStyle(signStyle: SignStyle) {
        this.signStyle = signStyle
    }

    override fun onParsed(action: TemporalParser.Context.(parsed: Long) -> Unit) {
        onParsed += action
    }

    fun build(): TemporalParser {
        return if (minLength == maxLength) {
            FixedLengthNumberParser(
                minLength,
                onParsed,
                signStyle
            )
        } else {
            VariableLengthNumberParser(
                minLength,
                maxLength,
                onParsed,
                signStyle
            )
        }
    }
}

internal class DecimalNumberParserBuilderImpl(
    private val minWholeLength: Int,
    private val maxWholeLength: Int,
    private val minFractionLength: Int,
    private val maxFractionLength: Int,
    private val fractionScale: Int,
) : DecimalNumberParserBuilder {

    private var signStyle: SignStyle? = null
    private val onParsed = mutableListOf<TemporalParser.Context.(whole: Long, fraction: Long) -> Unit>()

    override fun enforceSignStyle(signStyle: SignStyle) {
        this.signStyle = signStyle
    }

    override fun onParsed(action: TemporalParser.Context.(whole: Long, fraction: Long) -> Unit) {
        onParsed += action
    }

    fun build(): TemporalParser {
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

internal class FractionParserBuilderImpl(
    private val minLength: Int,
    private val maxLength: Int,
    private val scale: Int
) : FractionParserBuilder {

    private val onParsed = mutableListOf<TemporalParser.Context.(parsed: Long) -> Unit>()

    override fun onParsed(action: TemporalParser.Context.(parsed: Long) -> Unit) {
        onParsed += action
    }

    fun build(): TemporalParser = FractionParser(minLength, maxLength, scale, onParsed)
}

internal class TextParserBuilderImpl(
    private val minLength: Int,
    private val maxLength: Int
) : TextParserBuilder {

    private val onEachChar = mutableListOf<TemporalParser.Context.(char: Char, index: Int) -> StringParseAction>()
    private val onParsed = mutableListOf<TemporalParser.Context.(parsed: String) -> Unit>()

    override fun onEachChar(action: TemporalParser.Context.(char: Char, index: Int) -> StringParseAction) {
        onEachChar += action
    }

    override fun onParsed(action: TemporalParser.Context.(parsed: String) -> Unit) {
        onParsed += action
    }

    fun build(): TemporalParser {
        return StringParser(
            minLength,
            maxLength,
            onEachChar.ifEmpty { DEFAULT_ON_PARSED },
            onParsed
        )
    }

    companion object {
        private val DEFAULT_ON_PARSED = listOf<TemporalParser.Context.(char: Char, index: Int) -> StringParseAction>(
            { _, _ -> StringParseAction.REJECT_AND_STOP }
        )
    }
}

internal class CharLiteralParserBuilderImpl(
    private val char: Char
) : LiteralParserBuilder {
    private val onParsed = mutableListOf<TemporalParser.Context.() -> Unit>()

    override fun onParsed(action: TemporalParser.Context.() -> Unit) {
        onParsed += action
    }

    fun build(): TemporalParser {
        return CharLiteralParser(char, onParsed)
    }
}

internal class StringLiteralParserBuilderImpl(
    private val string: String
) : LiteralParserBuilder {
    private val onParsed = mutableListOf<TemporalParser.Context.() -> Unit>()

    override fun onParsed(action: TemporalParser.Context.() -> Unit) {
        onParsed += action
    }

    fun build(): TemporalParser {
        return StringLiteralParser(string, onParsed)
    }
}
