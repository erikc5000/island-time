package io.islandtime.parser.internal

import io.islandtime.format.SignStyle
import io.islandtime.parser.*

internal class SignParserBuilderImpl : SignParserBuilder {
    private val onParsed = mutableListOf<TemporalParseResult.(parsed: Int) -> Unit>()

    override fun onParsed(action: TemporalParseResult.(parsed: Int) -> Unit) {
        onParsed += action
    }

    fun build(): TemporalParser {
        return SignParser(onParsed)
    }
}

internal abstract class WholeNumberParserBuilderImpl : WholeNumberParserBuilder {
    protected val onParsed = mutableListOf<TemporalParseResult.(parsed: Long) -> Unit>()
    protected var signStyle: SignStyle? = null

    override fun enforceSignStyle(signStyle: SignStyle) {
        this.signStyle = signStyle
    }

    override fun onParsed(action: TemporalParseResult.(parsed: Long) -> Unit) {
        onParsed += action
    }
}

internal class FixedLengthNumberParserBuilderImpl(
    private val length: Int
) : WholeNumberParserBuilderImpl() {

    fun build(): TemporalParser {
        return FixedLengthNumberParser(
            length,
            onParsed,
            signStyle
        )
    }
}

internal class VariableLengthNumberParserBuilderImpl(
    private val minLength: Int,
    private val maxLength: Int
) : WholeNumberParserBuilderImpl() {

    fun build(): TemporalParser {
        return VariableLengthNumberParser(
            minLength,
            maxLength,
            onParsed,
            signStyle
        )
    }
}

internal class DecimalNumberParserBuilderImpl(
    private val minWholeLength: Int,
    private val maxWholeLength: Int,
    private val minFractionLength: Int,
    private val maxFractionLength: Int,
    private val fractionScale: Int
) : DecimalNumberParserBuilder {

    private var signStyle: SignStyle? = null
    private val onParsed = mutableListOf<TemporalParseResult.(whole: Long, fraction: Long) -> Unit>()

    override fun enforceSignStyle(signStyle: SignStyle) {
        this.signStyle = signStyle
    }

    override fun onParsed(action: TemporalParseResult.(whole: Long, fraction: Long) -> Unit) {
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

internal class StringParserBuilderImpl(
    private val length: IntRange
) : StringParserBuilder {
    private val onEachChar = mutableListOf<TemporalParseResult.(char: Char, index: Int) -> StringParseAction>()
    private val onParsed = mutableListOf<TemporalParseResult.(parsed: String) -> Unit>()

    override fun onEachChar(action: TemporalParseResult.(char: Char, index: Int) -> StringParseAction) {
        onEachChar += action
    }

    override fun onParsed(action: TemporalParseResult.(parsed: String) -> Unit) {
        onParsed += action
    }

    fun build(): TemporalParser {
        return StringParser(
            length,
            onEachChar.ifEmpty { DEFAULT_ON_PARSED },
            onParsed
        )
    }

    companion object {
        private val DEFAULT_ON_PARSED = listOf<TemporalParseResult.(char: Char, index: Int) -> StringParseAction>(
            { _, _ -> StringParseAction.REJECT_AND_STOP }
        )
    }
}

internal class CharLiteralParserBuilderImpl(
    private val char: Char
) : LiteralParserBuilder {
    private val onParsed = mutableListOf<TemporalParseResult.() -> Unit>()

    override fun onParsed(action: TemporalParseResult.() -> Unit) {
        onParsed += action
    }

    fun build(): TemporalParser {
        return CharLiteralParser(char, onParsed)
    }
}

internal class StringLiteralParserBuilderImpl(
    private val string: String
) : LiteralParserBuilder {
    private val onParsed = mutableListOf<TemporalParseResult.() -> Unit>()

    override fun onParsed(action: TemporalParseResult.() -> Unit) {
        onParsed += action
    }

    fun build(): TemporalParser {
        return StringLiteralParser(string, onParsed)
    }
}