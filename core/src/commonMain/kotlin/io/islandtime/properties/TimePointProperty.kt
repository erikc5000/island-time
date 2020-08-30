package io.islandtime.properties

import io.islandtime.Instant
import io.islandtime.base.NumberProperty
import io.islandtime.base.ObjectProperty

/**
 * A property of a time point.
 */
sealed class TimePointProperty {
    /**
     * The second of the Unix epoch. `0` corresponds to `1970-01-01T00:00Z`.
     */
    object SecondOfUnixEpoch : TimePointProperty(), NumberProperty

    /**
     * The time point represented as an [Instant].
     */
    object InstantObject : TimePointProperty(), ObjectProperty<Instant>
}
