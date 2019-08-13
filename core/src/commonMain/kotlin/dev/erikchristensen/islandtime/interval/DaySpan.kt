package dev.erikchristensen.islandtime.interval

inline class DaySpan(val value: Int) : Comparable<DaySpan> {
    override fun compareTo(other: DaySpan) = value.compareTo(other.value)
}

operator fun DaySpan.unaryPlus() = DaySpan(+value)
operator fun DaySpan.unaryMinus() = DaySpan(-value)
operator fun DaySpan.plus(days: DaySpan) = DaySpan(value + days.value)
operator fun DaySpan.minus(days: DaySpan) = plus(-days)

//operator fun DaySpan.times(factor: Long) = DaySpan(value * factor)
operator fun DaySpan.times(factor: Int) = DaySpan(value * factor)

//operator fun DaySpan.div(factor: Long) = DaySpan(value / factor)
operator fun DaySpan.div(factor: Int) = DaySpan(value / factor)

//operator fun DaySpan.rem(factor: Long) = DaySpan(value % factor)
operator fun DaySpan.rem(factor: Int) = DaySpan(value % factor)

fun DaySpan.toLong() = LongDaySpan(value.toLong())

inline val Int.days: DaySpan get() = DaySpan(this)
inline val Int.weeks: DaySpan get() = DaySpan(7 * this)

inline class LongDaySpan(val value: Long) : Comparable<LongDaySpan> {
    override fun compareTo(other: LongDaySpan) = value.compareTo(other.value)
}

operator fun LongDaySpan.unaryPlus() = LongDaySpan(+value)
operator fun LongDaySpan.unaryMinus() = LongDaySpan(-value)

operator fun LongDaySpan.plus(days: LongDaySpan) = LongDaySpan(value + days.value)
operator fun LongDaySpan.minus(days: LongDaySpan) = plus(-days)

operator fun LongDaySpan.times(factor: Long) = LongDaySpan(value * factor)
operator fun LongDaySpan.times(factor: Int) = LongDaySpan(value * factor)

operator fun LongDaySpan.div(factor: Long) = LongDaySpan(value / factor)
operator fun LongDaySpan.div(factor: Int) = LongDaySpan(value / factor)

operator fun LongDaySpan.rem(factor: Long) = LongDaySpan(value % factor)
operator fun LongDaySpan.rem(factor: Int) = LongDaySpan(value % factor)

fun LongDaySpan.toInt() = DaySpan(value.toInt())

inline val Long.days: LongDaySpan get() = LongDaySpan(this)
inline val Long.weeks: LongDaySpan get() = LongDaySpan(7 * this)