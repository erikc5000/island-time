@file:Suppress("unused")

package io.islandtime.operators

import io.islandtime.*
import io.islandtime.internal.deprecatedToError

/**
 * Return a copy of this time, truncated to the `hour` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(HOURS)", "io.islandtime.measures.TimeUnit.HOURS"),
    DeprecationLevel.ERROR
)
fun Time.truncatedToHours(): Time = deprecatedToError()

/**
 * Return a copy of this time, truncated to the `minute` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MINUTES)", "io.islandtime.measures.TimeUnit.MINUTES"),
    DeprecationLevel.ERROR
)
fun Time.truncatedToMinutes(): Time = deprecatedToError()

/**
 * Return a copy of this time, truncated to the `second` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(SECONDS)", "io.islandtime.measures.TimeUnit.SECONDS"),
    DeprecationLevel.ERROR
)
fun Time.truncatedToSeconds(): Time = deprecatedToError()

/**
 * Return a copy of this time with the `nanosecond` value truncated to milliseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MILLISECONDS)", "io.islandtime.measures.TimeUnit.MILLISECONDS"),
    DeprecationLevel.ERROR
)
fun Time.truncatedToMilliseconds(): Time = deprecatedToError()

/**
 * Return a copy of this time with the `nanosecond` value truncated to microseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MICROSECONDS)", "io.islandtime.measures.TimeUnit.MICROSECONDS"),
    DeprecationLevel.ERROR
)
fun Time.truncatedToMicroseconds(): Time = deprecatedToError()

/**
 * Return a copy of this date-time, truncated to the `hour` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(HOURS)", "io.islandtime.measures.TimeUnit.HOURS"),
    DeprecationLevel.ERROR
)
fun DateTime.truncatedToHours(): DateTime = deprecatedToError()

/**
 * Return a copy of this date-time, truncated to the `minute` value. ll smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(TimeUnit.MINUTES)", "io.islandtime.measures.TimeUnit"),
    DeprecationLevel.ERROR
)
fun DateTime.truncatedToMinutes(): DateTime = deprecatedToError()

/**
 * Return a copy of this date-time, truncated to the `second` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(SECONDS)", "io.islandtime.measures.TimeUnit.SECONDS"),
    DeprecationLevel.ERROR
)
fun DateTime.truncatedToSeconds(): DateTime = deprecatedToError()

/**
 * Return a copy of this date-time with the `nanosecond` value truncated to milliseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MILLISECONDS)", "io.islandtime.measures.TimeUnit.MILLISECONDS"),
    DeprecationLevel.ERROR
)
fun DateTime.truncatedToMilliseconds(): DateTime = deprecatedToError()

/**
 * Return a copy of this date-time with the `nanosecond` value truncated to microseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MICROSECONDS)", "io.islandtime.measures.TimeUnit.MICROSECONDS"),
    DeprecationLevel.ERROR
)
fun DateTime.truncatedToMicroseconds(): DateTime = deprecatedToError()

/**
 * Return a copy of this date-time, truncated to the `hour` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(HOURS)", "io.islandtime.measures.TimeUnit.HOURS"),
    DeprecationLevel.ERROR
)
fun ZonedDateTime.truncatedToHours(): ZonedDateTime = deprecatedToError()

/**
 * Return a copy of this date-time, truncated to the `minute` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MINUTES)", "io.islandtime.measures.TimeUnit.MINUTES"),
    DeprecationLevel.ERROR
)
fun ZonedDateTime.truncatedToMinutes(): ZonedDateTime = deprecatedToError()

/**
 * Return a copy of this date-time, truncated to the `second` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(SECONDS)", "io.islandtime.measures.TimeUnit.SECONDS"),
    DeprecationLevel.ERROR
)
fun ZonedDateTime.truncatedToSeconds(): ZonedDateTime = deprecatedToError()

/**
 * Return a copy of this date-time with the `nanosecond` value truncated to milliseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MILLISECONDS)", "io.islandtime.measures.TimeUnit.MILLISECONDS"),
    DeprecationLevel.ERROR
)
fun ZonedDateTime.truncatedToMilliseconds(): ZonedDateTime = deprecatedToError()

/**
 * Return a copy of this date-time with the `nanosecond` value truncated to microseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MICROSECONDS)", "io.islandtime.measures.TimeUnit.MICROSECONDS"),
    DeprecationLevel.ERROR
)
fun ZonedDateTime.truncatedToMicroseconds(): ZonedDateTime = deprecatedToError()

/**
 * Return a copy of this time, truncated to the `hour` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(HOURS)", "io.islandtime.measures.TimeUnit.HOURS"),
    DeprecationLevel.ERROR
)
fun OffsetTime.truncatedToHours(): OffsetTime = deprecatedToError()

/**
 * Return a copy of this time, truncated to the `minute` value. ll smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MINUTES)", "io.islandtime.measures.TimeUnit.MINUTES"),
    DeprecationLevel.ERROR
)
fun OffsetTime.truncatedToMinutes(): OffsetTime = deprecatedToError()

/**
 * Return a copy of this time, truncated to the `second` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(SECONDS)", "io.islandtime.measures.TimeUnit.SECONDS"),
    DeprecationLevel.ERROR
)
fun OffsetTime.truncatedToSeconds(): OffsetTime = deprecatedToError()

/**
 * Return a copy of this time with the `nanosecond` value truncated to milliseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MILLISECONDS)", "io.islandtime.measures.TimeUnit.MILLISECONDS"),
    DeprecationLevel.ERROR
)
fun OffsetTime.truncatedToMilliseconds(): OffsetTime = deprecatedToError()

/**
 * Return a copy of this time with the `nanosecond` value truncated to microseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MICROSECONDS)", "io.islandtime.measures.TimeUnit.MICROSECONDS"),
    DeprecationLevel.ERROR
)
fun OffsetTime.truncatedToMicroseconds(): OffsetTime = deprecatedToError()

/**
 * Return a copy of this date-time, truncated to the `hour` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(HOURS)", "io.islandtime.measures.TimeUnit.HOURS"),
    DeprecationLevel.ERROR
)
fun OffsetDateTime.truncatedToHours(): OffsetDateTime = deprecatedToError()

/**
 * Return a copy of this date-time, truncated to the `minute` value. ll smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MINUTES)", "io.islandtime.measures.TimeUnit.MINUTES"),
    DeprecationLevel.ERROR
)
fun OffsetDateTime.truncatedToMinutes(): OffsetDateTime = deprecatedToError()

/**
 * Return a copy of this date-time, truncated to the `second` value. All smaller components will be replaced with zero.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(SECONDS)", "io.islandtime.measures.TimeUnit.SECONDS"),
    DeprecationLevel.ERROR
)
fun OffsetDateTime.truncatedToSeconds(): OffsetDateTime = deprecatedToError()

/**
 * Return a copy of this date-time with the `nanosecond` value truncated to milliseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MILLISECONDS)", "io.islandtime.measures.TimeUnit.MILLISECONDS"),
    DeprecationLevel.ERROR
)
fun OffsetDateTime.truncatedToMilliseconds(): OffsetDateTime = deprecatedToError()

/**
 * Return a copy of this date-time with the `nanosecond` value truncated to microseconds.
 */
@Deprecated(
    "Use truncatedTo() instead.",
    ReplaceWith("truncatedTo(MICROSECONDS)", "io.islandtime.measures.TimeUnit.MICROSECONDS"),
    DeprecationLevel.ERROR
)
fun OffsetDateTime.truncatedToMicroseconds(): OffsetDateTime = deprecatedToError()
