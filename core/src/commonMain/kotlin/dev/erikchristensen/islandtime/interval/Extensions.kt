package dev.erikchristensen.islandtime.interval

import dev.erikchristensen.islandtime.internal.NANOSECONDS_PER_SECOND

internal infix fun IntNanoseconds.unsafeAdd(nanoseconds: IntNanoseconds) =
    IntNanoseconds(value + nanoseconds.value)

internal infix fun IntNanoseconds.unsafeAdd(seconds: IntSeconds) =
    IntNanoseconds(value + seconds.value * NANOSECONDS_PER_SECOND.toInt())

internal infix fun IntNanoseconds.unsafeSubtract(nanoseconds: IntNanoseconds) = unsafeAdd(-nanoseconds)
internal infix fun IntNanoseconds.unsafeSubtract(seconds: IntSeconds) = unsafeAdd(-seconds)