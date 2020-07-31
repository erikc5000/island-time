package io.islandtime.clock

import io.islandtime.Instant
import io.islandtime.PlatformInstant
import io.islandtime.TimeZone
import io.islandtime.internal.toPlatformInstant
import io.islandtime.measures.*

/**
 * A clock with a fixed time, suitable for testing.
 */
class FixedClock(
    private var instant: Instant,
    override val zone: TimeZone
) : Clock {

    fun setTo(instant: Instant) {
        this.instant = instant
    }

    operator fun plusAssign(days: LongDays) {
        instant += days
    }

    operator fun plusAssign(days: IntDays) {
        instant += days
    }

    operator fun plusAssign(hours: LongHours) {
        instant += hours
    }

    operator fun plusAssign(hours: IntHours) {
        instant += hours
    }

    operator fun plusAssign(minutes: LongMinutes) {
        instant += minutes
    }

    operator fun plusAssign(minutes: IntMinutes) {
        instant += minutes
    }

    operator fun plusAssign(seconds: LongSeconds) {
        instant += seconds
    }

    operator fun plusAssign(seconds: IntSeconds) {
        instant += seconds
    }

    operator fun plusAssign(milliseconds: LongMilliseconds) {
        instant += milliseconds
    }

    operator fun plusAssign(milliseconds: IntMilliseconds) {
        instant += milliseconds
    }

    operator fun plusAssign(microseconds: LongMicroseconds) {
        instant += microseconds
    }

    operator fun plusAssign(microseconds: IntMicroseconds) {
        instant += microseconds
    }

    operator fun plusAssign(nanoseconds: LongNanoseconds) {
        instant += nanoseconds
    }

    operator fun plusAssign(nanoseconds: IntNanoseconds) {
        instant += nanoseconds
    }

    operator fun minusAssign(days: LongDays) {
        instant -= days
    }

    operator fun minusAssign(days: IntDays) {
        instant -= days
    }

    operator fun minusAssign(hours: LongHours) {
        instant -= hours
    }

    operator fun minusAssign(hours: IntHours) {
        instant -= hours
    }

    operator fun minusAssign(minutes: LongMinutes) {
        instant -= minutes
    }

    operator fun minusAssign(minutes: IntMinutes) {
        instant -= minutes
    }

    operator fun minusAssign(seconds: LongSeconds) {
        instant -= seconds
    }

    operator fun minusAssign(seconds: IntSeconds) {
        instant -= seconds
    }

    operator fun minusAssign(milliseconds: LongMilliseconds) {
        instant -= milliseconds
    }

    operator fun minusAssign(milliseconds: IntMilliseconds) {
        instant -= milliseconds
    }

    operator fun minusAssign(microseconds: LongMicroseconds) {
        instant -= microseconds
    }

    operator fun minusAssign(microseconds: IntMicroseconds) {
        instant -= microseconds
    }

    operator fun minusAssign(nanoseconds: LongNanoseconds) {
        instant -= nanoseconds
    }

    operator fun minusAssign(nanoseconds: IntNanoseconds) {
        instant -= nanoseconds
    }

    override fun readMilliseconds(): LongMilliseconds {
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