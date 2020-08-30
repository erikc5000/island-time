package io.islandtime.properties

import io.islandtime.base.NumberProperty

/**
 * A property of an offset from UTC.
 */
sealed class UtcOffsetProperty {
    /**
     * The sign of the UTC offset, -1 (negative) or 1 (positive).
     */
    object Sign : UtcOffsetProperty(), NumberProperty {
        override val valueRange: LongRange = -1L..1L
    }

    /**
     * The hour component of the UTC offset, from 0-18.
     */
    object Hours : UtcOffsetProperty(), NumberProperty {
        override val valueRange: LongRange = 0L..18L
    }

    /**
     * The minute component of the UTC offset, from 0-59.
     */
    object Minutes : UtcOffsetProperty(), NumberProperty {
        override val valueRange: LongRange = 0L..59L
    }

    /**
     * The second component of the UTC offset, from 0-59.
     */
    object Seconds : UtcOffsetProperty(), NumberProperty {
        override val valueRange: LongRange = 0L..59L
    }

    /**
     * The total number of seconds in the UTC offset, from (-18 * 60 * 60) to (18 * 60 * 60).
     */
    object TotalSeconds : UtcOffsetProperty(), NumberProperty {
        override val valueRange: LongRange = (-18L * 60 * 60)..(18L * 60 * 60)
    }
}
