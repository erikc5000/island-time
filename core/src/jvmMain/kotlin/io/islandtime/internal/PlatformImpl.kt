@file:Suppress("NewApi")

package io.islandtime.internal

import io.islandtime.Instant
import io.islandtime.PlatformInstant
import io.islandtime.TimeZone
import io.islandtime.jvm.toIslandInstant
import io.islandtime.jvm.toJavaInstant

internal actual val PlatformInstant.secondOfUnixEpoch: Long get() = epochSecond
internal actual val PlatformInstant.nanosecond: Int get() = nano
internal actual fun PlatformInstant.toIslandInstant(): Instant = this.toIslandInstant()
internal actual fun Instant.toPlatformInstant(): PlatformInstant = this.toJavaInstant()

internal actual fun systemDefaultTimeZone(): TimeZone = java.util.TimeZone.getDefault().toIslandTimeZone()

private var lastDefaultTimeZone: Pair<java.util.TimeZone, TimeZone>? = null

internal fun java.util.TimeZone.toIslandTimeZone(): TimeZone {
    lastDefaultTimeZone?.let { (javaZone, islandZone) ->
        if (this == javaZone) {
            return islandZone
        }
    }

    val id = id
    val islandZone = if (id.startsWith("GMT")) {
        if (id.length == 3) {
            TimeZone.Region(id)
        } else {
            TimeZone.FixedOffset(id.substring(3))
        }
    } else {
        TimeZone.Region(id)
    }

    lastDefaultTimeZone = this to islandZone
    return islandZone
}