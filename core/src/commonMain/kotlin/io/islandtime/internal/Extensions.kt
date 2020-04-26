package io.islandtime.internal

internal infix fun Long.plusExact(other: Int): Long = this plusExact other.toLong()
internal infix fun Long.minusExact(other: Int): Long = this minusExact other.toLong()

/**
 * Append a number to a string, padding it with zeros as necessary to reach a desired length
 * @param number the number to pad -- must be positive or zero
 * @param length minimum length of the appended string
 */
internal fun StringBuilder.appendZeroPadded(number: Int, length: Int): StringBuilder {
    val requiredPadding = length - number.lengthInDigits

    if (requiredPadding > 0) {
        append(ZERO_PAD[requiredPadding])
    }

    return append(number)
}

/**
 * Append a number to a string, padding it with [padChar] as necessary to reach a desired length
 * @param number the number to pad -- must be positive or zero
 * @param length minimum length of the appended string
 * @param padChar the character to pad with
 */
internal fun StringBuilder.appendWithPaddedStart(number: Long, length: Int, padChar: Char): StringBuilder {
    val requiredPadding = length - number.lengthInDigits
    repeat(requiredPadding) { append(padChar) }
    return append(number)
}

internal fun Int.toZeroPaddedString(length: Int): String {
    return buildString { appendZeroPadded(this@toZeroPaddedString, length) }
}

internal fun Long.toZeroPaddedString(length: Int): String {
    return buildString { appendWithPaddedStart(this@toZeroPaddedString, length, '0') }
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

private inline val Long.lengthInDigits
    get() = when {
        this < 10L -> 1
        this < 100L -> 2
        this < 1_000L -> 3
        this < 10_000L -> 4
        this < 100_000L -> 5
        this < 1_000_000L -> 6
        this < 10_000_000L -> 7
        this < 100_000_000L -> 8
        this < 1_000_000_000L -> 9
        this < 10_000_000_000L -> 10
        this < 100_000_000_000L -> 11
        this < 1_000_000_000_000L -> 12
        this < 10_000_000_000_000L -> 13
        this < 100_000_000_000_000L -> 14
        this < 1_000_000_000_000_000L -> 15
        this < 10_000_000_000_000_000L -> 16
        this < 100_000_000_000_000_000L -> 17
        this < 1_000_000_000_000_000_000L -> 18
        else -> 19
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
    "000000000",
    "0000000000"
)