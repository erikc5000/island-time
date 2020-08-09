package io.islandtime.calendar

import io.islandtime.DayOfWeek
import kotlin.test.Test
import kotlin.test.assertEquals

class JvmWeekSettingsTest {

    @Test
    fun weekSettings_systemDefault_in_US() {
        //TODO common test names have to be refactored to be run on Js env
        // other wise compiler will complain
        val settings = WeekSettings.systemDefault()

        assertEquals(DayOfWeek.SUNDAY, settings.firstDayOfWeek)
        assertEquals(1, settings.minimumDaysInFirstWeek)
    }

//    @Test
//    fun `WeekSettings_systemDefault() in Germany`() {
//        Locale.setDefault(Locale.GERMANY)
//        val settings = WeekSettings.systemDefault()
//
//        assertEquals(DayOfWeek.MONDAY, settings.firstDayOfWeek)
//        assertEquals(4, settings.minimumDaysInFirstWeek)
//    }
}