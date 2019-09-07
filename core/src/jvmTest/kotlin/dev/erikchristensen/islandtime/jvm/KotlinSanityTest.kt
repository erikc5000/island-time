package dev.erikchristensen.islandtime.jvm

import com.google.common.truth.Truth.assertThat
import dev.erikchristensen.islandtime.*
import dev.erikchristensen.islandtime.date.Date
import java.time.LocalDate
import kotlin.test.Test

class KotlinSanityTest {

    @Test
    fun `converts island Date to java LocalDate`() {
        val islandDate = Date(2019, Month.MAY, 3)
        val javaDate = islandDate.toJavaLocalDate()

        assertThat(islandDate.year).isEqualTo(javaDate.year)
        assertThat(islandDate.month.number).isEqualTo(javaDate.monthValue)
        assertThat(islandDate.dayOfMonth).isEqualTo(javaDate.dayOfMonth)
    }

    @Test
    fun `converts java LocalDate to island Date`() {
        val javaDate = LocalDate.of(2019, java.time.Month.MAY, 3)
        val islandDate = javaDate.toIslandDate()

        assertThat(javaDate.year).isEqualTo(islandDate.year)
        assertThat(javaDate.monthValue).isEqualTo(islandDate.month.number)
        assertThat(javaDate.dayOfMonth).isEqualTo(islandDate.dayOfMonth)
    }
}