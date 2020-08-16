package io.islandtime.clock.internal

import io.islandtime.*
import io.islandtime.clock.Clock

internal expect fun Date.Companion.nowImpl(clock: Clock): Date
internal expect fun DateTime.Companion.nowImpl(clock: Clock): DateTime
internal expect fun OffsetDateTime.Companion.nowImpl(clock: Clock): OffsetDateTime
internal expect fun ZonedDateTime.Companion.nowImpl(clock: Clock): ZonedDateTime
internal expect fun Time.Companion.nowImpl(clock: Clock): Time
internal expect fun OffsetTime.Companion.nowImpl(clock: Clock): OffsetTime
