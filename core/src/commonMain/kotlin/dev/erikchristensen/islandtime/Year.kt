@file:JvmName("Years")
package dev.erikchristensen.islandtime

import kotlin.jvm.JvmName

inline class Year(private val value: Int) : Comparable<Year> {

    val isLeap: Boolean get() = isLeapYear(value)

    override fun compareTo(other: Year) = value.compareTo(other.value)

    companion object {
        const val MIN_VALUE = 1
        const val MAX_VALUE = 9999
    }
}

fun isLeapYear(year: Int): Boolean {
    return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
}