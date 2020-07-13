package io.islandtime.format

import io.islandtime.format.internal.IsoDateTimeFormatterBuilderImpl

enum class IsoFormat {
    BASIC,
    EXTENDED
}

//@TemporalFormatterDsl
//interface UtcOffsetFormatterBuilder {
//    var useUtcDesignatorWhenZero: Boolean
//    var minutes: FormatOption
//    var seconds: FormatOption
//}

//enum class CalendarUnit {
//    YEAR,
//    MONTH,
//    WEEK,
//    DAY
//}
//
//enum class IsoDateType {
//    YEAR,
//    YEAR_MONTH,
//    YEAR_MONTH_DAY,
//    YEAR_DAY
//}

enum class IsoTimeDesignator(val char: Char?) {
    T('T'),
    NONE(null),
    SPACE(' ') // Non-standard extension
}

@IslandTimeFormatDsl
interface IsoDateTimeFormatterBuilder {
    var format: IsoFormat
    var timeDesignator: IsoTimeDesignator

//    fun components(builder: IsoDateTimeComponentsFormatterBuilder.() -> Unit)
}

//enum class DayPrecisionType {
//    CALENDAR,
//    ORDINAL
//}
//
//sealed class DatePrecision {
//    object Year : DatePrecision()
//    object Month : DatePrecision()
//
//    class Day(val type: DayPrecisionType = DayPrecisionType.CALENDAR) : DatePrecision()
//}
//
//sealed class TimePrecision {
//    object Hour : TimePrecision()
//    object Minute : TimePrecision()
//    object Second : TimePrecision()
//
//    class FractionalSecond(val length: IntRange = 0..9, val increment: Int = 1) : TimePrecision() {
//        constructor(length: Int, val increment: Int = 1) : this(length..length, increment)
//    }
//}

//@IslandTimeFormatDsl
//interface IsoDateTimeComponentsFormatterBuilder {
//    fun date(builder: IsoDateFormatterBuilder.() -> Unit = {})
//
//    fun time(builder: IsoTimeFormatterBuilder.() -> Unit = {})
//
//    fun utcOffset(builder: UtcOffsetFormatterBuilder.() -> Unit = {})
//
//    // Non-standard extension
//    fun timeZoneId()
//}

//@TemporalFormatterDsl
//interface IsoDateFormatterBuilder {
//    var precision: DatePrecision
//    fun year(builder: IsoYearFormatterBuilder.() -> Unit)
////    fun month()
////    fun day()
//}
//
//@TemporalFormatterDsl
//interface IsoTimeFormatterBuilder {
//    var precision: TimePrecision
//}
//
//interface IsoYearFormatterBuilder {
//    var expanded: FormatOption
//}
//
//interface IsoExpandedYearRepresentationBuilder {
//    //var digits
//}

inline fun isoDateTimeFormatter(
    builder: IsoDateTimeFormatterBuilder.() -> Unit = {}
): TemporalFormatter {
    return IsoDateTimeFormatterBuilderImpl().apply(builder).build()
}

//val formatter = isoDateTimeFormatter {
//    format = IsoFormat.BASIC
//    timeDesignator = IsoTimeDesignator.SPACE
//
//    components {
//        date {
//            precision = DatePrecision.Day(DayPrecisionType.ORDINAL)
//
//            year {
//                expanded = FormatOption.ALWAYS
//            }
//        }
//        time {
//            precision = TimePrecision.FractionalSecond(9)
//        }
//        utcOffset {
//            minutes = FormatOption.OPTIONAL
//        }
//    }
//}

//enum class IsoTimeIntervalType {
//    START_END,
//    START_DURATION,
//    DURATION_END
//}
//
//enum class IsoTimeIntervalSeparator {
//    SOLIDUS,
//    DOUBLE_HYPHEN
//}
//
//interface IsoTimeIntervalFormatterBuilder {
//    var type: IsoTimeIntervalType
//    var separator: IsoTimeIntervalSeparator
//}

//inline fun isoTimeIntervalFormatter(builder: IsoTimeIntervalFormatterBuilder.() -> Unit): IsoTimeIntervalFormatter {
//
//}