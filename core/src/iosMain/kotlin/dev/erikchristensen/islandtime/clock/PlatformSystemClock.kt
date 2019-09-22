package dev.erikchristensen.islandtime.clock

import dev.erikchristensen.islandtime.TimeZone
import dev.erikchristensen.islandtime.internal.MILLISECONDS_PER_SECOND
import dev.erikchristensen.islandtime.interval.milliseconds
import platform.Foundation.*

internal actual object PlatformSystemClock {
    actual fun currentZone() = TimeZone(NSTimeZone.localTimeZone.name)
    actual fun read() = (NSTimeIntervalSince1970 * MILLISECONDS_PER_SECOND).toLong().milliseconds
}