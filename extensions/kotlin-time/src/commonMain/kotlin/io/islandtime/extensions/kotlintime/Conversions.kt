package io.islandtime.extensions.kotlintime

import io.islandtime.measures.Duration
import io.islandtime.measures.durationOf
import io.islandtime.measures.nanoseconds
import io.islandtime.measures.seconds
import kotlin.time.seconds as kotlinSeconds

@UseExperimental(kotlin.time.ExperimentalTime::class)
fun kotlin.time.Duration.toIslandDuration(): Duration {
    return toComponents { seconds, nanoseconds ->
        durationOf(seconds.seconds, nanoseconds.nanoseconds)
    }
}

@UseExperimental(kotlin.time.ExperimentalTime::class)
fun Duration.toKotlinDuration(): kotlin.time.Duration {
    return (seconds.value + nanosecondAdjustment.value.toDouble() / 1_000_000_000).kotlinSeconds
}