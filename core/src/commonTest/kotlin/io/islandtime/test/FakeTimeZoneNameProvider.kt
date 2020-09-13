package io.islandtime.test

import io.islandtime.format.TimeZoneNameProvider
import io.islandtime.format.TimeZoneNameStyle
import io.islandtime.locale.Locale

object FakeTimeZoneNameProvider : TimeZoneNameProvider {
    override fun getNameFor(regionId: String, style: TimeZoneNameStyle, locale: Locale): String? {
        return when (regionId) {
            "America/New_York", "America/Detroit", "America/Toronto" -> when (style) {
                TimeZoneNameStyle.SHORT_STANDARD -> "EST"
                TimeZoneNameStyle.LONG_STANDARD -> "Eastern Standard Time"
                TimeZoneNameStyle.SHORT_DAYLIGHT -> "EDT"
                TimeZoneNameStyle.LONG_DAYLIGHT -> "Eastern Daylight Time"
                TimeZoneNameStyle.SHORT_GENERIC -> "ET"
                TimeZoneNameStyle.LONG_GENERIC -> "Eastern Time"
            }
            "America/Denver" -> when (style) {
                TimeZoneNameStyle.SHORT_STANDARD -> "MST"
                TimeZoneNameStyle.LONG_STANDARD -> "Mountain Standard Time"
                TimeZoneNameStyle.SHORT_DAYLIGHT -> "MDT"
                TimeZoneNameStyle.LONG_DAYLIGHT -> "Mountain Daylight Time"
                TimeZoneNameStyle.SHORT_GENERIC -> "MT"
                TimeZoneNameStyle.LONG_GENERIC -> "Mountain Time"
            }
            "Etc/GMT" -> when (style) {
                TimeZoneNameStyle.SHORT_STANDARD -> "GMT"
                TimeZoneNameStyle.LONG_STANDARD -> "Greenwich Mean Time"
                TimeZoneNameStyle.SHORT_DAYLIGHT -> "GMT"
                TimeZoneNameStyle.LONG_DAYLIGHT -> "Greenwich Mean Time"
                TimeZoneNameStyle.SHORT_GENERIC -> "GMT"
                TimeZoneNameStyle.LONG_GENERIC -> "Greenwich Mean Time"
            }
            "Test/EmptyString" -> ""
            else -> null
        }
    }
}
