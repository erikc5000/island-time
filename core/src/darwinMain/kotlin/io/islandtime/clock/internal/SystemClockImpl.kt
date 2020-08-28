package io.islandtime.clock.internal

import io.islandtime.Instant
import io.islandtime.PlatformInstant
import io.islandtime.TimeZone
import io.islandtime.clock.SystemClock
import io.islandtime.measures.*
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.Foundation.NSDate
import platform.posix.gettimeofday
import platform.posix.timeval

internal actual fun createSystemClock(zone: TimeZone): SystemClock {
    return object : SystemClock() {
        override val zone: TimeZone = zone

        override fun readMilliseconds(): LongMilliseconds {
            return readSystemTime { seconds, microseconds -> seconds + microseconds.inMilliseconds }
        }

        override fun readInstant(): Instant {
            return readSystemTime { seconds, microseconds -> Instant(seconds, microseconds.inNanoseconds) }
        }

        override fun readPlatformInstant(): PlatformInstant = NSDate()
    }
}

private inline fun <T> readSystemTime(action: (seconds: LongSeconds, microseconds: IntMicroseconds) -> T): T {
    return memScoped {
        val posixTime = alloc<timeval>()
        gettimeofday(posixTime.ptr, null)
        action(posixTime.tv_sec.convert<Long>().seconds, posixTime.tv_usec.microseconds)
    }
}
