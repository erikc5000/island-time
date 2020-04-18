package io.islandtime.base

sealed class DurationProperty {
    /**
     * The sign of the period, -1 (negative) or 1 (positive).
     */
    object Sign : DurationProperty(), NumberProperty

    /**
     * A period of years.
     */
    object Years : DurationProperty(), NumberProperty

    /**
     * A period of months.
     */
    object Months : DurationProperty(), NumberProperty

    /**
     * A period of weeks.
     */
    object Weeks : DurationProperty(), NumberProperty

    /**
     * A period of days.
     */
    object Days : DurationProperty(), NumberProperty

    /**
     * A duration of hours.
     */
    object Hours : DurationProperty(), NumberProperty

    /**
     * A duration of minutes.
     */
    object Minutes : DurationProperty(), NumberProperty

    /**
     * A duration of seconds.
     */
    object Seconds : DurationProperty(), NumberProperty

    /**
     * A duration of nanoseconds.
     */
    object Nanoseconds : DurationProperty(), NumberProperty

    /**
     * Is this duration zero?
     */
    object IsZero : DurationProperty(), BooleanProperty
}