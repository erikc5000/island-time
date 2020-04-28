package io.islandtime.format

import io.islandtime.TimeZone
import io.islandtime.locale.Locale
import platform.Foundation.*

actual object PlatformTimeZoneTextProvider : TimeZoneTextProvider {
    override fun textFor(zone: TimeZone, style: TimeZoneTextStyle, locale: Locale): String? {
        return if (zone is TimeZone.Region) {
            NSTimeZone.timeZoneWithName(zone.id)?.run {
                val darwinStyle = style.toNSTimeZoneNameStyle()
                localizedName(darwinStyle, locale)
            }
        } else {
            null
        }
    }
}

private fun TimeZoneTextStyle.toNSTimeZoneNameStyle(): NSTimeZoneNameStyle {
    return when (this) {
        TimeZoneTextStyle.STANDARD -> NSTimeZoneNameStyle.NSTimeZoneNameStyleStandard
        TimeZoneTextStyle.SHORT_STANDARD -> NSTimeZoneNameStyle.NSTimeZoneNameStyleShortStandard
        TimeZoneTextStyle.DAYLIGHT_SAVING -> NSTimeZoneNameStyle.NSTimeZoneNameStyleDaylightSaving
        TimeZoneTextStyle.SHORT_DAYLIGHT_SAVING ->
            NSTimeZoneNameStyle.NSTimeZoneNameStyleShortDaylightSaving
        TimeZoneTextStyle.GENERIC -> NSTimeZoneNameStyle.NSTimeZoneNameStyleGeneric
        TimeZoneTextStyle.SHORT_GENERIC -> NSTimeZoneNameStyle.NSTimeZoneNameStyleShortGeneric
    }
}