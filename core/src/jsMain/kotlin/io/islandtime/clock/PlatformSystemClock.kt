package io.islandtime.clock

import io.islandtime.*
import io.islandtime.intl.DateTimeFormat
import io.islandtime.measures.IntMinutes
import io.islandtime.measures.milliseconds
import io.islandtime.measures.minutes
import io.islandtime.zone.IANARules
import io.islandtime.zone.InvalidTimeZone
import kotlin.js.Date

internal actual object PlatformSystemClock {
    actual fun currentZone(): TimeZone {
        //TODO we better to check for Intl API support.
        //  function hasIntl() {
        //  try {
        //    return typeof Intl !== "undefined" && Intl.DateTimeFormat;
        //  } catch (e) {
        //    return false;
        //  }
        //  }
        //  if not found we can use FixedOffset
        //  TimeZone.FixedOffset("${kotlin.js.Date().getTimezoneOffset()}")


        return findTimeZone(DateTimeFormat().resolvedOptions().timeZone)
    }

    private fun findTimeZone(regionId: String): TimeZone {
        val lowered = regionId.toLowerCase()
        return if (lowered === "local") TimeZone.Region(lowered)
        else if (lowered === "utc" || lowered === "gmt") TimeZone.UTC
        else if (parseGMTOffset(regionId) != null) {
            // handle Etc/GMT-4, which V8 chokes on
            val offset = parseGMTOffset(regionId)!!
            TimeZone.FixedOffset(offset)
        } else if (IANARules.isValidSpecifier(lowered)) {
            TimeZone.Region(regionId);
        } else {
            parseFixedZoneSpecifier(lowered)
                ?: throw InvalidTimeZone("region= $regionId is not a valid time zone")
        }
    }

    private fun parseFixedZoneSpecifier(regionId: String): TimeZone.FixedOffset? {
        val r = regionId.match("/^utc(?:([+-]\\d{1,2})(?::(\\d{2}))?)?\$/i") ?: return null
        return TimeZone.FixedOffset(signedOffset(r[1], r[2]).asUtcOffset())
    }

    private fun signedOffset(offHour: String, offMinute: String): IntMinutes {
        var offHour = offHour.toIntOrNull(10)

        // don't || this because we want to preserve -0
        // kotlin conversion to null TODO not sure if we have to check for -0 too
        if (offHour == null) {
            offHour = 0;
        }

        val offMin = offMinute.toIntOrNull(10) ?: 0
        val offMinSigned = if (offHour < 0 || offHour == -0) -offMin else offMin
        return (offHour * 60 + offMinSigned).minutes;
    }

    private fun parseGMTOffset(regionId: String): UtcOffset? {
        val match = regionId.match("/^Etc\\/GMT([+-]\\d{1,2})\$/i");
        if (match != null) {
            return match[1]
                .toIntOrNull()
                ?.let {
                    -60 * it
                }
                ?.minutes
                ?.asUtcOffset();
        }
        return null
    }

    actual fun read() =
        Date.now().toLong().milliseconds

}