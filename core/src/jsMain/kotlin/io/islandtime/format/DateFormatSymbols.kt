package io.islandtime.format

import io.islandtime.js.internal.intl.DateTimeFormat
import io.islandtime.locale.Locale
import io.islandtime.locale.defaultLocale
import io.islandtime.objectOf
import kotlin.js.Date

class DateFormatSymbols private constructor(
    private val locale: Locale
) {

    companion object {

        fun getInstance(locale: Locale? = null): DateFormatSymbols {
            return DateFormatSymbols(locale ?: defaultLocale())
        }
    }

    private val amPmFormatter =
        DateTimeFormat(locale.locale, objectOf {
            hour12 = true
            hour = "2-digit"
        })

    val weekdays: Array<String> = dayNameList("long")

    val shortWeekdays: Array<String> = dayNameList("short")

    private fun dayNameList(format: String): Array<String> {
        fun Date.getDayDate(day: Int): Date =
            Date(getFullYear(), getMonth(), getDay() + day)

        val dtf = DateTimeFormat(locale.locale, objectOf {
            weekday = format
        })

        //TODO could not find a correct day as the start
        // this way the resulting array is not sorted correctly
        val now = Date(2020, 1, 5)

        val dayNumbers = sequenceOf(0, 1, 2, 3, 4, 5, 6)

        return dayNumbers
            .mapNotNull {
                dtf.formatToParts(now.getDayDate(it)).find { it.type == "weekday" }?.value
            }
            .toList()
            .toTypedArray()
    }

    val months: Array<String> = monthsNameList("long")

    val shortMonthsNames: Array<String> = monthsNameList("short")

    private fun monthsNameList(format: String): Array<String> {
        fun Date.getMonthDate(month: Int): Date =
            Date(getFullYear(), month)

        val dtf = DateTimeFormat("en-US", objectOf {
            month = format
        })

        val now = Date(2020, 0)

        val dayNumbers = sequenceOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
        return dayNumbers
            .mapNotNull {
                dtf.formatToParts(now.getMonthDate(it)).find { it.type == "month" }?.value
            }
            .toList()
            .toTypedArray()
    }

    val apPm: Array<String>
        get() {
            val at10 = Date(2020, 0, 1, 10)
            val at22 = Date(2020, 0, 1, 22)

            return arrayOf(
                amPmFormatter.formatToParts(at10).find { it.type == "dayPeriod" }?.value ?: APPM.AM.raw,
                amPmFormatter.formatToParts(at22).find { it.type == "dayPeriod" }?.value ?: APPM.PM.raw
            )
        }
}

enum class CalendarDayName(
    val fullName: String,
    val shortName: String

) {
    MONDAY(
        fullName = "Monday",
        shortName = "Mon"
    ),
    TUESDAY(
        fullName = "Tuesday",
        shortName = "Tue"
    ),
    WEDNESDAY(
        fullName = "Wednesday",
        shortName = "Wed"
    ),
    THURSDAY(
        fullName = "Thursday",
        shortName = "Thu"
    ),
    FRIDAY(
        fullName = "Friday",
        shortName = "Fri"
    ),
    SATURDAY(
        fullName = "Saturday",
        shortName = "Sat"
    ),
    SUNDAY(
        fullName = "Sunday",
        shortName = "Sun"
    )
}

enum class CalendarMonthName(
    val fullName: String,
    val shortName: String

) {
    JANUARY(
        fullName = "January",
        shortName = "Jan"
    ),
    FEBRUARY(
        fullName = "February",
        shortName = "Feb"
    ),
    MARCH(
        fullName = "March",
        shortName = "Mar"
    ),
    APRIL(
        fullName = "April",
        shortName = "Apr"
    ),
    MAY(
        fullName = "May",
        shortName = "May"
    ),
    JUNE(
        fullName = "June",
        shortName = "Jun"
    ),
    JULY(
        fullName = "July",
        shortName = "Jul"
    ),
    AUGUST(
        fullName = "August",
        shortName = "Aug"
    ),
    SEPTEMBER(
        fullName = "September",
        shortName = "Sep"
    ),
    OCTOBER(
        fullName = "October",
        shortName = "Oct"
    ),
    NOVEMBER(
        fullName = "November",
        shortName = "Nov"
    ),
    DECEMBER(
        fullName = "December",
        shortName = "Dec"
    )
}

enum class APPM(
    val raw: String
) {
    AM(
        raw = "AM"
    ),
    PM(
        raw = "PM"
    )
}