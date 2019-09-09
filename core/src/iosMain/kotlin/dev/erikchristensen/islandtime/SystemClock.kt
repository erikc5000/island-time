package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.internal.MILLISECONDS_PER_SECOND
import dev.erikchristensen.islandtime.interval.milliseconds
import platform.Foundation.*

internal actual class SystemClock actual constructor() : Clock {
    actual override val timeZone = TimeZone(NSTimeZone.localTimeZone.name)

    actual override fun instant(): Instant {
       return Instant((NSTimeIntervalSince1970 * MILLISECONDS_PER_SECOND).toLong().milliseconds)
    }
}