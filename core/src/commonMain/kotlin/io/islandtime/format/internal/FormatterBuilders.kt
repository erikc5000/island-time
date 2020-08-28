package io.islandtime.format.internal

import io.islandtime.format.LengthExceededBehavior
import io.islandtime.format.NumberFormatterBuilder
import io.islandtime.format.SignStyle
import io.islandtime.format.TemporalFormatter

internal class NumberFormatterBuilderImpl(
    private val propertyName: String,
    private val value: TemporalFormatter.Context.() -> Long,
    private val minLength: Int,
    private val maxLength: Int
) : NumberFormatterBuilder {

    override var signStyle: SignStyle = SignStyle.NEGATIVE_ONLY
    override var lengthExceededBehavior: LengthExceededBehavior = LengthExceededBehavior.THROW
    override var valueTransform: (Long) -> Long = { it }

    fun build(): TemporalFormatter {
        return WholeNumberFormatter(
            propertyName,
            value,
            minLength,
            maxLength,
            signStyle,
            lengthExceededBehavior,
            valueTransform
        )
    }
}

//internal class UtcOffsetFormatterBuilderImpl(val format: IsoFormat) : UtcOffsetFormatterBuilder {
//    override var useUtcDesignatorWhenZero: Boolean = true
//    override var minutes: FormatOption = FormatOption.ALWAYS
//    override var seconds: FormatOption = FormatOption.OPTIONAL
//
//    fun build(): TemporalFormatter {
//        return UtcOffsetFormatter(
//            format,
//            useUtcDesignatorWhenZero,
//            minutes,
//            seconds
//        )
//    }
//}
