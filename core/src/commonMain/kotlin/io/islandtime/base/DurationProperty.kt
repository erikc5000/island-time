package io.islandtime.base

sealed class DurationProperty {
    /**
     * The sign of the period, -1 (negative) or 1 (positive).
     */
    object Sign : NumberProperty

    /**
     * A period of years.
     */
    object Years : NumberProperty

    /**
     * A period of months.
     */
    object Months : NumberProperty

    /**
     * A period of weeks.
     */
    object Weeks : NumberProperty

    /**
     * A period of days.
     */
    object Days : NumberProperty

    /**
     * A duration of hours.
     */
    object Hours : NumberProperty

    /**
     * A duration of minutes.
     */
    object Minutes : NumberProperty

    /**
     * A duration of seconds.
     */
    object Seconds : NumberProperty

    /**
     * A duration of nanoseconds.
     */
    object Nanoseconds : NumberProperty

    /**
     * Is this duration zero?
     */
    object IsZero : BooleanProperty
}