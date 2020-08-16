package io.islandtime.jvm

import com.google.common.truth.Truth.assertThat
import io.islandtime.DateTime
import io.islandtime.UtcOffset
import io.islandtime.internal.NANOSECONDS_PER_SECOND
import io.islandtime.measures.milliseconds
import io.islandtime.measures.nanoseconds
import io.islandtime.measures.seconds
import org.junit.Test
import java.time.*

class DateTimeComparisonTest {

    private fun compare(javaDateTime: LocalDateTime, islandDateTime: DateTime) {
        assertThat(javaDateTime.year).isEqualTo(islandDateTime.year)
        assertThat(javaDateTime.monthValue).isEqualTo(islandDateTime.monthNumber)
        assertThat(javaDateTime.dayOfMonth).isEqualTo(islandDateTime.dayOfMonth)
        assertThat(javaDateTime.hour).isEqualTo(islandDateTime.hour)
        assertThat(javaDateTime.minute).isEqualTo(islandDateTime.minute)
        assertThat(javaDateTime.second).isEqualTo(islandDateTime.second)
        assertThat(javaDateTime.nano).isEqualTo(islandDateTime.nanosecond)
        assertThat(javaDateTime.toEpochSecond(ZoneOffset.UTC))
            .isEqualTo(islandDateTime.secondsSinceUnixEpochAt(UtcOffset.ZERO).value)
    }

    @Test
    fun `dateTime from milliseconds since unix epoch`() {
        val epochMilliRange = -10_000_000L..10_000_000L

        for (i in epochMilliRange step 100_373) {
            val javaDateTime = Instant.ofEpochMilli(i).atOffset(ZoneOffset.UTC).toLocalDateTime()
            val islandDateTime = DateTime.fromMillisecondsSinceUnixEpoch(i.milliseconds, UtcOffset.ZERO)
            compare(javaDateTime, islandDateTime)
        }
    }

    @Test
    fun `dateTime from seconds since unix epoch`() {
        val epochSecondRange = -2L..2L
        val nanoRange = 0 until NANOSECONDS_PER_SECOND

        for (second in epochSecondRange) {
            for (nano in nanoRange step 100_373) {
                val javaDateTime = LocalDateTime.ofEpochSecond(second, nano, ZoneOffset.UTC)
                val islandDateTime =
                    DateTime.fromSecondsSinceUnixEpoch(second.seconds, nano.nanoseconds, UtcOffset.ZERO)
                compare(javaDateTime, islandDateTime)
            }
        }
    }
}
