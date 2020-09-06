package io.islandtime.formatter.internal

import io.islandtime.base.getOrElse
import io.islandtime.formatter.TemporalFormatter
import io.islandtime.formatter.dsl.onlyIfFalse
import io.islandtime.formatter.dsl.onlyIfPresentAndNonZero
import io.islandtime.formatter.dsl.onlyIfTrue
import io.islandtime.properties.DurationProperty

internal class IsoDurationFormatterBuilderImpl {
    fun build(): TemporalFormatter = TemporalFormatter {
        onlyIfTrue(DurationProperty.IsZero) {
            +"PT0S"
        }

        onlyIfFalse(DurationProperty.IsZero) {
            +'P'
            onlyIfPresentAndNonZero(DurationProperty.Years) {
                wholeNumber(DurationProperty.Years)
                +'Y'
            }
            onlyIfPresentAndNonZero(DurationProperty.Months) {
                wholeNumber(DurationProperty.Months)
                +'M'
            }
            onlyIfPresentAndNonZero(DurationProperty.Weeks) {
                wholeNumber(DurationProperty.Weeks)
                +'W'
            }
            onlyIfPresentAndNonZero(DurationProperty.Days) {
                wholeNumber(DurationProperty.Days)
                +'D'
            }
            onlyIf({
                temporal.getOrElse(DurationProperty.Hours) { 0L } != 0L ||
                    temporal.getOrElse(DurationProperty.Minutes) { 0L } != 0L ||
                    temporal.getOrElse(DurationProperty.Seconds) { 0L } != 0L ||
                    temporal.getOrElse(DurationProperty.Nanoseconds) { 0L } != 0L
            }) {
                +'T'
            }
            onlyIfPresentAndNonZero(DurationProperty.Hours) {
                wholeNumber(DurationProperty.Hours)
                +'H'
            }
            onlyIfPresentAndNonZero(DurationProperty.Minutes) {
                wholeNumber(DurationProperty.Minutes)
                +'M'
            }
            onlyIf({
                temporal.getOrElse(DurationProperty.Seconds) { 0L } != 0L ||
                    temporal.getOrElse(DurationProperty.Nanoseconds) { 0L } != 0L
            }) {
                decimalNumber(DurationProperty.Seconds, DurationProperty.Nanoseconds)
                +'S'
            }
        }
    }
}
