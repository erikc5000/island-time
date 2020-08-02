package io.islandtime.internal

import io.islandtime.Instant
import io.islandtime.PlatformInstant
import io.islandtime.TimeZone
import io.islandtime.darwin.toIslandInstant
import io.islandtime.darwin.toIslandTimeZone
import io.islandtime.darwin.toNSDate
import platform.Foundation.NSTimeZone
import platform.Foundation.localTimeZone

internal actual fun systemDefaultTimeZone(): TimeZone = NSTimeZone.localTimeZone.toIslandTimeZone()

internal actual fun PlatformInstant.toIslandInstant(): Instant = toIslandInstant()
internal actual fun Instant.toPlatformInstant(): PlatformInstant = toNSDate()