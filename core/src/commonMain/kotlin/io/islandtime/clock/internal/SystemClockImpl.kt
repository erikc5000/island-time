package io.islandtime.clock.internal

import io.islandtime.TimeZone
import io.islandtime.clock.SystemClock

internal expect fun createSystemClock(zone: TimeZone): SystemClock
