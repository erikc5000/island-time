package io.islandtime.format

import io.islandtime.locale.Locale
import platform.Foundation.*

internal actual fun createDefaultTimeZoneNameProvider(): TimeZoneNameProvider = DarwinTimeZoneNameProvider()

private class DarwinTimeZoneNameProvider : TimeZoneNameProvider {
    override fun getNameFor(regionId: String, style: TimeZoneNameStyle, locale: Locale): String? {
        return NSTimeZone.timeZoneWithName(regionId)?.run {
            val darwinStyle = style.toNSTimeZoneNameStyle()
            localizedName(darwinStyle, locale)
        }
    }
}

private fun TimeZoneNameStyle.toNSTimeZoneNameStyle(): NSTimeZoneNameStyle {
    return when (this) {
        TimeZoneNameStyle.LONG_STANDARD -> NSTimeZoneNameStyle.NSTimeZoneNameStyleStandard
        TimeZoneNameStyle.SHORT_STANDARD -> NSTimeZoneNameStyle.NSTimeZoneNameStyleShortStandard
        TimeZoneNameStyle.LONG_DAYLIGHT -> NSTimeZoneNameStyle.NSTimeZoneNameStyleDaylightSaving
        TimeZoneNameStyle.SHORT_DAYLIGHT -> NSTimeZoneNameStyle.NSTimeZoneNameStyleShortDaylightSaving
        TimeZoneNameStyle.LONG_GENERIC -> NSTimeZoneNameStyle.NSTimeZoneNameStyleGeneric
        TimeZoneNameStyle.SHORT_GENERIC -> NSTimeZoneNameStyle.NSTimeZoneNameStyleShortGeneric
    }
}
