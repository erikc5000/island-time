package io.islandtime.clock

import io.islandtime.*
import io.islandtime.measures.LongMilliseconds
import io.islandtime.measures.milliseconds
import moment.moment
import moment.now

internal actual object PlatformSystemClock {
    //TODO FixedOffset will serve us well at the beginning
    // but with the help of moment-timezone we can support them better func
    actual fun currentZone() = TimeZone.FixedOffset("${kotlin.js.Date().getTimezoneOffset()}")

    actual fun read() = moment.now().toLong().milliseconds

}