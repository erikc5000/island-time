package dev.erikchristensen.islandtime.parser

@DslMarker
annotation class DateTimeParserDsl

@DateTimeParserDsl
interface DateTimeParserBuilder {
    fun year(length: Int, block: FixedLengthNumberParserBuilder.() -> Unit = {})
    fun monthNumber(length: Int, block: FixedLengthNumberParserBuilder.() -> Unit = {})
    fun dayOfYear(length: Int, block: FixedLengthNumberParserBuilder.() -> Unit = {})
    fun dayOfMonth(length: Int, block: FixedLengthNumberParserBuilder.() -> Unit = {})
    fun dayOfWeekNumber(length: Int, block: FixedLengthNumberParserBuilder.() -> Unit = {})
    fun hourOfDay(length: Int, block: FixedLengthNumberParserBuilder.() -> Unit = {})
    fun minuteOfHour(length: Int, block: FixedLengthNumberParserBuilder.() -> Unit = {})
    fun secondOfMinute(length: Int, block: FixedLengthNumberParserBuilder.() -> Unit = {})
    fun timeOffsetHours(length: Int, block: FixedLengthNumberParserBuilder.() -> Unit = {})
    fun timeOffsetMinutes(length: Int, block: FixedLengthNumberParserBuilder.() -> Unit = {})
    fun timeOffsetSeconds(length: Int, block: FixedLengthNumberParserBuilder.() -> Unit = {})

    fun timeOffsetSign()
    fun timeOffsetUtc()

//    fun periodOfYears(minLength: Int, maxLength: Int, block: VariableLengthNumberParserBuilder.() -> Unit = {})
//    fun periodOfMonths(minLength: Int, maxLength: Int, block: VariableLengthNumberParserBuilder.() -> Unit = {})
//    fun periodOfDays(minLength: Int, maxLength: Int, block: VariableLengthNumberParserBuilder.() -> Unit = {})
//    fun durationOfHours(minLength: Int, maxLength: Int, block: VariableLengthNumberParserBuilder.() -> Unit = {})
//    fun durationOfMinutes(minLength: Int, maxLength: Int, block: VariableLengthNumberParserBuilder.() -> Unit = {})
//    fun durationOfSeconds(minLength: Int, maxLength: Int, block: VariableLengthNumberParserBuilder.() -> Unit = {})

    operator fun Char.unaryPlus() {
        literal(this)
    }

    operator fun String.unaryPlus() {
        literal(this)
    }

    fun literal(char: Char)
    fun literal(string: String)

    fun optional(block: DateTimeParserBuilder.() -> Unit)

    fun anyOf(vararg block: DateTimeParserBuilder.() -> Unit)
    fun anyOf(vararg subParsers: DateTimeParser)

    fun subParser(subParser: DateTimeParser)
}

@DateTimeParserDsl
interface FixedLengthNumberParserBuilder {
    var signExceedsLength: Boolean

    fun enforceSignStyle(signStyle: SignStyle)
}

//@DateTimeParserDsl
//class VariableLengthNumberParserBuilder {
//    private var signStyle: SignStyle? = null
//    var signExceedsLength: Boolean = false
//
//    fun enforceSignStyle(signStyle: SignStyle) {
//        this.signStyle = signStyle
//    }
//}