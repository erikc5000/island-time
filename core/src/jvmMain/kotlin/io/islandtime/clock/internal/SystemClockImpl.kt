package io.islandtime.clock.internal

import io.islandtime.Instant
import io.islandtime.PlatformInstant
import io.islandtime.TimeZone
import io.islandtime.clock.SystemClock
import io.islandtime.jvm.toIslandInstant
import io.islandtime.measures.LongMilliseconds
import io.islandtime.measures.milliseconds
import java.time.Clock as JavaClock

private val javaClock = JavaClock.systemUTC()

internal actual fun createSystemClock(zone: TimeZone): SystemClock {
    return object : SystemClock() {
        override val zone: TimeZone = zone
        override fun readMilliseconds(): LongMilliseconds = System.currentTimeMillis().milliseconds
        override fun readInstant(): Instant = readPlatformInstant().toIslandInstant()
        override fun readPlatformInstant(): PlatformInstant = javaClock.instant()
    }
}