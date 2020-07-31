package io.islandtime.internal

import io.islandtime.Instant
import io.islandtime.PlatformInstant
import io.islandtime.TimeZone

internal expect fun systemDefaultTimeZone(): TimeZone

internal expect val PlatformInstant.secondOfUnixEpoch: Long
internal expect val PlatformInstant.nanosecond: Int
internal expect fun PlatformInstant.toIslandInstant(): Instant
internal expect fun Instant.toPlatformInstant(): PlatformInstant