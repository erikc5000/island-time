package io.islandtime.test

import io.islandtime.format.DateTimeFormatProvider
import io.islandtime.format.DateTimeFormatter
import io.islandtime.format.FormatStyle
import io.islandtime.format.TemporalFormatter
import io.islandtime.locale.Locale

object FakeDateTimeFormatProvider : DateTimeFormatProvider {
    override fun formatterFor(
        dateStyle: FormatStyle?,
        timeStyle: FormatStyle?,
        locale: Locale
    ): TemporalFormatter {
        val datePattern = if (dateStyle != null) "uuu-MM-dd ('$dateStyle')" else ""
        val timePattern = if (timeStyle != null) "HH:mm:ss ('$timeStyle')" else ""

        val wholePattern = if (datePattern.isNotEmpty() && timePattern.isNotEmpty()) {
            "$datePattern $timePattern"
        } else {
            datePattern + timePattern
        }

        return DateTimeFormatter(wholePattern)
    }
}