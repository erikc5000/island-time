package dev.erikchristensen.islandtime

import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class JvmClockTest {
    @Test
    fun `instant()`() {
        assertThat(systemClock().instant()).isGreaterThan(Instant.UNIX_EPOCH)
    }

    @Test
    fun `timeZone()`() {
        assertThat(systemClock().timeZone).isNotNull()
    }
}