package io.islandtime.internal

internal infix fun Long.plusExact(other: Int): Long = this plusExact other.toLong()
internal infix fun Long.minusExact(other: Int): Long = this minusExact other.toLong()

/**
 * Append a number to a string, padding it with zeros as necessary to reach a desired length
 * @param number The number to pad -- must be positive or zero
 * @param length Minimum length of the appended string
 */
internal fun StringBuilder.appendZeroPadded(number: Int, length: Int): StringBuilder {
    require(length <= 10) { "length must be <= 10" }
    val requiredPadding = length - number.lengthInDigits

    if (requiredPadding > 0) {
        append(ZERO_PAD[requiredPadding])
    }

    return append(number)
}

internal fun Int.toZeroPaddedString(length: Int): String {
    return buildString { appendZeroPadded(this@toZeroPaddedString, length) }
}

private inline val Int.lengthInDigits
    get() = when {
        this < 10 -> 1
        this < 100 -> 2
        this < 1_000 -> 3
        this < 10_000 -> 4
        this < 100_000 -> 5
        this < 1_000_000 -> 6
        this < 10_000_000 -> 7
        this < 100_000_000 -> 8
        this < 1_000_000_000 -> 9
        else -> 10
    }

private val ZERO_PAD = arrayOf(
    "",
    "0",
    "00",
    "000",
    "0000",
    "00000",
    "000000",
    "0000000",
    "00000000",
    "000000000"
)
