package dev.erikchristensen.islandtime.interval

import dev.erikchristensen.islandtime.internal.NANOSECONDS_PER_MICROSECOND
import dev.erikchristensen.islandtime.internal.NANOSECONDS_PER_MILLISECOND
import dev.erikchristensen.islandtime.internal.NANOSECONDS_PER_SECOND

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