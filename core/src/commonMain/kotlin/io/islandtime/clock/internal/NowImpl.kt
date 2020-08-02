package io.islandtime.clock.internal

import io.islandtime.*
import io.islandtime.clock.Clock

expect fun Date.Companion.nowImpl(clock: Clock): Date
expect fun DateTime.Companion.nowImpl(clock: Clock): DateTime
expect fun OffsetDateTime.Companion.nowImpl(clock: Clock): OffsetDateTime
expect fun ZonedDateTime.Companion.nowImpl(clock: Clock): ZonedDateTime
expect fun Time.Companion.nowImpl(clock: Clock): Time
expect fun OffsetTime.Companion.nowImpl(clock: Clock): OffsetTime