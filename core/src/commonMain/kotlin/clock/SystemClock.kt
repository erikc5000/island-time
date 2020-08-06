@file:Suppress("FunctionName")

package io.islandtime.clock

import io.islandtime.TimeZone
import io.islandtime.clock.internal.createSystemClock
import io.islandtime.internal.deprecatedToError

/**
 * A clock that provides the time from the current system.
 *
 * The time zone is treated as an immutable property of the clock, set when it is created. If you wish to follow
 * changes to the system clock's configured time zone, you must create a new [SystemClock] in response to any time zone
 * changes.
 */
abstract class SystemClock protected constructor() : Clock {
    override fun equals(other: Any?): Boolean {
        return other is SystemClock && zone == other.zone
    }

    override fun hashCode(): Int = zone.hashCode() + 1
    override fun toString(): String = "SystemClock[$zone]"

    companion object {
        /**
         * A system clock in the UTC time zone.
         */
        val UTC: SystemClock = createSystemClock(TimeZone.UTC)

        @Deprecated(
            "Use TimeZone.systemDefault() instead.",
            ReplaceWith("TimeZone.systemDefault()"),
            DeprecationLevel.ERROR
        )
        fun currentZone(): TimeZone = deprecatedToError()
    }
}

/**
 * Creates a [SystemClock], optionally overriding the system's default time zone with another [zone].
 */
fun SystemClock(zone: TimeZone = TimeZone.systemDefault()): SystemClock = createSystemClock(zone)
