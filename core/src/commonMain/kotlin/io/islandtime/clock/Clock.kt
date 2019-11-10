package io.islandtime.clock

import io.islandtime.Instant
import io.islandtime.TimeZone
import io.islandtime.measures.*

interface Clock {
    /**
     * The clock's time zone, set during initialization of the clock
     */
    val zone: TimeZone

    /**
     * Get the current number of milliseconds since the Unix epoch of 1970-01-01T00:00 in UTC
     */
    fun read(): LongMilliseconds

    /**
     * Get the current instant
     */
    fun instant(): Instant = Instant(read())
}

class SystemClock(override val zone: TimeZone = currentZone()) : Clock {
    override fun read() = PlatformSystemClock.read()

    override fun equals(other: Any?): Boolean {
        return other is SystemClock && zone == other.zone
    }

    override fun hashCode(): Int = zone.hashCode() + 1
    override fun toString(): String = "SystemClock[$zone]"

    companion object {
        val UTC = SystemClock(TimeZone.UTC)

        /**
         * Get the current system time zone
         */
        fun currentZone(): TimeZone = PlatformSystemClock.currentZone()
    }
}

/**
 * Platform system clock implementation
 */
internal expect object PlatformSystemClock {
    fun currentZone(): TimeZone
    fun read(): LongMilliseconds
}

/**
 * A clock with fixed time, suitable for testing
 */
class FixedClock(
    private var millisecondsSinceUnixEpoch: LongMilliseconds = 0L.milliseconds,
    override val zone: TimeZone = TimeZone.UTC
) : Clock {
    
    operator fun plusAssign(days: LongDays) {
        millisecondsSinceUnixEpoch += days
    }

    operator fun plusAssign(days: IntDays) {
        millisecondsSinceUnixEpoch += days
    }

    operator fun plusAssign(hours: LongHours) {
        millisecondsSinceUnixEpoch += hours
    }

    operator fun plusAssign(hours: IntHours) {
        millisecondsSinceUnixEpoch += hours
    }

    operator fun plusAssign(minutes: LongMinutes) {
        millisecondsSinceUnixEpoch += minutes
    }

    operator fun plusAssign(minutes: IntMinutes) {
        millisecondsSinceUnixEpoch += minutes
    }

    operator fun plusAssign(seconds: LongSeconds) {
        millisecondsSinceUnixEpoch += seconds
    }

    operator fun plusAssign(seconds: IntSeconds) {
        millisecondsSinceUnixEpoch += seconds
    }

    operator fun plusAssign(milliseconds: LongMilliseconds) {
        millisecondsSinceUnixEpoch += milliseconds
    }

    operator fun plusAssign(milliseconds: IntMilliseconds) {
        millisecondsSinceUnixEpoch += milliseconds
    }

    operator fun minusAssign(days: LongDays) {
        millisecondsSinceUnixEpoch -= days
    }

    operator fun minusAssign(days: IntDays) {
        millisecondsSinceUnixEpoch -= days
    }

    operator fun minusAssign(hours: LongHours) {
        millisecondsSinceUnixEpoch -= hours
    }

    operator fun minusAssign(hours: IntHours) {
        millisecondsSinceUnixEpoch -= hours
    }

    operator fun minusAssign(minutes: LongMinutes) {
        millisecondsSinceUnixEpoch -= minutes
    }

    operator fun minusAssign(minutes: IntMinutes) {
        millisecondsSinceUnixEpoch -= minutes
    }

    operator fun minusAssign(seconds: LongSeconds) {
        millisecondsSinceUnixEpoch -= seconds
    }

    operator fun minusAssign(seconds: IntSeconds) {
        millisecondsSinceUnixEpoch -= seconds
    }

    operator fun minusAssign(milliseconds: LongMilliseconds) {
        millisecondsSinceUnixEpoch -= milliseconds
    }

    operator fun minusAssign(milliseconds: IntMilliseconds) {
        millisecondsSinceUnixEpoch -= milliseconds
    }

    override fun read(): LongMilliseconds {
        return millisecondsSinceUnixEpoch
    }

    override fun equals(other: Any?): Boolean {
        return other is FixedClock &&
            millisecondsSinceUnixEpoch == other.millisecondsSinceUnixEpoch &&
            zone == other.zone
    }

    override fun hashCode(): Int {
        return 31 * millisecondsSinceUnixEpoch.hashCode() + zone.hashCode()
    }

    override fun toString(): String = "FixedClock[${instant()}, $zone]"
}

@Suppress("FunctionName")
fun FixedClock(days: LongDays, zone: TimeZone = TimeZone.UTC) = FixedClock(days.inMillisecondsExact(), zone)

@Suppress("FunctionName")
fun FixedClock(days: IntDays, zone: TimeZone = TimeZone.UTC) = FixedClock(days.inMilliseconds, zone)

@Suppress("FunctionName")
fun FixedClock(hours: LongHours, zone: TimeZone = TimeZone.UTC) = FixedClock(hours.inMillisecondsExact(), zone)

@Suppress("FunctionName")
fun FixedClock(hours: IntHours, zone: TimeZone = TimeZone.UTC) = FixedClock(hours.inMilliseconds, zone)

@Suppress("FunctionName")
fun FixedClock(minutes: LongMinutes, zone: TimeZone = TimeZone.UTC) = FixedClock(minutes.inMillisecondsExact(), zone)

@Suppress("FunctionName")
fun FixedClock(minutes: IntMinutes, zone: TimeZone = TimeZone.UTC) = FixedClock(minutes.inMilliseconds, zone)

@Suppress("FunctionName")
fun FixedClock(seconds: LongSeconds, zone: TimeZone = TimeZone.UTC) = FixedClock(seconds.inMillisecondsExact(), zone)

@Suppress("FunctionName")
fun FixedClock(seconds: IntSeconds, zone: TimeZone = TimeZone.UTC) = FixedClock(seconds.inMilliseconds, zone)

@Suppress("FunctionName")
fun FixedClock(milliseconds: IntMilliseconds, zone: TimeZone = TimeZone.UTC) = FixedClock(milliseconds.toLong(), zone)