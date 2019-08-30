package dev.erikchristensen.islandtime.extensions.javatime

import com.google.common.truth.Truth.assertThat
import dev.erikchristensen.islandtime.date.*
import dev.erikchristensen.islandtime.interval.isNegative
import dev.erikchristensen.islandtime.interval.normalized
import dev.erikchristensen.islandtime.interval.unaryMinus
import org.junit.Test
import java.time.LocalDate
import java.time.Period

class DateComparisonTest {
    private val javaDates = listOf(
        LocalDate.of(2019, java.time.Month.MAY, 3),
        LocalDate.of(1970, java.time.Month.JANUARY, 1),
        LocalDate.of(1952, java.time.Month.FEBRUARY, 29)
    )

    private inline fun List<LocalDate>.compareWithIsland(action: (javaDate: LocalDate, islandDate: Date) -> Unit) {
        forEach { action(it, it.toIslandDate()) }
    }

    @Test
    fun `property equivalence`() {
        javaDates.compareWithIsland { javaDate, islandDate ->
            assertThat(javaDate.dayOfWeek.value).isEqualTo(islandDate.dayOfWeek.number)
            assertThat(javaDate.dayOfYear).isEqualTo(islandDate.dayOfYear)
            assertThat(javaDate.isLeapYear).isEqualTo(islandDate.isInLeapYear)
            assertThat(javaDate.lengthOfMonth()).isEqualTo(islandDate.lengthOfMonth.value)
            assertThat(javaDate.lengthOfYear()).isEqualTo(islandDate.lengthOfYear.value)
        }
    }

    @Test
    fun `epoch days`() {
        javaDates.compareWithIsland { javaDate, islandDate ->
            assertThat(javaDate.toEpochDay()).isEqualTo(islandDate.unixEpochDays.value)
        }
    }

    @Test
    fun `period between`() {
        val javaEndDate = LocalDate.of(2018, java.time.Month.APRIL, 4)
        val islandEndDate = javaEndDate.toIslandDate()

        javaDates.compareWithIsland { javaDate, islandDate ->
            val javaPeriod = Period.between(javaDate, javaEndDate)
            val islandPeriod = periodBetween(islandDate, islandEndDate)

            assertThat(javaPeriod.days).isEqualTo(islandPeriod.days.value)
            assertThat(javaPeriod.months).isEqualTo(islandPeriod.months.value)
            assertThat(javaPeriod.years).isEqualTo(islandPeriod.years.value)
            assertThat(javaPeriod.negated().days).isEqualTo((-islandPeriod).days.value)
            assertThat(javaPeriod.isNegative).isEqualTo(islandPeriod.isNegative)
            assertThat(javaPeriod.normalized().days).isEqualTo(islandPeriod.normalized().days.value)
        }
    }
}