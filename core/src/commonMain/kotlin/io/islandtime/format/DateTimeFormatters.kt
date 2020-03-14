package io.islandtime.format

import io.islandtime.base.DateProperty
import io.islandtime.base.DurationProperty
import io.islandtime.base.TimeProperty

object DateTimeFormatters {
    object Iso {
        val CALENDAR_DATE = dateTimeFormatter {
            wholeNumber(DateProperty.Year, 4)

            onlyIf({ has(DateProperty.MonthOfYear) }) {
                +'-'
                wholeNumber(DateProperty.MonthOfYear, 2)

                onlyIf({ has(DateProperty.DayOfMonth) }) {
                    +'-'
                    wholeNumber(DateProperty.DayOfMonth, 2)
                }
            }
        }

        val DURATION = dateTimeFormatter {
            onlyIf({ has(DurationProperty.Sign)}) {
                sign(DurationProperty.Sign)
            }
            +'P'
            onlyIf({ has(DurationProperty.Years)}) {
                wholeNumber(DurationProperty.Years)
                +'Y'
            }
        }
    }
}