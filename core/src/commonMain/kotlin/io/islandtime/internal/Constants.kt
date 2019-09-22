@file:JvmMultifileClass
@file:JvmName("ConstantsKt")

package io.islandtime.internal

import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

internal const val DAYS_IN_COMMON_YEAR = 365L
internal const val MONTHS_IN_YEAR = 12
internal const val DAYS_IN_WEEK = 7

internal const val DAYS_PER_400_YEAR_CYCLE = 146_097
internal const val NUMBER_OF_400_YEAR_CYCLES_FROM_0000_TO_1970 = 5L
internal const val LEAP_YEARS_FROM_1970_TO_2000 = 7L
internal const val YEARS_FROM__1970_TO_2000 = 30L

internal const val DAYS_FROM_0000_TO_1970 =
    (DAYS_PER_400_YEAR_CYCLE * NUMBER_OF_400_YEAR_CYCLES_FROM_0000_TO_1970) -
        (YEARS_FROM__1970_TO_2000 * DAYS_IN_COMMON_YEAR + LEAP_YEARS_FROM_1970_TO_2000)