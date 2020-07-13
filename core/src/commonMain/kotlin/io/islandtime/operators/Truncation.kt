package io.islandtime.operators

import io.islandtime.*
import io.islandtime.measures.TimeUnit.*

/**
 * Return a copy of this time, truncated to the `hour` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(HOURS)", "io.islandtime.measures.TimeUnit.HOURS"),
    DeprecationLevel.WARNING
)
fun Time.truncatedToHours() = truncatedTo(HOURS)

/**
 * Return a copy of this time, truncated to the `minute` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MINUTES)", "io.islandtime.measures.TimeUnit.MINUTES"),
    DeprecationLevel.WARNING
)
fun Time.truncatedToMinutes() = truncatedTo(MINUTES)

/**
 * Return a copy of this time, truncated to the `second` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(SECONDS)", "io.islandtime.measures.TimeUnit.SECONDS"),
    DeprecationLevel.WARNING
)
fun Time.truncatedToSeconds() = truncatedTo(SECONDS)

/**
 * Return a copy of this time with the `nanosecond` value truncated to milliseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MILLISECONDS)", "io.islandtime.measures.TimeUnit.MILLISECONDS"),
    DeprecationLevel.WARNING
)
fun Time.truncatedToMilliseconds() = truncatedTo(MILLISECONDS)

/**
 * Return a copy of this time with the `nanosecond` value truncated to microseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MICROSECONDS)", "io.islandtime.measures.TimeUnit.MICROSECONDS"),
    DeprecationLevel.WARNING
)
fun Time.truncatedToMicroseconds() = truncatedTo(MICROSECONDS)

/**
 * Return a copy of this date-time, truncated to the `hour` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(HOURS)", "io.islandtime.measures.TimeUnit.HOURS"),
    DeprecationLevel.WARNING
)
fun DateTime.truncatedToHours() = truncatedTo(HOURS)

/**
 * Return a copy of this date-time, truncated to the `minute` value. ll smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(TimeUnit.MINUTES)", "io.islandtime.measures.TimeUnit"),
    DeprecationLevel.WARNING
)
fun DateTime.truncatedToMinutes() = truncatedTo(MINUTES)

/**
 * Return a copy of this date-time, truncated to the `second` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(SECONDS)", "io.islandtime.measures.TimeUnit.SECONDS"),
    DeprecationLevel.WARNING
)
fun DateTime.truncatedToSeconds() = truncatedTo(SECONDS)

/**
 * Return a copy of this date-time with the `nanosecond` value truncated to milliseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MILLISECONDS)", "io.islandtime.measures.TimeUnit.MILLISECONDS"),
    DeprecationLevel.WARNING
)
fun DateTime.truncatedToMilliseconds() = truncatedTo(MILLISECONDS)

/**
 * Return a copy of this date-time with the `nanosecond` value truncated to microseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MICROSECONDS)", "io.islandtime.measures.TimeUnit.MICROSECONDS"),
    DeprecationLevel.WARNING
)
fun DateTime.truncatedToMicroseconds() = truncatedTo(MICROSECONDS)

/**
 * Return a copy of this date-time, truncated to the `hour` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(HOURS)", "io.islandtime.measures.TimeUnit.HOURS"),
    DeprecationLevel.WARNING
)
fun ZonedDateTime.truncatedToHours() = truncatedTo(HOURS)

/**
 * Return a copy of this date-time, truncated to the `minute` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MINUTES)", "io.islandtime.measures.TimeUnit.MINUTES"),
    DeprecationLevel.WARNING
)
fun ZonedDateTime.truncatedToMinutes() = truncatedTo(MINUTES)

/**
 * Return a copy of this date-time, truncated to the `second` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(SECONDS)", "io.islandtime.measures.TimeUnit.SECONDS"),
    DeprecationLevel.WARNING
)
fun ZonedDateTime.truncatedToSeconds() = truncatedTo(SECONDS)

/**
 * Return a copy of this date-time with the `nanosecond` value truncated to milliseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MILLISECONDS)", "io.islandtime.measures.TimeUnit.MILLISECONDS"),
    DeprecationLevel.WARNING
)
fun ZonedDateTime.truncatedToMilliseconds() = truncatedTo(MILLISECONDS)

/**
 * Return a copy of this date-time with the `nanosecond` value truncated to microseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MICROSECONDS)", "io.islandtime.measures.TimeUnit.MICROSECONDS"),
    DeprecationLevel.WARNING
)
fun ZonedDateTime.truncatedToMicroseconds() = truncatedTo(MICROSECONDS)

/**
 * Return a copy of this time, truncated to the `hour` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(HOURS)", "io.islandtime.measures.TimeUnit.HOURS"),
    DeprecationLevel.WARNING
)
fun OffsetTime.truncatedToHours() = truncatedTo(HOURS)

/**
 * Return a copy of this time, truncated to the `minute` value. ll smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MINUTES)", "io.islandtime.measures.TimeUnit.MINUTES"),
    DeprecationLevel.WARNING
)
fun OffsetTime.truncatedToMinutes() = truncatedTo(MINUTES)

/**
 * Return a copy of this time, truncated to the `second` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(SECONDS)", "io.islandtime.measures.TimeUnit.SECONDS"),
    DeprecationLevel.WARNING
)
fun OffsetTime.truncatedToSeconds() = truncatedTo(SECONDS)

/**
 * Return a copy of this time with the `nanosecond` value truncated to milliseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MILLISECONDS)", "io.islandtime.measures.TimeUnit.MILLISECONDS"),
    DeprecationLevel.WARNING
)
fun OffsetTime.truncatedToMilliseconds() = truncatedTo(MILLISECONDS)

/**
 * Return a copy of this time with the `nanosecond` value truncated to microseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MICROSECONDS)", "io.islandtime.measures.TimeUnit.MICROSECONDS"),
    DeprecationLevel.WARNING
)
fun OffsetTime.truncatedToMicroseconds() = truncatedTo(MICROSECONDS)

/**
 * Return a copy of this date-time, truncated to the `hour` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(HOURS)", "io.islandtime.measures.TimeUnit.HOURS"),
    DeprecationLevel.WARNING
)
fun OffsetDateTime.truncatedToHours() = truncatedTo(HOURS)

/**
 * Return a copy of this date-time, truncated to the `minute` value. ll smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MINUTES)", "io.islandtime.measures.TimeUnit.MINUTES"),
    DeprecationLevel.WARNING
)
fun OffsetDateTime.truncatedToMinutes() = truncatedTo(MINUTES)

/**
 * Return a copy of this date-time, truncated to the `second` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(SECONDS)", "io.islandtime.measures.TimeUnit.SECONDS"),
    DeprecationLevel.WARNING
)
fun OffsetDateTime.truncatedToSeconds() = truncatedTo(SECONDS)

/**
 * Return a copy of this date-time with the `nanosecond` value truncated to milliseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MILLISECONDS)", "io.islandtime.measures.TimeUnit.MILLISECONDS"),
    DeprecationLevel.WARNING
)
fun OffsetDateTime.truncatedToMilliseconds() = truncatedTo(MILLISECONDS)

/**
 * Return a copy of this date-time with the `nanosecond` value truncated to microseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MICROSECONDS)", "io.islandtime.measures.TimeUnit.MICROSECONDS"),
    DeprecationLevel.WARNING
)
fun OffsetDateTime.truncatedToMicroseconds() = truncatedTo(MICROSECONDS)