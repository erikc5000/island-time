package dev.erikchristensen.islandtime.tz

import dev.erikchristensen.islandtime.*
import dev.erikchristensen.islandtime.internal.MILLISECONDS_PER_SECOND
import dev.erikchristensen.islandtime.interval.seconds
import platform.Foundation.*

actual object PlatformDefault : TimeZoneRulesProvider {
    private val cachedRegionIds = (NSTimeZone.knownTimeZoneNames as List<String>).toSet()

    override fun getAvailableRegionIds() = cachedRegionIds

    override fun getRules(regionId: String): TimeZoneRules {
        return IosTimeZoneRules(
            NSTimeZone.timeZoneWithName(regionId)
                ?: throw TimeZoneRulesException("No time zone exists with region ID '$regionId'")
        )
    }
}

private class IosTimeZoneRules(
    private val timeZone: NSTimeZone
) : TimeZoneRules {

    val calendar = NSCalendar(NSCalendarIdentifierISO8601).apply {
        timeZone = NSTimeZone.timeZoneWithName("Etc/UTC")
            ?: throw TimeZoneRulesException("Missing UTC time zone")
    }

    override fun isValidOffset(dateTime: DateTime, offset: UtcOffset): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun offsetAt(dateTime: DateTime): UtcOffset {
        val dateComponents = dateTime.toDateComponents(timeZone)
        val date = calendar.dateFromComponents(dateComponents)
        return offsetAt(date ?: throw TimeZoneRulesException("Invalid date time: '$dateTime'"))
    }

    override fun offsetAt(instant: Instant): UtcOffset {
        val date = NSDate.dateWithTimeIntervalSince1970(instant.toTimeInterval())
        return offsetAt(date)
    }

    override fun transitionAt(dateTime: DateTime): TimeZoneOffsetTransition? {
        return null
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun validOffsetsAt(dateTime: DateTime): List<UtcOffset> {
        val dateComponents = dateTime.toDateComponents(timeZone)
        val date = calendar.dateFromComponents(dateComponents)

        // TODO: Doesn't deal with multiple offsets correctly
        return if (date == null) {
            emptyList()
        } else {
            listOf(offsetAt(date))
        }
    }

    private fun offsetAt(date: NSDate): UtcOffset {
        return timeZone.secondsFromGMTForDate(date).toInt().seconds.asUtcOffset()
    }
}

private fun DateTime.toDateComponents(nsTimeZone: NSTimeZone? = null): NSDateComponents {
    return NSDateComponents().apply {
        year = this@toDateComponents.year.toLong()
        month = this@toDateComponents.month.number.toLong()
        day = this@toDateComponents.dayOfMonth.toLong()
        hour = this@toDateComponents.hour.toLong()
        minute = this@toDateComponents.minute.toLong()
        second = this@toDateComponents.second.toLong()
        nanosecond = this@toDateComponents.nanoOfSecond.toLong()

        if (nsTimeZone != null) {
            timeZone = nsTimeZone
        }
    }
}

private fun Instant.toTimeInterval(): NSTimeInterval {
    return unixEpochMilliseconds.value.toDouble() / MILLISECONDS_PER_SECOND
}