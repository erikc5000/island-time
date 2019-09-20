package dev.erikchristensen.islandtime.internal

//
// Adapted from https://github.com/ThreeTen/threetenbp/blob/master/src/main/java/org/threeten/bp/jdk8/Jdk8Methods.java
//

internal actual infix fun Long.floorMod(other: Long): Long {
    return ((this % other) + other) % other
}

internal actual infix fun Int.floorMod(other: Int): Int {
    return ((this % other) + other) % other
}

internal actual infix fun Long.floorMod(other: Int): Long = this floorMod other.toLong()

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

internal actual infix fun Long.floorDiv(other: Int): Long = this floorDiv other.toLong()

internal actual infix fun Long.plusExact(other: Long): Long {
    val result = this + other

    if (this xor result < 0L && this xor other >= 0L) {
        throw ArithmeticException("'$this + $other' overflows a Long")
    }

    return result
}

internal actual infix fun Int.plusExact(other: Int): Int {
    val result = this + other

    if (this xor result < 0 && this xor other >= 0) {
        throw ArithmeticException("'$this + $other' overflows an Int")
    }

    return result
}

internal actual infix fun Long.minusExact(other: Long): Long {
    val result = this - other

    if (this xor result < 0L && this xor other < 0L) {
        throw ArithmeticException("'$this - $other' overflows a Long")
    }

    return result
}

internal actual infix fun Int.minusExact(other: Int): Int {
    val result = this - other

    if (this xor result < 0 && this xor other < 0) {
        throw ArithmeticException("'$this - $other' overflows an Int")
    }

    return result
}

internal actual infix fun Long.timesExact(other: Long): Long {
    return when {
        other == 1L -> this
        this == 1L -> other
        this == 0L || other == 0L -> 0L
        else -> {
            val total = this * other

            if (total / other != this ||
                (this == Long.MIN_VALUE && other == -1L) ||
                (other == Long.MIN_VALUE && this == -1L)
            ) {
                throw ArithmeticException("'$this * $other' overflows a Long")
            }

            total
        }
    }
}

internal actual infix fun Int.timesExact(other: Int): Int {
    val total = this.toLong() * other.toLong()

    if (total !in Int.MIN_VALUE..Int.MAX_VALUE) {
        throw ArithmeticException("'$this * $other' overflows an Int")
    }

    return total.toInt()
}

internal actual infix fun Long.timesExact(other: Int): Long = this timesExact other.toLong()

internal actual fun Int.negateExact(): Int {
    if (this == Int.MIN_VALUE) {
        throw ArithmeticException("'$this' can't be negated without overflow")
    }
    return -this
}

internal actual fun Long.negateExact(): Long {
    if (this == Long.MIN_VALUE) {
        throw ArithmeticException("'$this' can't be negated without overflow")
    }
    return -this
}

internal actual fun Long.toIntExact(): Int {
    if (this !in Int.MIN_VALUE..Int.MAX_VALUE) {
        throw ArithmeticException("'$this' can't be converted to Int without overflow")
    }

    return toInt()
}