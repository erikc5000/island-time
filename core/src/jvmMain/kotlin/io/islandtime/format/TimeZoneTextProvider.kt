package io.islandtime.format

import io.islandtime.TimeZone
import java.util.*

actual object PlatformTimeZoneTextProvider : TimeZoneTextProvider {
    override fun timeZoneTextFor(zone: TimeZone, style: TimeZoneTextStyle, locale: Locale): String? {
        return if (zone is TimeZone.FixedOffset || !zone.isValid || style.isGeneric()) {
            null
        } else {
            val javaTzStyle = if (style.isShort()) java.util.TimeZone.SHORT else java.util.TimeZone.LONG
            val isDaylightSaving = style.isDaylightSaving()

            return java.util.TimeZone.getTimeZone(zone.id).getDisplayName(isDaylightSaving, javaTzStyle, locale)
        }
    }
}
