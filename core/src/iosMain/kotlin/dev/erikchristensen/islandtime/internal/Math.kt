package dev.erikchristensen.islandtime.internal

internal actual infix fun Long.floorMod(other: Long): Long {
    return ((this % other) + other) % other
}

internal actual infix fun Int.floorMod(other: Int): Int {
    return ((this % other) + other) % other
}

internal actual infix fun Long.floorDiv(other: Long): Long {
    val result = this / other
    // if the signs are different and modulo not zero, round down
    return if (this xor other < 0 && result * other != this) {
        result - 1
    } else {
        result
    }
}

internal actual infix fun Int.floorDiv(other: Int): Int {
    val result = this / other
    // if the signs are different and modulo not zero, round down
    return if (this xor other < 0 && result * other != this) {
        result - 1
    } else {
        result
    }
}

internal actual fun Long.toIntExact(): Int {
    if (this !in Int.MIN_VALUE..Int.MAX_VALUE) {
        throw ArithmeticException("'$this' can't be converted to Int without overflow")
    }
    return toInt()
}