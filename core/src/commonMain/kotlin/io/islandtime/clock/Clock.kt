@file:Suppress("FunctionName", "UNUSED_PARAMETER")

package io.islandtime.clock

import io.islandtime.Instant
import io.islandtime.PlatformInstant
import io.islandtime.TimeZone
import io.islandtime.measures.Milliseconds

/**
 * An abstraction providing the current time.
 *
 * For an implementation that uses the system's clock, see [SystemClock]. [FixedClock] is also available for testing
 * purposes.
 *
 * @see SystemClock
 * @see FixedClock
 */
interface Clock {
    /**
     * The time zone of this clock.
     */
    val zone: TimeZone

    /**
     * Reads the current number of milliseconds that have elapsed since the Unix epoch of `1970-01-01T00:00` in UTC.
     */
    fun readMilliseconds(): Milliseconds

    /**
     * Reads the current [Instant].
     */
    fun readInstant(): Instant

    /**
     * Reads the current [PlatformInstant].
     */
    fun readPlatformInstant(): PlatformInstant
}
