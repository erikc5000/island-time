package io.islandtime.properties

import io.islandtime.Time
import io.islandtime.base.NumberProperty
import io.islandtime.base.ObjectProperty

/**
 * A property related to the time of day.
 */
sealed class TimeProperty {
    /**
     * AM or PM of the day.
     *
     * 0 (`AM`) or 1 (`PM`).
     */
    object AmPmOfDay : TimeProperty(), NumberProperty {
        override val valueRange: LongRange = 0L..1L
    }

    /**
     * The hour of the day in AM or PM, from 0-11.
     */
    object HourOfAmPm : TimeProperty(), NumberProperty {
        override val valueRange: LongRange = 0L..11L
    }

    /**
     * The hour of the day in AM or PM as read on a clock, from 1-12.
     */
    object ClockHourOfAmPm : TimeProperty(), NumberProperty {
        override val valueRange: LongRange = 1L..12L
    }

    /**
     * The hour of the day, from 0 to 23.
     */
    object HourOfDay : TimeProperty(), NumberProperty {
        override val valueRange: LongRange = 0L..23L
    }

    /**
     * The hour of the day as read on a clock, from 1-24.
     */
    object ClockHourOfDay : TimeProperty(), NumberProperty {
        override val valueRange: LongRange = 1L..24L
    }

    /**
     * The minute of the hour, from 0 to 59.
     */
    object MinuteOfHour : TimeProperty(), NumberProperty {
        override val valueRange: LongRange = 0L..59L
    }

    /**
     * The second of the day.
     *
     * A count of the number of seconds that have transpired since the start of the day, starting at 0.
     */
    object SecondOfDay : TimeProperty(), NumberProperty

    /**
     * The second of the minute, from 0 to 59.
     */
    object SecondOfMinute : TimeProperty(), NumberProperty {
        override val valueRange: LongRange = 0L..59L
    }

    /**
     * The nanosecond of the second, from 0 to 999,999,999.
     */
    object NanosecondOfSecond : TimeProperty(), NumberProperty {
        override val valueRange: LongRange = 0L..999_999_999L
    }

    /**
     * The nanosecond of the day.
     *
     * A count of the number of nanoseconds that have transpired since the start of the day, starting at 0.
     */
    object NanosecondOfDay : TimeProperty(), NumberProperty

    /**
     * The microsecond of the second, from 0 to 999,999.
     */
    object MicrosecondOfSecond : TimeProperty(), NumberProperty {
        override val valueRange: LongRange = 0L..999_999L
    }

    /**
     * The microsecond of the day.
     *
     * A count of the number of microseconds that have transpired since the start of the day, starting at 0.
     */
    object MicrosecondOfDay : TimeProperty(), NumberProperty

    /**
     * The millisecond of the second, from 0 to 999.
     */
    object MillisecondOfSecond : TimeProperty(), NumberProperty {
        override val valueRange: LongRange = 0L..999L
    }

    /**
     * The millisecond of the day.
     *
     * A count of the number of milliseconds that have transpired since the start of the day, starting at 0.
     */
    object MillisecondOfDay : TimeProperty(), NumberProperty

    /**
     * The [Time] object.
     */
    object TimeObject : TimeProperty(), ObjectProperty<Time>
}
