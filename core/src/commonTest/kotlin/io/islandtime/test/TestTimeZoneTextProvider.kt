package io.islandtime.test

import io.islandtime.TimeZone
import io.islandtime.format.TimeZoneTextProvider
import io.islandtime.format.TimeZoneTextStyle
import io.islandtime.locale.Locale

object TestTimeZoneTextProvider : TimeZoneTextProvider {
    override fun textFor(zone: TimeZone, style: TimeZoneTextStyle, locale: Locale): String? {
        return when (zone.id) {
            "America/New_York" -> when (style) {
                TimeZoneTextStyle.SHORT_STANDARD -> "EST"
                TimeZoneTextStyle.STANDARD -> "Eastern Standard Time"
                TimeZoneTextStyle.SHORT_DAYLIGHT_SAVING -> "EDT"
                TimeZoneTextStyle.DAYLIGHT_SAVING -> "Eastern Daylight Time"
                TimeZoneTextStyle.SHORT_GENERIC -> "ET"
                TimeZoneTextStyle.GENERIC -> "Eastern Time"
            }
            else -> null
        }
    }
}