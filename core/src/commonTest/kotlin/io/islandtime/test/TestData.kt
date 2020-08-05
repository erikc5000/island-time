package io.islandtime.test

import io.islandtime.Date
import io.islandtime.Year

object TestData {
    val isoWeekDates
        get() = listOf(
            Date(2005, 1, 1) to Triple(2004, 53, 6),
            Date(2005, 1, 2) to Triple(2004, 53, 7),
            Date(2005, 12, 31) to Triple(2005, 52, 6),
            Date(2006, 1, 1) to Triple(2005, 52, 7),
            Date(2006, 1, 2) to Triple(2006, 1, 1),
            Date(2006, 12, 31) to Triple(2006, 52, 7),
            Date(2007, 1, 1) to Triple(2007, 1, 1),
            Date(2007, 12, 30) to Triple(2007, 52, 7),
            Date(2007, 12, 31) to Triple(2008, 1, 1),
            Date(2008, 1, 1) to Triple(2008, 1, 2),
            Date(2008, 12, 28) to Triple(2008, 52, 7),
            Date(2008, 12, 29) to Triple(2009, 1, 1),
            Date(2008, 12, 30) to Triple(2009, 1, 2),
            Date(2008, 12, 31) to Triple(2009, 1, 3),
            Date(2009, 1, 1) to Triple(2009, 1, 4),
            Date(2009, 12, 31) to Triple(2009, 53, 4),
            Date(2010, 1, 1) to Triple(2009, 53, 5),
            Date(2010, 1, 2) to Triple(2009, 53, 6),
            Date(2010, 1, 3) to Triple(2009, 53, 7),
            Date(2010, 1, 4) to Triple(2010, 1, 1),
            Date.MIN to Triple(Year.MIN_VALUE, 1, 1),
            Date.MAX to Triple(Year.MAX_VALUE, 52, 5)
        )

    val sundayStartWeekDates
        get() = listOf(
            Date(2016, 12, 30) to Triple(2016, 53, 6),
            Date(2016, 12, 31) to Triple(2016, 53, 7),
            Date(2017, 1, 1) to Triple(2017, 1, 1),
            Date(2017, 1, 2) to Triple(2017, 1, 2),
            Date(2017, 1, 7) to Triple(2017, 1, 7),
            Date(2017, 1, 8) to Triple(2017, 2, 1),
            Date(2017, 12, 30) to Triple(2017, 52, 7),
            Date(2017, 12, 31) to Triple(2018, 1, 1),
            Date(2018, 1, 6) to Triple(2018, 1, 7),
            Date(2018, 12, 29) to Triple(2018, 52, 7),
            Date(2018, 12, 30) to Triple(2019, 1, 1),
            Date(2019, 1, 5) to Triple(2019, 1, 7),
            Date(2019, 1, 6) to Triple(2019, 2, 1),
            Date(2019, 12, 28) to Triple(2019, 52, 7),
            Date(2019, 12, 29) to Triple(2020, 1, 1),
            Date(2020, 1, 5) to Triple(2020, 2, 1),
            Date.MIN to Triple(Year.MIN_VALUE, 1, 2),
            Date.MAX to Triple(Year.MAX_VALUE + 1, 1, 6)
        )
}
