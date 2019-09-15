package dev.erikchristensen.islandtime.interval

import dev.erikchristensen.islandtime.internal.NANOSECONDS_PER_SECOND

internal infix fun IntNanoseconds.plusWithOverflow(nanoseconds: IntNanoseconds) =
    IntNanoseconds(value + nanoseconds.value)

internal infix fun IntNanoseconds.plusWithOverflow(seconds: IntSeconds) =
    IntNanoseconds(value + seconds.value * NANOSECONDS_PER_SECOND)

internal infix fun IntNanoseconds.minusWithOverflow(nanoseconds: IntNanoseconds) = plusWithOverflow(-nanoseconds)
internal infix fun IntNanoseconds.minusWithOverflow(seconds: IntSeconds) = plusWithOverflow(-seconds)