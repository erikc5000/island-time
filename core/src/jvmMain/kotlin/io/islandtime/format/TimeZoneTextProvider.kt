package io.islandtime.format

import io.islandtime.TimeZone
import java.util.*
import java.util.TimeZone as JavaTimeZone

actual object PlatformTimeZoneTextProvider : TimeZoneTextProvider {
    override fun textFor(zone: TimeZone, style: TimeZoneTextStyle, locale: Locale): String? {
        return if (zone is TimeZone.FixedOffset || !zone.isValid || style.isGeneric()) {
            null
        } else {
            return JavaTimeZone.getTimeZone(zone.id).getDisplayName(
                style.isDaylightSaving(),
                style.toJavaTimeZoneStyle(),
                locale
            )
        }
    }

    private fun TimeZoneTextStyle.toJavaTimeZoneStyle(): Int {
        return if (isShort()) JavaTimeZone.SHORT else JavaTimeZone.LONG
    }
}