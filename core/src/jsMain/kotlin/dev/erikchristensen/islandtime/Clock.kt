package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.interval.milliseconds
import kotlin.js.Date

internal actual class SystemClock actual constructor() : Clock {
    actual override val timeZone: TimeZone
        get() = TimeZone(js("Intl.DateTimeFormat().resolvedOptions().timeZone") as String)

    actual override fun instant(): Instant {
        return Instant.fromMillisecondsSinceUnixEpoch(Date.now().toLong().milliseconds)
    }
}