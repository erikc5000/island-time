package io.islandtime

import io.islandtime.measures.*

/**
 * An object that can be placed exactly on a timeline
 */
interface TimePoint<T> {
    /**
     * The number of seconds since the Unix epoch of 1970-01-01T00:00Z
     */
    val secondsSinceUnixEpoch: LongSeconds

    /**
     * The number of additional nanoseconds on top of the seconds since the Unix epoch
     */
    val nanoOfSecondsSinceUnixEpoch: IntNanoseconds

    /**
     * The number of milliseconds since the Unix epoch of 1970-01-01T00:00Z
     */
    val millisecondsSinceUnixEpoch: LongMilliseconds

    /**
     * The second of the Unix epoch
     */
    val unixEpochSecond: Long get() = secondsSinceUnixEpoch.value

    /**
     * The nanosecond of the second of the Unix epoch
     */
    val unixEpochNanoOfSecond: Int get() = nanoOfSecondsSinceUnixEpoch.value

    /**
     * The millisecond of the Unix epoch
     */
    val unixEpochMillisecond: Long get() = millisecondsSinceUnixEpoch.value

    /**
     * Check if this time point represent the same instant as [other]. Unlike the equals operator, equality is
     * determined solely by timeline order.
     */
    fun <U> isSameInstantAs(other: TimePoint<U>): Boolean {
        return unixEpochSecond == other.unixEpochSecond && unixEpochNanoOfSecond == other.unixEpochNanoOfSecond
    }

    /**
     * Time points can be compared to other time points based on timeline order, but aren't required to implement the
     * [Comparable] interface since they don't necessarily have a natural order that's consistent with equals,
     */
    operator fun <U> compareTo(other: TimePoint<U>): Int {
        val second = unixEpochSecond
        val otherSecond = other.unixEpochSecond

        val secondDiff = second.compareTo(otherSecond)

        return if (secondDiff != 0) {
            secondDiff
        } else {
            unixEpochNanoOfSecond - other.unixEpochNanoOfSecond
        }
    }

    operator fun plus(hours: IntHours): T
    operator fun plus(hours: LongHours): T
    operator fun plus(minutes: IntMinutes): T
    operator fun plus(minutes: LongMinutes): T
    operator fun plus(seconds: IntSeconds): T
    operator fun plus(seconds: LongSeconds): T
    operator fun plus(milliseconds: IntMilliseconds): T
    operator fun plus(milliseconds: LongMilliseconds): T
    operator fun plus(microseconds: IntMicroseconds): T
    operator fun plus(microseconds: LongMicroseconds): T
    operator fun plus(nanoseconds: IntNanoseconds): T
    operator fun plus(nanoseconds: LongNanoseconds): T

    operator fun minus(hours: IntHours): T
    operator fun minus(hours: LongHours): T
    operator fun minus(minutes: IntMinutes): T
    operator fun minus(minutes: LongMinutes): T
    operator fun minus(seconds: IntSeconds): T
    operator fun minus(seconds: LongSeconds): T
    operator fun minus(milliseconds: IntMilliseconds): T
    operator fun minus(milliseconds: LongMilliseconds): T
    operator fun minus(microseconds: IntMicroseconds): T
    operator fun minus(microseconds: LongMicroseconds): T
    operator fun minus(nanoseconds: IntNanoseconds): T
    operator fun minus(nanoseconds: LongNanoseconds): T

    companion object {
        val TIMELINE_ORDER = compareBy<TimePoint<*>> { it.unixEpochSecond }
            .thenBy { it.unixEpochNanoOfSecond }
    }
}

/**
 * A day-based measure of time
 */
//interface DayBased<T> {
//    val year: Int
//    val month: Month
//    val dayOfMonth: Int
//
//    operator fun compareTo(other: DayBased<T>): Int {
//        val yearDiff = year - other.year
//
//        return if (yearDiff != 0) {
//            yearDiff
//        } else {
//            val monthDiff = month.ordinal - other.month.ordinal
//
//            if (monthDiff != 0) {
//                monthDiff
//            } else {
//                dayOfMonth - other.dayOfMonth
//            }
//        }
//    }
//
//    operator fun plus(years: IntYears): T
//    operator fun plus(years: LongYears): T
//    operator fun plus(months: IntMonths): T
//    operator fun plus(months: LongMonths): T
//    operator fun plus(days: IntDays): T
//    operator fun plus(days: LongDays): T
//
//    operator fun minus(years: IntYears): T
//    operator fun minus(years: LongYears): T
//    operator fun minus(months: IntMonths): T
//    operator fun minus(months: LongMonths): T
//    operator fun minus(days: IntDays): T
//    operator fun minus(days: LongDays): T
//
////    val daysSinceUnixEpoch: LongDays get() = unixEpochDay.days
////    val unixEpochDay: Long
//}

//internal inline val BaseDate.monthsSinceYear0 get() = year * 12 + month.ordinal