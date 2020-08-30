package io.islandtime.internal

internal fun dayOfWeekInMonth(dayOfMonth: Int): Int = ((dayOfMonth - 1) / 7) + 1
