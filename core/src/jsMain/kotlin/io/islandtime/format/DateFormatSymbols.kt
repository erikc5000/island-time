package io.islandtime.format

import io.islandtime.locale.MLocale

class DateFormatSymbols private constructor(
    private val locale: MLocale?
) {

    companion object {

        fun getInstance(locale: MLocale? = null): DateFormatSymbols {
            return DateFormatSymbols(locale)
        }

    }

    val weekdays: Array<String> =
        locale?.weekdays() ?: moment.weekdays()

    val shortWeekdays: Array<String> =
        locale?.weekdaysShort() ?: moment.weekdaysShort()

    val months : Array<String> =
        locale?.months() ?: moment.months()

    val shortMonthsNames : Array<String> =
        locale?.monthsShort() ?: moment.monthsShort()

    val apPm : Array<String> =
        //TODO not sure if there is something similar in javascript
        APPM
            .values()
            .map { it.raw }
            .toTypedArray()
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

enum class APPM (
    val raw : String
){
    AM(
        raw = "AM"
    ),
    PM(
        raw = "PM"
    )
}