package io.islandtime.calendar

import io.islandtime.DayOfWeek
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class JvmWeekSettingsTest {
    private var previousLocale: Locale? = null

    @Before
    fun setUp() {
        previousLocale = Locale.getDefault()
    }

    @After
    fun tearDown() {
        Locale.setDefault(previousLocale)
    }

    @Test
    fun `WeekSettings_systemDefault() in US`() {
        Locale.setDefault(Locale.US)
        val settings = WeekSettings.systemDefault()

        assertEquals(DayOfWeek.SUNDAY, settings.firstDayOfWeek)
        assertEquals(1, settings.minimumDaysInFirstWeek)
    }

    @Test
    fun `WeekSettings_systemDefault() in Germany`() {
        Locale.setDefault(Locale.GERMANY)
        val settings = WeekSettings.systemDefault()

        assertEquals(DayOfWeek.MONDAY, settings.firstDayOfWeek)
        assertEquals(4, settings.minimumDaysInFirstWeek)
    }
}