package io.islandtime.internal

import io.islandtime.TimeZone
import io.islandtime.darwin.toIslandTimeZone
import platform.Foundation.NSTimeZone
import platform.Foundation.localTimeZone

internal actual fun systemDefaultTimeZone(): TimeZone = NSTimeZone.localTimeZone.toIslandTimeZone()