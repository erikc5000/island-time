package dev.erikchristensen.islandtime

import dev.erikchristensen.islandtime.interval.milliseconds
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InstantTest {
    @Test
    fun `unixEpochMilliseconds property returns the milliseconds since the Unix epoch`() {
        assertEquals(
            0L.milliseconds,
            Instant.UNIX_EPOCH.unixEpochMilliseconds
        )

        assertEquals(
            1566256047821L.milliseconds,
            Instant(1566256047821L.milliseconds).unixEpochMilliseconds
        )
    }

    @Test
    fun `instants can be compared to each other`() {
        assertTrue { Instant.UNIX_EPOCH < Instant(1566256047821L.milliseconds) }
    }

    @Test
    fun `toString() returns an ISO-8601 extended date-time with UTC offset`() {
        assertEquals(
            "1970-01-01T00:00Z",
            Instant.UNIX_EPOCH.toString()
        )

        assertEquals(
            "2019-08-19T23:07:27.821Z",
            Instant(1566256047821L.milliseconds).toString()
        )
    }
}