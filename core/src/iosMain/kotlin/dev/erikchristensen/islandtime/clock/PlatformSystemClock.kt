package dev.erikchristensen.islandtime.clock

import dev.erikchristensen.islandtime.TimeZone
import dev.erikchristensen.islandtime.interval.LongMilliseconds
import dev.erikchristensen.islandtime.interval.microseconds
import dev.erikchristensen.islandtime.interval.seconds
import kotlinx.cinterop.*
import platform.Foundation.*
import platform.posix.gettimeofday
import platform.posix.timeval

internal actual object PlatformSystemClock {
    actual fun currentZone() = TimeZone(NSTimeZone.localTimeZone.name)

    actual fun read(): LongMilliseconds {
        return memScoped {
            val posixTime = alloc<timeval>()
            gettimeofday(posixTime.ptr, null)
            posixTime.tv_sec.seconds + posixTime.tv_usec.microseconds.inWholeMilliseconds
        }
    }
}