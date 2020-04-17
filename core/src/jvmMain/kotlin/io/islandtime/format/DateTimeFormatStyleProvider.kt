@file:Suppress("NewApi")

package io.islandtime.format

import io.islandtime.base.TemporalPropertyException
import io.islandtime.format.internal.PrintContext
import io.islandtime.jvm.asJavaTemporalAccessor

actual object PlatformDateTimeFormatStyleProvider : DateTimeFormatStyleProvider {
    override fun formatterFor(dateStyle: FormatStyle?, timeStyle: FormatStyle?): DateTimeFormatter {
        require(dateStyle != null || timeStyle != null) { "At least one date or time style must be non-null" }

        val javaFormatter = when {
            dateStyle != null && timeStyle == null -> java.time.format.DateTimeFormatter.ofLocalizedDate(dateStyle)
            dateStyle == null && timeStyle != null -> java.time.format.DateTimeFormatter.ofLocalizedTime(timeStyle)
            else -> java.time.format.DateTimeFormatter.ofLocalizedDateTime(dateStyle, timeStyle)
        }

        return object : DateTimeFormatter() {
            override fun format(context: PrintContext, stringBuilder: StringBuilder) {
                try {
                    javaFormatter
                        .withLocale(context.locale)
                        .withDecimalStyle(context.settings.numberStyle.toJavaDecimalStyle())
                        .formatTo(context.temporal.asJavaTemporalAccessor(), stringBuilder)
                } catch (e: java.time.temporal.UnsupportedTemporalTypeException) {
                    throw TemporalPropertyException(e.message, e)
                }
            }
        }
    }
}

private fun NumberStyle.toJavaDecimalStyle(): java.time.format.DecimalStyle {
    return java.time.format.DecimalStyle.STANDARD
        .withZeroDigit(zeroDigit)
        .withDecimalSeparator(decimalSeparator.first())
        .withNegativeSign(minusSign.first())
        .withPositiveSign(plusSign.first())
}