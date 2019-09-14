@file:Suppress("NewApi")

package dev.erikchristensen.islandtime.internal

internal actual infix fun Long.floorMod(other: Long): Long = Math.floorMod(this, other)
internal actual infix fun Int.floorMod(other: Int): Int = Math.floorMod(this, other)
internal actual infix fun Long.floorDiv(other: Long): Long = Math.floorDiv(this, other)
internal actual infix fun Int.floorDiv(other: Int): Int = Math.floorDiv(this, other)
internal actual fun Long.toIntExact(): Int = Math.toIntExact(this)