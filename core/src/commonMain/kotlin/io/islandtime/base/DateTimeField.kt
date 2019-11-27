package io.islandtime.base

/**
 * A field describing a property of a date, time, or measurement of time.
 * 
 * These are intended to be used primarily during parsing and formatting. Each date-time primitive is capable of
 * resolving or supplying the set of fields that are applicable to it.
 */
enum class DateTimeField {
    /**
     * The proleptic year.
     *
     * In the ISO calendar system, years prior to 1 ACE will be negative.
     */
    YEAR,

    /**
     * The month of the year, such as `January`
     *
     * In the ISO calendar system, this will be from 1 (`January`) to 12 (`December`).
     */
    MONTH_OF_YEAR,

    /**
     * The day of the year.
     *
     * In the ISO calendar system, this will be from 1-366.
     */
    DAY_OF_YEAR,

    /**
     * The day of the month.
     * 
     * In the ISO calendar system, this will be from 1-31.
     */
    DAY_OF_MONTH,

    /**
     * The day of the week, such as `Sunday`.
     * 
     * In the ISO calendar system, this will be from 1 (`Monday`) to 7 (`Sunday`).
     */
    DAY_OF_WEEK,

    /**
     * AM or PM of the day.
     *
     * 0 (`AM`) or 1 (`PM`).
     */
//    AM_PM_OF_DAY,

    /**
     * The hour of the day in AM or PM, from 0-11.
     */
//    HOUR_OF_AM_PM,

    /**
     * The hour of the day in AM or PM as read on a clock, from 1-12.
     */
//    CLOCK_HOUR_OF_AM_PM,

    /**
     * The hour of the day, from 0 to 23.
     */
    HOUR_OF_DAY,

    /**
     * The hour of the day as read on a clock, from 1-24.
     */
//    CLOCK_HOUR_OF_DAY,

    /**
     * The minute of the hour, from 0 to 59.
     */
    MINUTE_OF_HOUR,

    /**
     * The second of the day.
     *
     * A count of the number of seconds that have transpired since the start of the day, starting at 0.
     */
//    SECOND_OF_DAY,

    /**
     * The second of the minute, from 0 to 59.
     */
    SECOND_OF_MINUTE,

    /**
     * The nanosecond of the second, from 0 to 999,999,999.
     */
    NANOSECOND_OF_SECOND,

    /**
     * The nanosecond of the day.
     * 
     * A count of the number of nanoseconds that have transpired since the start of the day, starting at 0.
     */
//    NANOSECOND_OF_DAY,

    /**
     * The microsecond of the second, from 0 to 999,999.
     */
//    MICROSECOND_OF_SECOND,

    /**
     * The microsecond of the day.
     *
     * A count of the number of microseconds that have transpired since the start of the day, starting at 0.
     */
//    MICROSECOND_OF_DAY,

    /**
     * The millisecond of the second, from 0 to 999.
     */
    MILLISECOND_OF_SECOND,

    /**
     * The millisecond of the day.
     *
     * A count of the number of milliseconds that have transpired since the start of the day, starting at 0.
     */
//    MILLISECOND_OF_DAY,

    /**
     * The sign of the UTC offset, -1 (negative) or 1 (positive).
     */
    UTC_OFFSET_SIGN,

    /**
     * The hour component of the UTC offset, from 0-18.
     */
    UTC_OFFSET_HOURS,

    /**
     * The minute component of the UTC offset, from 0-59.
     */
    UTC_OFFSET_MINUTES,

    /**
     * The second component of the UTC offset, from 0-59.
     */
    UTC_OFFSET_SECONDS,

    /**
     * The total number of seconds in the UTC offset, from (-18 * 60 * 60) to (18 * 60 * 60).
     */
    UTC_OFFSET_TOTAL_SECONDS,

    /**
     * The sign of the period, -1 (negative) or 1 (positive).
     */
    PERIOD_SIGN,

    /**
     * A period of years.
     */
    PERIOD_OF_YEARS,

    /**
     * A period of months.
     */
    PERIOD_OF_MONTHS,

    /**
     * A period of weeks.
     */
    PERIOD_OF_WEEKS,

    /**
     * A period of days.
     */
    PERIOD_OF_DAYS,

    /**
     * A duration of hours.
     */
    DURATION_OF_HOURS,

    /**
     * A duration of minutes.
     */
    DURATION_OF_MINUTES,

    /**
     * A duration of seconds.
     */
    DURATION_OF_SECONDS,

    /**
     * Indicates the presence of the minimum or maximum sentinel value, which should treated as unbounded
     * (in ISO-8601 parlance, "open") in the context of an interval.
     *
     * -1 (`true`) or 0 (`false`).
     */
    IS_UNBOUNDED
}