@file:Suppress("NewApi")

package io.islandtime.internal

//TODO replace with Math functions
internal actual infix fun Long.floorMod(other: Long): Long = this.rem(other)
internal actual infix fun Int.floorMod(other: Int): Int = this.rem(other)
internal actual infix fun Long.floorMod(other: Int): Long = this floorMod other.toLong()

internal actual infix fun Long.floorDiv(other: Long): Long = this / other
internal actual infix fun Int.floorDiv(other: Int): Int = this / other
internal actual infix fun Long.floorDiv(other: Int): Long = this floorDiv other.toLong()

internal actual infix fun Long.plusExact(other: Long): Long = this + other
internal actual infix fun Int.plusExact(other: Int): Int = this + other

internal actual infix fun Long.minusExact(other: Long): Long = this - other
internal actual infix fun Int.minusExact(other: Int): Int = this - other

internal actual infix fun Long.timesExact(other: Long): Long = this * other
internal actual infix fun Int.timesExact(other: Int): Int = this * other
internal actual infix fun Long.timesExact(other: Int): Long = this timesExact other.toLong()

internal actual fun Int.negateExact(): Int =
    -(this)

internal actual fun Long.negateExact(): Long =
    -(this)

internal actual fun Long.toIntExact(): Int =
    if (this <= Int.MAX_VALUE || this >= Int.MIN_VALUE) {
        this.toInt()
    } else {
        throw NumberFormatException()
    }