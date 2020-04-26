package io.islandtime.format

@DslMarker
annotation class IslandTimeFormatDsl

@IslandTimeFormatDsl
interface LiteralFormatBuilder {
    /**
     * Append a character literal.
     */
    operator fun Char.unaryPlus() {
        literal(this)
    }

    /**
     * Append a string literal.
     */
    operator fun String.unaryPlus() {
        literal(this)
    }

    /**
     * Append a character literal.
     */
    fun literal(char: Char)

    /**
     * Append a string literal.
     */
    fun literal(string: String)
}

enum class FormatOption {
    NEVER,
    OPTIONAL,
    ALWAYS
}