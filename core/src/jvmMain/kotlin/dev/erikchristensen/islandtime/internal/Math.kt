@file:Suppress("NewApi")

package dev.erikchristensen.islandtime.internal

internal actual infix fun Long.floorMod(other: Long): Long = Math.floorMod(this, other)
internal actual infix fun Int.floorMod(other: Int): Int = Math.floorMod(this, other)
internal actual infix fun Long.floorMod(other: Int): Long = this floorMod other.toLong()

internal actual infix fun Long.floorDiv(other: Long): Long = Math.floorDiv(this, other)
internal actual infix fun Int.floorDiv(other: Int): Int = Math.floorDiv(this, other)
internal actual infix fun Long.floorDiv(other: Int): Long = this floorDiv other.toLong()

internal actual infix fun Long.plusExact(other: Long): Long = Math.addExact(this, other)
internal actual infix fun Int.plusExact(other: Int): Int = Math.addExact(this, other)

internal actual infix fun Long.minusExact(other: Long): Long = Math.subtractExact(this, other)
internal actual infix fun Int.minusExact(other: Int): Int = Math.subtractExact(this, other)

internal actual infix fun Long.timesExact(other: Long): Long = Math.multiplyExact(this, other)
internal actual infix fun Int.timesExact(other: Int): Int = Math.multiplyExact(this, other)
internal actual infix fun Long.timesExact(other: Int): Long = this timesExact other.toLong()

internal actual fun Int.negateExact(): Int = Math.negateExact(this)
internal actual fun Long.negateExact(): Long = Math.negateExact(this)

internal actual fun Long.toIntExact(): Int = Math.toIntExact(this)