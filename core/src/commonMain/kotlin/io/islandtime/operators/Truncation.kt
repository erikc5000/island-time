package io.islandtime.operators

import io.islandtime.*
import io.islandtime.internal.NANOSECONDS_PER_MICROSECOND
import io.islandtime.internal.NANOSECONDS_PER_MILLISECOND

/**
 * Return a copy of this time, truncated to the `hour` value. All smaller components will be replaced with zero.
 */
fun Time.truncatedToHours() = copy(minute = 0, second = 0, nanosecond = 0)

/**
 * Return a copy of this time, truncated to the `minute` value. All smaller components will be replaced with zero.
 */
fun Time.truncatedToMinutes() = copy(second = 0, nanosecond = 0)

/**
 * Return a copy of this time, truncated to the `second` value. All smaller components will be replaced with zero.
 */
fun Time.truncatedToSeconds() = copy(nanosecond = 0)

/**
 * Return a copy of this time with the `nanosecond` value truncated to milliseconds.
 */
fun Time.truncatedToMilliseconds() =
    copy(nanosecond = this.nanosecond / NANOSECONDS_PER_MILLISECOND * NANOSECONDS_PER_MILLISECOND)

/**
 * Return a copy of this time with the `nanosecond` value truncated to microseconds.
 */
fun Time.truncatedToMicroseconds() =
    copy(nanosecond = this.nanosecond / NANOSECONDS_PER_MICROSECOND * NANOSECONDS_PER_MICROSECOND)

/**
 * Return a copy of this date-time, truncated to the `hour` value. All smaller components will be replaced with zero.
 */
fun DateTime.truncatedToHours() = copy(time = time.truncatedToHours())

/**
 * Return a copy of this date-time, truncated to the `minute` value. ll smaller components will be replaced with zero.
 */
fun DateTime.truncatedToMinutes() = copy(time = time.truncatedToMinutes())

/**
 * Return a copy of this date-time, truncated to the `second` value. All smaller components will be replaced with zero.
 */
fun DateTime.truncatedToSeconds() = copy(time = time.truncatedToSeconds())

/**
 * Return a copy of this date-time with the `nanosecond` value truncated to milliseconds.
 */
fun DateTime.truncatedToMilliseconds() = copy(time = time.truncatedToMilliseconds())

/**
 * Return a copy of this date-time with the `nanosecond` value truncated to microseconds.
 */
fun DateTime.truncatedToMicroseconds() = copy(time = time.truncatedToMicroseconds())

/**
 * Return a copy of this date-time, truncated to the `hour` value. All smaller components will be replaced with zero.
 */
fun ZonedDateTime.truncatedToHours() = copy(dateTime = dateTime.truncatedToHours())

/**
 * Return a copy of this date-time, truncated to the `minute` value. ll smaller components will be replaced with zero.
 */
fun ZonedDateTime.truncatedToMinutes() = copy(dateTime = dateTime.truncatedToMinutes())

/**
 * Return a copy of this date-time, truncated to the `second` value. All smaller components will be replaced with zero.
 */
fun ZonedDateTime.truncatedToSeconds() = copy(dateTime = dateTime.truncatedToSeconds())

/**
 * Return a copy of this date-time with the `nanosecond` value truncated to milliseconds.
 */
fun ZonedDateTime.truncatedToMilliseconds() = copy(dateTime = dateTime.truncatedToMilliseconds())

/**
 * Return a copy of this date-time with the `nanosecond` value truncated to microseconds.
 */
fun ZonedDateTime.truncatedToMicroseconds() = copy(dateTime = dateTime.truncatedToMicroseconds())

/**
 * Return a copy of this time, truncated to the `hour` value. All smaller components will be replaced with zero.
 */
fun OffsetTime.truncatedToHours() = copy(time = time.truncatedToHours())

/**
 * Return a copy of this time, truncated to the `minute` value. ll smaller components will be replaced with zero.
 */
fun OffsetTime.truncatedToMinutes() = copy(time = time.truncatedToMinutes())

/**
 * Return a copy of this time, truncated to the `second` value. All smaller components will be replaced with zero.
 */
fun OffsetTime.truncatedToSeconds() = copy(time = time.truncatedToSeconds())

/**
 * Return a copy of this time with the `nanosecond` value truncated to milliseconds.
 */
fun OffsetTime.truncatedToMilliseconds() = copy(time = time.truncatedToMilliseconds())

/**
 * Return a copy of this time with the `nanosecond` value truncated to microseconds.
 */
fun OffsetTime.truncatedToMicroseconds() = copy(time = time.truncatedToMicroseconds())

/**
 * Return a copy of this date-time, truncated to the `hour` value. All smaller components will be replaced with zero.
 */
fun OffsetDateTime.truncatedToHours() = copy(dateTime = dateTime.truncatedToHours())

/**
 * Return a copy of this date-time, truncated to the `minute` value. ll smaller components will be replaced with zero.
 */
fun OffsetDateTime.truncatedToMinutes() = copy(dateTime = dateTime.truncatedToMinutes())

/**
 * Return a copy of this date-time, truncated to the `second` value. All smaller components will be replaced with zero.
 */
fun OffsetDateTime.truncatedToSeconds() = copy(dateTime = dateTime.truncatedToSeconds())

/**
 * Return a copy of this date-time with the `nanosecond` value truncated to milliseconds.
 */
fun OffsetDateTime.truncatedToMilliseconds() = copy(dateTime = dateTime.truncatedToMilliseconds())

/**
 * Return a copy of this date-time with the `nanosecond` value truncated to microseconds.
 */
fun OffsetDateTime.truncatedToMicroseconds() = copy(dateTime = dateTime.truncatedToMicroseconds())