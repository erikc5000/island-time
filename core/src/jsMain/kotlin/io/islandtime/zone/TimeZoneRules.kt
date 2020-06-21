@file:Suppress("NewApi")

package io.islandtime.zone


import io.islandtime.*
import io.islandtime.intl.DateTimeFormat
import io.islandtime.measures.*
import kotlin.js.Date

actual object PlatformTimeZoneRulesProvider : TimeZoneRulesProvider {

    override val databaseVersion: String
        get() = "1"

    override val availableRegionIds: Set<String>
        get() = setOf()

    override fun hasRulesFor(regionId: String): Boolean {
        return try {
            DateTimeFormat("en-US", objectOf {
                timeZone = regionId
            }).format()
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun rulesFor(regionId: String): TimeZoneRules {
        val lowered = regionId.toLowerCase()
        return when {
            lowered === "local" -> LocalRules()
            IANARules.isValidSpecifier(lowered) -> {
                IANARules.create(regionId);
            }
            else -> {
                throw InvalidTimeZone("region= $regionId is not a valid time zone")
            }
        }
    }
}


class LocalRules : TimeZoneRules {
    override val hasFixedOffset: Boolean = false

    override fun offsetAt(millisecondsSinceUnixEpoch: LongMilliseconds): UtcOffset {
        return offsetAt(
            Date(
                millisecondsSinceUnixEpoch
                    .toIntMilliseconds()
                    .value
            )
        )
    }

    override fun offsetAt(secondsSinceUnixEpoch: LongSeconds, nanoOfSeconds: IntNanoseconds): UtcOffset {
        //TODO it seems JS does not support nano second
        return offsetAt(secondsSinceUnixEpoch.inMilliseconds)
    }

    override fun offsetAt(instant: Instant): UtcOffset {
        return offsetAt(instant.secondsSinceUnixEpoch.inMilliseconds)
    }

    override fun offsetAt(dateTime: DateTime): UtcOffset = with(dateTime) {
        offsetAt(Date(year, monthNumber, dayOfMonth, hour, minute, second))
    }

    private fun offsetAt(date: Date): UtcOffset =
        (-date.getTimezoneOffset()).minutes.asUtcOffset()

    override fun validOffsetsAt(dateTime: DateTime): List<UtcOffset> {
        TODO("Not yet implemented")
    }

    override fun transitionAt(dateTime: DateTime): TimeZoneOffsetTransition? {
        TODO("Not yet implemented")
    }

    override fun isDaylightSavingsAt(instant: Instant): Boolean {
        //Extracted from momentjs:
        // https://github.com/moment/moment/blob/226799e1f8767a8ab2849ea04f595dc82ce83747/src/lib/units/offset.js#L210
//        offsetAt(instant) > instant.plus(IntDays())
//        (
//            this.utcOffset() > this.clone().month(0).utcOffset() ||
//                this.utcOffset() > this.clone().month(5).utcOffset()
//            )

        //luxon is the same
        // https://github.com/moment/luxon/blob/03852eaa1e2fe9661513629978f8b5f17976904e/src/datetime.js#L1171


        val offset = offsetAt(instant)
        val firstMonthOffset = Date(instant.millisecondOfUnixEpoch)
            .let {
                Date(
                    it.getFullYear(),
                    1,
                    it.getDay(),
                    it.getHours(),
                    it.getMinutes(),
                    it.getSeconds(),
                    it.getMilliseconds()
                )
            }.let {
                offsetAt(it)
            }

        val fifthMonthOffset = Date(instant.millisecondOfUnixEpoch)
            .let {
                Date(
                    it.getFullYear(),
                    5,
                    it.getDay(),
                    it.getHours(),
                    it.getMinutes(),
                    it.getSeconds(),
                    it.getMilliseconds()
                )
            }.let {
                offsetAt(it)
            }

        return offset > firstMonthOffset || offset > fifthMonthOffset
    }

    override fun daylightSavingsAt(instant: Instant): IntSeconds {
        Date(instant.millisecondOfUnixEpoch).getTimezoneOffset()
        Date().getTimezoneOffset()
        TODO("Not yet implemented")
    }
}

class IANARules(
    private val region: String
) : TimeZoneRules {
    companion object {
        fun isValidSpecifier(regionId: String): Boolean {
            return regionId
                .match("^/[A-Za-z_+-]{1,256}(:?\\/[A-Za-z_+-]{1,256}(\\/[A-Za-z_+-]{1,256})?)?/\$")
                ?.isNotEmpty()
                ?: false
        }

        fun create(regionId: String): IANARules =
            IANARules(regionId)
    }

    override val hasFixedOffset: Boolean = false

    override fun offsetAt(millisecondsSinceUnixEpoch: LongMilliseconds): UtcOffset {
        val date = Date(millisecondsSinceUnixEpoch.value)
        return offsetAt(date)
    }

    private fun offsetAt(date: Date): UtcOffset {
        val dtf = DateTimeFormat("en-US", objectOf {
            hour12 = false
            timeZone = region
            year = "numeric"
            month = "2-digit"
            day = "2-digit"
            hour = "2-digit"
            minute = "2-digit"
            second = "2-digit"
        })
        val parts = if (dtf.formatToParts() != null) {
            partsOffset(dtf, date)
        } else {
            hackyOffset(dtf, date)
        } ?: return UtcOffset.ZERO
        // work around https://bugs.chromium.org/p/chromium/issues/detail?id=1025564&can=2&q=%2224%3A00%22%20datetimeformat
        parts[TypeToPosition.hour.position] = parts[TypeToPosition.hour.position].takeIf {
            it != 24
        } ?: 0
        val utc = arrayToUtc(parts)

        var asTS = +date.getTime().toLong()
        val over = asTS % 1000;
        asTS -= if (over >= 0) over else 1000 + over
        val final = (utc - asTS) / (60 * 1000);
        return final.minutes.toIntMinutes().asUtcOffset()
    }

    private fun hackyOffset(dtf: DateTimeFormat, date: Date): IntArray? {
        return dtf
            .format(date)
            .replace("/\u200E/g", "")
            .let { formatted ->
                """(\d+)\/(\d+)\/(\d+),? (\d+):(\d+):(\d+)"""
                    .toRegex()
                    .find(formatted)
            }
            ?.groupValues
            ?.asSequence()
            ?.drop(1)
            ?.map {
                it.toIntOrNull()
            }
            ?.filterNotNull()
            ?.toList()
            ?.takeIf { it.size == 6 }
            ?.let {
                val array = IntArray(it.size)
                it.forEachIndexed { index, i ->
                    array[index] = i
                }
                array
            }
//        return [fYear, fMonth, fDay, fHour, fMinute, fSecond];
    }

    private fun arrayToUtc(arr: IntArray): Long =
        Date
            .UTC(
                arr[TypeToPosition.year.position],
                arr[TypeToPosition.month.position] - 1,
                arr[TypeToPosition.day.position],
                arr[TypeToPosition.hour.position],
                arr[TypeToPosition.minute.position],
                arr[TypeToPosition.second.position],
                0
            )
            .toLong()

    enum class TypeToPosition(
        val position: Int
    ) {
        year(0),
        month(1),
        day(2),
        hour(3),
        minute(4),
        second(5),
        ;
    }

    private fun partsOffset(dtf: DateTimeFormat, date: Date): IntArray {
        val formatted = dtf.formatToParts(date)
        val filled = intArrayOf()
        for (part in formatted) {
            TypeToPosition.values().find { it.name == part.type }?.position?.let {
                filled[it] = part.value.toInt(10)
            }
        }
        return filled;
    }

    override fun offsetAt(secondsSinceUnixEpoch: LongSeconds, nanoOfSeconds: IntNanoseconds): UtcOffset {
        //TODO it seems JS does not support nano second
        return offsetAt(secondsSinceUnixEpoch.inMilliseconds)
    }

    override fun offsetAt(instant: Instant): UtcOffset {
        return offsetAt(instant.secondsSinceUnixEpoch.inMilliseconds)
    }

    override fun offsetAt(dateTime: DateTime): UtcOffset = with(dateTime) {
        offsetAt(Date(year, monthNumber, dayOfMonth, hour, minute, second))
    }

    override fun validOffsetsAt(dateTime: DateTime): List<UtcOffset> {
        TODO("Not yet implemented")
    }

    override fun transitionAt(dateTime: DateTime): TimeZoneOffsetTransition? {
        TODO("Not yet implemented")
    }

    override fun isDaylightSavingsAt(instant: Instant): Boolean {
        val offset = offsetAt(instant)
        val firstMonthOffset = Date(instant.millisecondOfUnixEpoch)
            .let {
                Date(
                    it.getFullYear(),
                    1,
                    it.getDay(),
                    it.getHours(),
                    it.getMinutes(),
                    it.getSeconds(),
                    it.getMilliseconds()
                )
            }.let {
                offsetAt(it)
            }

        val fifthMonthOffset = Date(instant.millisecondOfUnixEpoch)
            .let {
                Date(
                    it.getFullYear(),
                    5,
                    it.getDay(),
                    it.getHours(),
                    it.getMinutes(),
                    it.getSeconds(),
                    it.getMilliseconds()
                )
            }.let {
                offsetAt(it)
            }

        return offset > firstMonthOffset || offset > fifthMonthOffset
    }

    override fun daylightSavingsAt(instant: Instant): IntSeconds {
        TODO("Not Used anywhere")
    }

}

class InvalidTimeZone(message: String) : Throwable(message)