package io.islandtime.operators

import io.islandtime.*
import io.islandtime.measures.TimeUnit.*

/**
 * Return a copy of this time, truncated to the `hour` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(HOURS)", "io.islandtime.measures.TimeUnit.HOURS"),
    DeprecationLevel.ERROR
)
fun Time.truncatedToHours() = truncatedTo(HOURS)

/**
 * Return a copy of this time, truncated to the `minute` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MINUTES)", "io.islandtime.measures.TimeUnit.MINUTES"),
    DeprecationLevel.ERROR
)
fun Time.truncatedToMinutes() = truncatedTo(MINUTES)

/**
 * Return a copy of this time, truncated to the `second` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(SECONDS)", "io.islandtime.measures.TimeUnit.SECONDS"),
    DeprecationLevel.ERROR
)
fun Time.truncatedToSeconds() = truncatedTo(SECONDS)

/**
 * Return a copy of this time with the `nanosecond` value truncated to milliseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MILLISECONDS)", "io.islandtime.measures.TimeUnit.MILLISECONDS"),
    DeprecationLevel.ERROR
)
fun Time.truncatedToMilliseconds() = truncatedTo(MILLISECONDS)

/**
 * Return a copy of this time with the `nanosecond` value truncated to microseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MICROSECONDS)", "io.islandtime.measures.TimeUnit.MICROSECONDS"),
    DeprecationLevel.ERROR
)
fun Time.truncatedToMicroseconds() = truncatedTo(MICROSECONDS)

/**
 * Return a copy of this date-time, truncated to the `hour` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(HOURS)", "io.islandtime.measures.TimeUnit.HOURS"),
    DeprecationLevel.ERROR
)
fun DateTime.truncatedToHours() = truncatedTo(HOURS)

/**
 * Return a copy of this date-time, truncated to the `minute` value. ll smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(TimeUnit.MINUTES)", "io.islandtime.measures.TimeUnit"),
    DeprecationLevel.ERROR
)
fun DateTime.truncatedToMinutes() = truncatedTo(MINUTES)

/**
 * Return a copy of this date-time, truncated to the `second` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(SECONDS)", "io.islandtime.measures.TimeUnit.SECONDS"),
    DeprecationLevel.ERROR
)
fun DateTime.truncatedToSeconds() = truncatedTo(SECONDS)

/**
 * Return a copy of this date-time with the `nanosecond` value truncated to milliseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MILLISECONDS)", "io.islandtime.measures.TimeUnit.MILLISECONDS"),
    DeprecationLevel.ERROR
)
fun DateTime.truncatedToMilliseconds() = truncatedTo(MILLISECONDS)

/**
 * Return a copy of this date-time with the `nanosecond` value truncated to microseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MICROSECONDS)", "io.islandtime.measures.TimeUnit.MICROSECONDS"),
    DeprecationLevel.ERROR
)
fun DateTime.truncatedToMicroseconds() = truncatedTo(MICROSECONDS)

/**
 * Return a copy of this date-time, truncated to the `hour` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(HOURS)", "io.islandtime.measures.TimeUnit.HOURS"),
    DeprecationLevel.ERROR
)
fun ZonedDateTime.truncatedToHours() = truncatedTo(HOURS)

/**
 * Return a copy of this date-time, truncated to the `minute` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MINUTES)", "io.islandtime.measures.TimeUnit.MINUTES"),
    DeprecationLevel.ERROR
)
fun ZonedDateTime.truncatedToMinutes() = truncatedTo(MINUTES)

/**
 * Return a copy of this date-time, truncated to the `second` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(SECONDS)", "io.islandtime.measures.TimeUnit.SECONDS"),
    DeprecationLevel.ERROR
)
fun ZonedDateTime.truncatedToSeconds() = truncatedTo(SECONDS)

/**
 * Return a copy of this date-time with the `nanosecond` value truncated to milliseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MILLISECONDS)", "io.islandtime.measures.TimeUnit.MILLISECONDS"),
    DeprecationLevel.ERROR
)
fun ZonedDateTime.truncatedToMilliseconds() = truncatedTo(MILLISECONDS)

/**
 * Return a copy of this date-time with the `nanosecond` value truncated to microseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MICROSECONDS)", "io.islandtime.measures.TimeUnit.MICROSECONDS"),
    DeprecationLevel.ERROR
)
fun ZonedDateTime.truncatedToMicroseconds() = truncatedTo(MICROSECONDS)

/**
 * Return a copy of this time, truncated to the `hour` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(HOURS)", "io.islandtime.measures.TimeUnit.HOURS"),
    DeprecationLevel.ERROR
)
fun OffsetTime.truncatedToHours() = truncatedTo(HOURS)

/**
 * Return a copy of this time, truncated to the `minute` value. ll smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MINUTES)", "io.islandtime.measures.TimeUnit.MINUTES"),
    DeprecationLevel.ERROR
)
fun OffsetTime.truncatedToMinutes() = truncatedTo(MINUTES)

/**
 * Return a copy of this time, truncated to the `second` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(SECONDS)", "io.islandtime.measures.TimeUnit.SECONDS"),
    DeprecationLevel.ERROR
)
fun OffsetTime.truncatedToSeconds() = truncatedTo(SECONDS)

/**
 * Return a copy of this time with the `nanosecond` value truncated to milliseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MILLISECONDS)", "io.islandtime.measures.TimeUnit.MILLISECONDS"),
    DeprecationLevel.ERROR
)
fun OffsetTime.truncatedToMilliseconds() = truncatedTo(MILLISECONDS)

/**
 * Return a copy of this time with the `nanosecond` value truncated to microseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MICROSECONDS)", "io.islandtime.measures.TimeUnit.MICROSECONDS"),
    DeprecationLevel.ERROR
)
fun OffsetTime.truncatedToMicroseconds() = truncatedTo(MICROSECONDS)

/**
 * Return a copy of this date-time, truncated to the `hour` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(HOURS)", "io.islandtime.measures.TimeUnit.HOURS"),
    DeprecationLevel.ERROR
)
fun OffsetDateTime.truncatedToHours() = truncatedTo(HOURS)

/**
 * Return a copy of this date-time, truncated to the `minute` value. ll smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MINUTES)", "io.islandtime.measures.TimeUnit.MINUTES"),
    DeprecationLevel.ERROR
)
fun OffsetDateTime.truncatedToMinutes() = truncatedTo(MINUTES)

/**
 * Return a copy of this date-time, truncated to the `second` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(SECONDS)", "io.islandtime.measures.TimeUnit.SECONDS"),
    DeprecationLevel.ERROR
)
fun OffsetDateTime.truncatedToSeconds() = truncatedTo(SECONDS)

/**
 * Return a copy of this date-time with the `nanosecond` value truncated to milliseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MILLISECONDS)", "io.islandtime.measures.TimeUnit.MILLISECONDS"),
    DeprecationLevel.ERROR
)
fun OffsetDateTime.truncatedToMilliseconds() = truncatedTo(MILLISECONDS)

/**
 * Return a copy of this date-time with the `nanosecond` value truncated to microseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MICROSECONDS)", "io.islandtime.measures.TimeUnit.MICROSECONDS"),
    DeprecationLevel.ERROR
)
fun OffsetDateTime.truncatedToMicroseconds() = truncatedTo(MICROSECONDS)
