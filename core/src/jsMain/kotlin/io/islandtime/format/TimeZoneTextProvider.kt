package io.islandtime.format

import io.islandtime.TimeZone
import io.islandtime.locale.Locale
import io.islandtime.objectOf
import kotlin.js.Date

actual object PlatformTimeZoneTextProvider : TimeZoneTextProvider {
    override fun timeZoneTextFor(zone: TimeZone, style: TimeZoneTextStyle, locale: Locale): String? {
        //TODO not sure how to support generic ones
        val zoneStyle = when (style) {
            TimeZoneTextStyle.GENERIC,
            TimeZoneTextStyle.STANDARD,
            TimeZoneTextStyle.DAYLIGHT_SAVING -> "long"
            TimeZoneTextStyle.SHORT_STANDARD,
            TimeZoneTextStyle.SHORT_DAYLIGHT_SAVING,
            TimeZoneTextStyle.SHORT_GENERIC -> "short"
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