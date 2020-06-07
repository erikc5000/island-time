package io.islandtime.format

import io.islandtime.TimeZone
import io.islandtime.locale.Locale

actual object PlatformTimeZoneTextProvider : TimeZoneTextProvider {
    override fun timeZoneTextFor(zone: TimeZone, style: TimeZoneTextStyle, locale: Locale): String? {
        //TODO we are using FIXED offset. later we may be able to support timezones
        return null
    }
}