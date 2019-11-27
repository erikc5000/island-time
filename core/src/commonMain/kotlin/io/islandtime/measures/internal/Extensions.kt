package io.islandtime.measures.internal

import io.islandtime.internal.NANOSECONDS_PER_MICROSECOND
import io.islandtime.internal.NANOSECONDS_PER_MILLISECOND
import io.islandtime.internal.NANOSECONDS_PER_SECOND
import io.islandtime.measures.IntMicroseconds
import io.islandtime.measures.IntMilliseconds
import io.islandtime.measures.IntNanoseconds
import io.islandtime.measures.IntSeconds

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