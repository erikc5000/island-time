package dev.erikchristensen.islandtime.extensions.threetenbp

import com.google.common.truth.Truth.assertThat
import dev.erikchristensen.islandtime.Month
import dev.erikchristensen.islandtime.date.Date
import org.threeten.bp.LocalDate
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
        val javaDate = LocalDate.of(2019, org.threeten.bp.Month.MAY, 3)
        val islandDate = javaDate.toIslandDate()

        assertThat(javaDate.year).isEqualTo(islandDate.year)
        assertThat(javaDate.monthValue).isEqualTo(islandDate.month.number)
        assertThat(javaDate.dayOfMonth).isEqualTo(islandDate.dayOfMonth)
    }
}