package io.islandtime.internal

import io.islandtime.Instant
import io.islandtime.PlatformInstant
import io.islandtime.TimeZone

internal expect fun systemDefaultTimeZone(): TimeZone

internal expect fun PlatformInstant.toIslandInstant(): Instant
internal expect fun Instant.toPlatformInstant(): PlatformInstant