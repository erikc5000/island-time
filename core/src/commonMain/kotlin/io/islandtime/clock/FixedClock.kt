package io.islandtime.clock

import io.islandtime.Instant
import io.islandtime.PlatformInstant
import io.islandtime.TimeZone
import io.islandtime.internal.toPlatformInstant
import io.islandtime.measures.*

/**
 * A clock with a fixed time, suitable for testing.
 *
 * @param instant the initial instant that the clock should be set to
 * @param zone the time zone
 */
class FixedClock(
    private var instant: Instant,
    override val zone: TimeZone
) : Clock {

    fun setTo(instant: Instant) {
        this.instant = instant
    }

    operator fun plusAssign(days: Days) {
        instant += days
    }

    operator fun plusAssign(hours: Hours) {
        instant += hours
    }

    operator fun plusAssign(minutes: Minutes) {
        instant += minutes
    }

    operator fun plusAssign(seconds: Seconds) {
        instant += seconds
    }

    operator fun plusAssign(milliseconds: Milliseconds) {
        instant += milliseconds
    }

    operator fun plusAssign(microseconds: Microseconds) {
        instant += microseconds
    }

    operator fun plusAssign(nanoseconds: Nanoseconds) {
        instant += nanoseconds
    }

    operator fun minusAssign(days: Days) {
        instant -= days
    }

    operator fun minusAssign(hours: Hours) {
        instant -= hours
    }

    operator fun minusAssign(minutes: Minutes) {
        instant -= minutes
    }

    operator fun minusAssign(seconds: Seconds) {
        instant -= seconds
    }

    operator fun minusAssign(milliseconds: Milliseconds) {
        instant -= milliseconds
    }

    operator fun minusAssign(microseconds: Microseconds) {
        instant -= microseconds
    }

    operator fun minusAssign(nanoseconds: Nanoseconds) {
        instant -= nanoseconds
    }

    override fun readMilliseconds(): Milliseconds {
        return instant.millisecondsSinceUnixEpoch
    }

    override fun readInstant(): Instant {
        return instant
    }

    override fun readPlatformInstant(): PlatformInstant {
        return instant.toPlatformInstant()
    }

    override fun equals(other: Any?): Boolean {
        return other is FixedClock &&
            instant == other.instant &&
            zone == other.zone
    }

    override fun hashCode(): Int {
        return 31 * instant.hashCode() + zone.hashCode()
    }

    override fun toString(): String = "FixedClock[$instant, $zone]"
}
