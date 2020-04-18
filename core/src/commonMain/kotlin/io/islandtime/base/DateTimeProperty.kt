package io.islandtime.base

/**
 * A property of a time point.
 */
sealed class TimePointProperty {
    /**
     * The second of the Unix epoch. `0` corresponds to `1970-01-01T00:00Z`.
     */
    object SecondOfUnixEpoch : TimePointProperty(), NumberProperty
}

/**
 * A calendar-related property.
 */
sealed class DateProperty {
    /**
     * The proleptic year.
     *
     * In the ISO calendar system, positive values indicate years in the current era, a value of 0 is equivalent to 1
     * BCE, -1 to 2 BCE, and so forth.
     */
    object Year : DateProperty(), NumberProperty

    /**
     * The year of the era.
     */
    object YearOfEra : DateProperty(), NumberProperty

    /**
     * The era.
     *
     * In the ISO calendar system, this will be 0 (`BCE`) or 1 (`CE`).
     */
    object Era : DateProperty(), NumberProperty

    /**
     * The month of the year, such as `January`
     *
     * In the ISO calendar system, this will be from 1 (`January`) to 12 (`December`).
     */
    object MonthOfYear : DateProperty(), NumberProperty

    /**
     * The day of the Unix epoch. `0` corresponds to `1970-01-01`.
     */
    object DayOfUnixEpoch : DateProperty(), NumberProperty

    /**
     * The day of the year.
     *
     * In the ISO calendar system, this will be from 1-366.
     */
    object DayOfYear : DateProperty(), NumberProperty

    /**
     * The day of the month.
     *
     * In the ISO calendar system, this will be from 1-31.
     */
    object DayOfMonth : DateProperty(), NumberProperty

    /**
     * The day of the week, such as `Sunday`.
     *
     * In the ISO calendar system, this will be from 1 (`Monday`) to 7 (`Sunday`).
     */
    object DayOfWeek : DateProperty(), NumberProperty

    /**
     * Does this represent the "far past" sentinel value, which should treated as unbounded (in ISO-8601 parlance,
     * "open") in the context of an interval.
     */
    object IsFarPast : DateProperty(), BooleanProperty

    /**
     * Does this represent the "far future" sentinel value, which should treated as unbounded (in ISO-8601 parlance,
     * "open") in the context of an interval.
     */
    object IsFarFuture : DateProperty(), BooleanProperty
}

/**
 * A property related to the time of day.
 */
sealed class TimeProperty {
    /**
     * AM or PM of the day.
     *
     * 0 (`AM`) or 1 (`PM`).
     */
    object AmPmOfDay : TimeProperty(), NumberProperty

    /**
     * The hour of the day in AM or PM, from 0-11.
     */
    object HourOfAmPm : TimeProperty(), NumberProperty

    /**
     * The hour of the day in AM or PM as read on a clock, from 1-12.
     */
    object ClockHourOfAmPm : TimeProperty(), NumberProperty

    /**
     * The hour of the day, from 0 to 23.
     */
    object HourOfDay : TimeProperty(), NumberProperty

    /**
     * The hour of the day as read on a clock, from 1-24.
     */
    object ClockHourOfDay : TimeProperty(), NumberProperty

    /**
     * The minute of the hour, from 0 to 59.
     */
    object MinuteOfHour : TimeProperty(), NumberProperty

    /**
     * The second of the day.
     *
     * A count of the number of seconds that have transpired since the start of the day, starting at 0.
     */
    object SecondOfDay : TimeProperty(), NumberProperty

    /**
     * The second of the minute, from 0 to 59.
     */
    object SecondOfMinute : TimeProperty(), NumberProperty

    /**
     * The nanosecond of the second, from 0 to 999,999,999.
     */
    object NanosecondOfSecond : TimeProperty(), NumberProperty

    /**
     * The nanosecond of the day.
     *
     * A count of the number of nanoseconds that have transpired since the start of the day, starting at 0.
     */
    object NanosecondOfDay : TimeProperty(), NumberProperty

    /**
     * The microsecond of the second, from 0 to 999,999.
     */
    object MicrosecondOfSecond : TimeProperty(), NumberProperty

    /**
     * The microsecond of the day.
     *
     * A count of the number of microseconds that have transpired since the start of the day, starting at 0.
     */
    object MicrosecondOfDay : TimeProperty(), NumberProperty

    /**
     * The millisecond of the second, from 0 to 999.
     */
    object MillisecondOfSecond : TimeProperty(), NumberProperty

    /**
     * The millisecond of the day.
     *
     * A count of the number of milliseconds that have transpired since the start of the day, starting at 0.
     */
    object MillisecondOfDay : TimeProperty(), NumberProperty
}

/**
 * A property of an offset from UTC.
 */
sealed class UtcOffsetProperty {
    /**
     * The sign of the UTC offset, -1 (negative) or 1 (positive).
     */
    object Sign : UtcOffsetProperty(), NumberProperty

    /**
     * The hour component of the UTC offset, from 0-18.
     */
    object Hours : UtcOffsetProperty(), NumberProperty

    /**
     * The minute component of the UTC offset, from 0-59.
     */
    object Minutes : UtcOffsetProperty(), NumberProperty

    /**
     * The second component of the UTC offset, from 0-59.
     */
    object Seconds : UtcOffsetProperty(), NumberProperty

    /**
     * The total number of seconds in the UTC offset, from (-18 * 60 * 60) to (18 * 60 * 60).
     */
    object TotalSeconds : UtcOffsetProperty(), NumberProperty
}

/**
 * A property of a time zone.
 */
sealed class TimeZoneProperty {
    /**
     * The ID associated with the time zone. This is generally an IANA time zone database identifier or fixed UTC
     * offset.
     */
    object Id : TimeZoneProperty(), StringProperty

    /**
     * Is this a fixed offset time zone?
     */
    object IsFixedOffset : TimeZoneProperty(), BooleanProperty
}