package io.islandtime.internal

import io.islandtime.TimeZone
import io.islandtime.darwin.toIslandTimeZone
import platform.Foundation.NSTimeZone

internal actual fun systemDefaultTimeZone(): TimeZone = NSTimeZone.localTimeZone.toIslandTimeZone()