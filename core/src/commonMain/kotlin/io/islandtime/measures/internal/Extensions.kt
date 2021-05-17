package io.islandtime.measures.internal

import io.islandtime.internal.NANOSECONDS_PER_SECOND
import io.islandtime.measures.Nanoseconds
import io.islandtime.measures.Seconds

internal infix fun Nanoseconds.plusUnchecked(seconds: Seconds): Nanoseconds =
    Nanoseconds(value + seconds.value * NANOSECONDS_PER_SECOND)
