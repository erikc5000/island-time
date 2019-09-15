@file:Suppress("NewApi")

package dev.erikchristensen.islandtime.internal

internal actual infix fun Long.floorRem(other: Long): Long = Math.floorMod(this, other)
internal actual infix fun Int.floorRem(other: Int): Int = Math.floorMod(this, other)
internal actual infix fun Long.floorDiv(other: Long): Long = Math.floorDiv(this, other)
internal actual infix fun Int.floorDiv(other: Int): Int = Math.floorDiv(this, other)
internal actual infix fun Long.plusExact(other: Long): Long = Math.addExact(this, other)
internal actual infix fun Int.plusExact(other: Int): Int = Math.addExact(this, other)
internal actual infix fun Long.minusExact(other: Long): Long = Math.subtractExact(this, other)
internal actual infix fun Int.minusExact(other: Int): Int = Math.subtractExact(this, other)
internal actual infix fun Long.timesExact(other: Long): Long = Math.multiplyExact(this, other)
internal actual infix fun Int.timesExact(other: Int): Int = Math.multiplyExact(this, other)
internal actual fun Long.toIntExact(): Int = Math.toIntExact(this)