package io.islandtime.clock

import io.islandtime.*
import io.islandtime.internal.*
import io.islandtime.internal.MILLISECONDS_PER_DAY
import io.islandtime.internal.MILLISECONDS_PER_SECOND
import io.islandtime.internal.SECONDS_PER_DAY
import io.islandtime.internal.floorDiv
import io.islandtime.interval.nanoseconds

fun Instant.Companion.now() = now(SystemClock.UTC)
fun Instant.Companion.now(clock: Clock) = clock.instant()

fun Year.Companion.now() = now(SystemClock())
fun Year.Companion.now(clock: Clock) = Year(Date.now(clock).year)

fun YearMonth.Companion.now() = now(SystemClock())
fun YearMonth.Companion.now(clock: Clock) = Date.now(clock).yearMonth

fun Date.Companion.now() = now(SystemClock())

fun Date.Companion.now(clock: Clock): Date {
    val milliseconds = clock.read()
    val offset = clock.zone.rules.offsetAt(milliseconds)
    val unixEpochSecond = (milliseconds.value floorDiv MILLISECONDS_PER_SECOND) + offset.totalSeconds.value
    val unixEpochDay = unixEpochSecond floorDiv SECONDS_PER_DAY
    return fromUnixEpochDay(unixEpochDay)
}

fun DateTime.Companion.now() = now(SystemClock())

fun DateTime.Companion.now(clock: Clock): DateTime {
    val milliseconds = clock.read()
    val offset = clock.zone.rules.offsetAt(milliseconds)
    return fromMillisecondsSinceUnixEpoch(milliseconds, offset)
}

fun OffsetDateTime.Companion.now() = now(SystemClock())

fun OffsetDateTime.Companion.now(clock: Clock): OffsetDateTime {
    val milliseconds = clock.read()
    val offset = clock.zone.rules.offsetAt(milliseconds)
    return DateTime.fromMillisecondsSinceUnixEpoch(milliseconds, offset) at offset
}

fun OffsetTime.Companion.now() = now(SystemClock())

fun OffsetTime.Companion.now(clock: Clock): OffsetTime {
    val milliseconds = clock.read()
    val offset = clock.zone.rules.offsetAt(milliseconds)

    val nanosecondsSinceStartOfDay = ((milliseconds % MILLISECONDS_PER_DAY).inNanoseconds +
        offset.totalSeconds + NANOSECONDS_PER_DAY.nanoseconds) % NANOSECONDS_PER_DAY

    return Time.fromNanosecondOfDay(nanosecondsSinceStartOfDay.value) at offset
}

fun Time.Companion.now() = now(SystemClock())

fun Time.Companion.now(clock: Clock): Time {
    val milliseconds = clock.read()
    val offset = clock.zone.rules.offsetAt(milliseconds)

    val nanosecondsSinceStartOfDay = ((milliseconds % MILLISECONDS_PER_DAY).inNanoseconds +
        offset.totalSeconds + NANOSECONDS_PER_DAY.nanoseconds) % NANOSECONDS_PER_DAY

    return fromNanosecondOfDay(nanosecondsSinceStartOfDay.value)
}

fun ZonedDateTime.Companion.now() = now(SystemClock())

fun ZonedDateTime.Companion.now(clock: Clock): ZonedDateTime {
    return fromMillisecondsSinceUnixEpoch(clock.read(), clock.zone)
}