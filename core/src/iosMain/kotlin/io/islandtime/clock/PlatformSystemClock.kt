package io.islandtime.clock

import io.islandtime.TimeZone
import io.islandtime.measures.LongMilliseconds
import io.islandtime.measures.microseconds
import io.islandtime.measures.seconds
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
            posixTime.tv_sec.seconds + posixTime.tv_usec.microseconds.inMilliseconds
        }
    }
}