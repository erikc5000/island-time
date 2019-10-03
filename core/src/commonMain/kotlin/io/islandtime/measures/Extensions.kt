package io.islandtime.measures

import io.islandtime.internal.*
import io.islandtime.internal.NANOSECONDS_PER_MICROSECOND
import io.islandtime.internal.NANOSECONDS_PER_MILLISECOND
import io.islandtime.internal.NANOSECONDS_PER_SECOND
import io.islandtime.internal.plusExact

internal infix fun IntNanoseconds.plusWithOverflow(nanoseconds: IntNanoseconds) =
    IntNanoseconds(value + nanoseconds.value)

internal infix fun IntNanoseconds.plusWithOverflow(microseconds: IntMicroseconds) =
    IntNanoseconds(value + microseconds.value * NANOSECONDS_PER_MICROSECOND)

internal infix fun IntNanoseconds.plusWithOverflow(milliseconds: IntMilliseconds) =
    IntNanoseconds(value + milliseconds.value * NANOSECONDS_PER_MILLISECOND)

internal infix fun IntNanoseconds.plusWithOverflow(seconds: IntSeconds) =
    IntNanoseconds(value + seconds.value * NANOSECONDS_PER_SECOND)

internal infix fun IntNanoseconds.minusWithOverflow(nanoseconds: IntNanoseconds) = plusWithOverflow(-nanoseconds)
internal infix fun IntNanoseconds.minusWithOverflow(microseconds: IntMicroseconds) = plusWithOverflow(-microseconds)
internal infix fun IntNanoseconds.minusWithOverflow(milliseconds: IntMilliseconds) = plusWithOverflow(-milliseconds)
internal infix fun IntNanoseconds.minusWithOverflow(seconds: IntSeconds) = plusWithOverflow(-seconds)

internal infix fun LongDays.plusExact(days: LongDays) = LongDays(value plusExact days.value)
internal infix fun LongDays.minusExact(days: LongDays) = LongDays(value minusExact days.value)

internal infix fun LongSeconds.plusExact(seconds: LongSeconds) = LongSeconds(value plusExact seconds.value)
internal infix fun LongSeconds.minusExact(seconds: LongSeconds) = LongSeconds(value minusExact seconds.value)

internal infix fun LongMilliseconds.plusExact(milliseconds: LongMilliseconds) =
    LongMilliseconds(value plusExact milliseconds.value)

internal infix fun LongMilliseconds.minusExact(milliseconds: LongMilliseconds) =
    LongMilliseconds(value minusExact milliseconds.value)

internal infix fun LongMicroseconds.plusExact(microseconds: LongMicroseconds) =
    LongMicroseconds(value plusExact microseconds.value)

internal infix fun LongMicroseconds.minusExact(microseconds: LongMicroseconds) =
    LongMicroseconds(value minusExact microseconds.value)

internal infix fun LongNanoseconds.plusExact(nanoseconds: LongNanoseconds) =
    LongNanoseconds(value plusExact nanoseconds.value)

internal infix fun LongNanoseconds.minusExact(nanoseconds: LongNanoseconds) =
    LongNanoseconds(value minusExact nanoseconds.value)