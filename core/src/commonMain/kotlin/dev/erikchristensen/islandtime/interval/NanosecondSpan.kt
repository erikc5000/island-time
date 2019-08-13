package dev.erikchristensen.islandtime.interval

inline class NanosecondSpan(val value: Int) : Comparable<NanosecondSpan> {
    override fun compareTo(other: NanosecondSpan) = value.compareTo(other.value)
}

operator fun NanosecondSpan.unaryPlus() = NanosecondSpan(+value)
operator fun NanosecondSpan.unaryMinus() = NanosecondSpan(-value)

operator fun NanosecondSpan.plus(nanoseconds: NanosecondSpan) = NanosecondSpan(value + nanoseconds.value)
operator fun NanosecondSpan.plus(hours: HourSpan) = this + hours.asNanoseconds()
operator fun NanosecondSpan.plus(minutes: MinuteSpan) = this + minutes.asNanoseconds()
operator fun NanosecondSpan.plus(seconds: SecondSpan) = this + seconds.asNanoseconds()

operator fun NanosecondSpan.minus(nanoseconds: NanosecondSpan) = plus(-nanoseconds)
operator fun NanosecondSpan.minus(hours: HourSpan) = plus(-hours)
operator fun NanosecondSpan.minus(minutes: MinuteSpan) = plus(-minutes)
operator fun NanosecondSpan.minus(seconds: SecondSpan) = plus(-seconds)

//operator fun NanosecondSpan.times(scalar: Long) = NanosecondSpan(value * scalar)
operator fun NanosecondSpan.times(scalar: Int) = NanosecondSpan(value * scalar)

//operator fun NanosecondSpan.div(scalar: Long) = NanosecondSpan(value / scalar)
operator fun NanosecondSpan.div(scalar: Int) = NanosecondSpan(value / scalar)

//operator fun NanosecondSpan.rem(scalar: Long) = NanosecondSpan(value % scalar)
operator fun NanosecondSpan.rem(scalar: Int) = NanosecondSpan(value % scalar)

fun NanosecondSpan.toLong() = LongNanosecondSpan(value.toLong())

inline val Int.nanoseconds: NanosecondSpan get() = NanosecondSpan(this)
inline val Int.microseconds: NanosecondSpan get() = NanosecondSpan(this * 1_000)
inline val Int.milliseconds: NanosecondSpan get() = NanosecondSpan(this * 1_000_000)

inline class LongNanosecondSpan(val value: Long) : Comparable<LongNanosecondSpan> {
    override fun compareTo(other: LongNanosecondSpan) = value.compareTo(other.value)
}

operator fun LongNanosecondSpan.unaryPlus() = LongNanosecondSpan(+value)
operator fun LongNanosecondSpan.unaryMinus() = LongNanosecondSpan(-value)

operator fun LongNanosecondSpan.plus(nanoseconds: LongNanosecondSpan) = LongNanosecondSpan(value + nanoseconds.value)
operator fun LongNanosecondSpan.plus(hours: LongHourSpan) = this + hours.asNanoseconds()
operator fun LongNanosecondSpan.plus(minutes: LongMinuteSpan) = this + minutes.asNanoseconds()
operator fun LongNanosecondSpan.plus(seconds: LongSecondSpan) = this + seconds.asNanoseconds()

operator fun LongNanosecondSpan.minus(nanoseconds: LongNanosecondSpan) = plus(-nanoseconds)
operator fun LongNanosecondSpan.minus(hours: LongHourSpan) = plus(-hours)
operator fun LongNanosecondSpan.minus(minutes: LongMinuteSpan) = plus(-minutes)
operator fun LongNanosecondSpan.minus(seconds: LongSecondSpan) = plus(-seconds)

operator fun LongNanosecondSpan.times(scalar: Long) = LongNanosecondSpan(value * scalar)
operator fun LongNanosecondSpan.times(scalar: Int) = LongNanosecondSpan(value * scalar)

operator fun LongNanosecondSpan.div(scalar: Long) = LongNanosecondSpan(value / scalar)
operator fun LongNanosecondSpan.div(scalar: Int) = LongNanosecondSpan(value / scalar)

operator fun LongNanosecondSpan.rem(scalar: Long) = LongNanosecondSpan(value % scalar)
operator fun LongNanosecondSpan.rem(scalar: Int) = LongNanosecondSpan(value % scalar)

fun LongNanosecondSpan.toInt() = NanosecondSpan(value.toInt())

inline val Long.nanoseconds: LongNanosecondSpan get() = LongNanosecondSpan(this)
inline val Long.microseconds: LongNanosecondSpan get() = LongNanosecondSpan(this * 1_000)
inline val Long.milliseconds: LongNanosecondSpan get() = LongNanosecondSpan(this * 1_000_000)