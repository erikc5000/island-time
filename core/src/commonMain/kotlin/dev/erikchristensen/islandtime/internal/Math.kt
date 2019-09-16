package dev.erikchristensen.islandtime.internal

internal expect infix fun Long.floorMod(other: Long): Long
internal expect infix fun Int.floorMod(other: Int): Int
internal expect infix fun Long.floorMod(other: Int): Long

internal expect infix fun Long.floorDiv(other: Long): Long
internal expect infix fun Int.floorDiv(other: Int): Int
internal expect infix fun Long.floorDiv(other: Int): Long

internal expect infix fun Long.plusExact(other: Long): Long
internal expect infix fun Int.plusExact(other: Int): Int

internal expect infix fun Long.minusExact(other: Long): Long
internal expect infix fun Int.minusExact(other: Int): Int

internal expect infix fun Long.timesExact(other: Long): Long
internal expect infix fun Int.timesExact(other: Int): Int
internal expect infix fun Long.timesExact(other: Int): Long

internal expect fun Long.toIntExact(): Int