//
// This file is auto-generated by 'tools:code-generator'
//
@file:JvmMultifileClass
@file:JvmName("RangesKt")

package io.islandtime.ranges

import io.islandtime.Date
import io.islandtime.DateTime
import io.islandtime.Instant
import io.islandtime.OffsetDateTime
import io.islandtime.ZonedDateTime
import io.islandtime.ranges.`internal`.randomImpl
import io.islandtime.ranges.`internal`.randomOrNullImpl
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.random.Random

/**
 * Returns a random date within this range using the default random number generator.
 *
 * @throws NoSuchElementException if the range is empty
 * @throws UnsupportedOperationException if the range is unbounded
 * @see DateRange.randomOrNull
 */
public fun DateRange.random(): Date = randomImpl(Random)

/**
 * Returns a random date within this range using the supplied random number generator. 
 *
 * @throws NoSuchElementException if the range is empty
 * @throws UnsupportedOperationException if the range is unbounded
 * @see DateRange.randomOrNull
 */
public fun DateRange.random(random: Random): Date = randomImpl(random)

/**
 * Returns a random date within this range using the default random number generator or `null` if
 * the interval is empty or unbounded. 
 *
 * @see DateRange.random
 */
public fun DateRange.randomOrNull(): Date? = randomOrNullImpl(Random)

/**
 * Returns a random date within this range using the supplied random number generator or `null` if
 * the interval is empty or unbounded. 
 *
 * @see DateRange.random
 */
public fun DateRange.randomOrNull(random: Random): Date? = randomOrNullImpl(random)

/**
 * Returns a random date within this interval using the default random number generator.
 *
 * @throws NoSuchElementException if the interval is empty
 * @throws UnsupportedOperationException if the interval is unbounded
 * @see DateTimeInterval.randomOrNull
 */
public fun DateTimeInterval.random(): DateTime = randomImpl(Random)

/**
 * Returns a random date within this interval using the supplied random number generator. 
 *
 * @throws NoSuchElementException if the interval is empty
 * @throws UnsupportedOperationException if the interval is unbounded
 * @see DateTimeInterval.randomOrNull
 */
public fun DateTimeInterval.random(random: Random): DateTime = randomImpl(random)

/**
 * Returns a random date within this interval using the default random number generator or `null` if
 * the interval is empty or unbounded. 
 *
 * @see DateTimeInterval.random
 */
public fun DateTimeInterval.randomOrNull(): DateTime? = randomOrNullImpl(Random)

/**
 * Returns a random date within this interval using the supplied random number generator or `null`
 * if the interval is empty or unbounded. 
 *
 * @see DateTimeInterval.random
 */
public fun DateTimeInterval.randomOrNull(random: Random): DateTime? = randomOrNullImpl(random)

/**
 * Returns a random date within this interval using the default random number generator. The offset
 * of the start date-time will be used.
 *
 * @throws NoSuchElementException if the interval is empty
 * @throws UnsupportedOperationException if the interval is unbounded
 * @see OffsetDateTimeInterval.randomOrNull
 */
public fun OffsetDateTimeInterval.random(): OffsetDateTime = randomImpl(Random)

/**
 * Returns a random date within this interval using the supplied random number generator. The offset
 * of the start date-time will be used.
 *
 * @throws NoSuchElementException if the interval is empty
 * @throws UnsupportedOperationException if the interval is unbounded
 * @see OffsetDateTimeInterval.randomOrNull
 */
public fun OffsetDateTimeInterval.random(random: Random): OffsetDateTime = randomImpl(random)

/**
 * Returns a random date within this interval using the default random number generator or `null` if
 * the interval is empty or unbounded. The offset of the start date-time will be used.
 *
 * @see OffsetDateTimeInterval.random
 */
public fun OffsetDateTimeInterval.randomOrNull(): OffsetDateTime? = randomOrNullImpl(Random)

/**
 * Returns a random date within this interval using the supplied random number generator or `null`
 * if the interval is empty or unbounded. The offset of the start date-time will be used.
 *
 * @see OffsetDateTimeInterval.random
 */
public fun OffsetDateTimeInterval.randomOrNull(random: Random): OffsetDateTime? =
    randomOrNullImpl(random)

/**
 * Returns a random date within this interval using the default random number generator. The zone of
 * the start date-time will be used.
 *
 * @throws NoSuchElementException if the interval is empty
 * @throws UnsupportedOperationException if the interval is unbounded
 * @see ZonedDateTimeInterval.randomOrNull
 */
public fun ZonedDateTimeInterval.random(): ZonedDateTime = randomImpl(Random)

/**
 * Returns a random date within this interval using the supplied random number generator. The zone
 * of the start date-time will be used.
 *
 * @throws NoSuchElementException if the interval is empty
 * @throws UnsupportedOperationException if the interval is unbounded
 * @see ZonedDateTimeInterval.randomOrNull
 */
public fun ZonedDateTimeInterval.random(random: Random): ZonedDateTime = randomImpl(random)

/**
 * Returns a random date within this interval using the default random number generator or `null` if
 * the interval is empty or unbounded. The zone of the start date-time will be used.
 *
 * @see ZonedDateTimeInterval.random
 */
public fun ZonedDateTimeInterval.randomOrNull(): ZonedDateTime? = randomOrNullImpl(Random)

/**
 * Returns a random date within this interval using the supplied random number generator or `null`
 * if the interval is empty or unbounded. The zone of the start date-time will be used.
 *
 * @see ZonedDateTimeInterval.random
 */
public fun ZonedDateTimeInterval.randomOrNull(random: Random): ZonedDateTime? =
    randomOrNullImpl(random)

/**
 * Returns a random date within this interval using the default random number generator.
 *
 * @throws NoSuchElementException if the interval is empty
 * @throws UnsupportedOperationException if the interval is unbounded
 * @see InstantInterval.randomOrNull
 */
public fun InstantInterval.random(): Instant = randomImpl(Random)

/**
 * Returns a random date within this interval using the supplied random number generator. 
 *
 * @throws NoSuchElementException if the interval is empty
 * @throws UnsupportedOperationException if the interval is unbounded
 * @see InstantInterval.randomOrNull
 */
public fun InstantInterval.random(random: Random): Instant = randomImpl(random)

/**
 * Returns a random date within this interval using the default random number generator or `null` if
 * the interval is empty or unbounded. 
 *
 * @see InstantInterval.random
 */
public fun InstantInterval.randomOrNull(): Instant? = randomOrNullImpl(Random)

/**
 * Returns a random date within this interval using the supplied random number generator or `null`
 * if the interval is empty or unbounded. 
 *
 * @see InstantInterval.random
 */
public fun InstantInterval.randomOrNull(random: Random): Instant? = randomOrNullImpl(random)
