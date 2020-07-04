package io.islandtime.format

import io.islandtime.TimeZone
import io.islandtime.locale.Locale
import io.islandtime.objectOf
import kotlin.js.Date
import kotlin.js.Math

actual object PlatformTimeZoneTextProvider : TimeZoneTextProvider {
    override fun timeZoneTextFor(zone: TimeZone, style: TimeZoneTextStyle, locale: Locale): String? {
        val zoneStyle = when (style) {
            TimeZoneTextStyle.GENERIC,
            TimeZoneTextStyle.SHORT_GENERIC -> null
            TimeZoneTextStyle.STANDARD,
            TimeZoneTextStyle.DAYLIGHT_SAVING -> "long"
            TimeZoneTextStyle.SHORT_STANDARD,
            TimeZoneTextStyle.SHORT_DAYLIGHT_SAVING -> "short"
        }

        return try {
            Date().toLocaleDateString(locale.locale, objectOf {
                timeZone = zone.id
                timeZoneName = zoneStyle
            }).split(",")[1]
        } catch (e: Exception) {
            ""
        }
    }
}