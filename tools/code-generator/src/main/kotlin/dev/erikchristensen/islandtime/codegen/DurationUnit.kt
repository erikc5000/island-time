package dev.erikchristensen.islandtime.codegen

enum class DurationUnit(
    val pluralName: String,
    private val conversionFactor: Int,
    val isoPeriodUnit: Char
) {
    DAYS("Days", 1, 'D'),
    HOURS("Hours", 24, 'H'),
    MINUTES("Minutes", 60, 'M'),
    SECONDS("Seconds", 60, 'S'),
    MILLISECONDS("Milliseconds", 1_000, 'S') {
        override val isoPeriodIsFractional = true
        override val isoPeriodDecimalPlaces = 3
        override val isoPeriodUnitConversionFactor get() = SECONDS.per(this).value.toInt()
        override val forceLongInOperators = true
    },
    MICROSECONDS("Microseconds", 1_000, 'S') {
        override val isoPeriodIsFractional = true
        override val isoPeriodDecimalPlaces = 6
        override val isoPeriodUnitConversionFactor get() = SECONDS.per(this).value.toInt()
        override val forceLongInOperators = true
    },
    NANOSECONDS("Nanoseconds", 1_000, 'S') {
        override val isoPeriodIsFractional = true
        override val isoPeriodDecimalPlaces = 9
        override val isoPeriodUnitConversionFactor get() = SECONDS.per(this).value.toInt()
        override val forceLongInOperators = true
    };

    val intName: String get() = "Int$pluralName"
    val longName: String get() = "Long$pluralName"
    private val singularName: String get() = pluralName.dropLast(1)
    val lowerCaseName: String get() = pluralName.toLowerCase()
    val valueName: String get() = "value"

    open val isoPeriodIsFractional: Boolean = false
    open val isoPeriodDecimalPlaces: Int = 0
    open val isoPeriodUnitConversionFactor: Int = 1
    open val forceLongInOperators: Boolean = false

    fun per(unit: DurationUnit): DurationConstant {
        val progression = when {
            unit < this -> this.ordinal downTo unit.ordinal + 1
            else -> this.ordinal + 1..unit.ordinal
        }

        val propertyName = "${pluralName}_PER_${unit.singularName}".toUpperCase()
        val value = progression.map { values()[it].conversionFactor.toLong() }
            .fold(1L) { total, factor -> Math.multiplyExact(total, factor) }

        return DurationConstant(propertyName, value)
    }
}

data class DurationConstant(
    val propertyName: String,
    val value: Long
) {
    val isEmpty: Boolean get() = value == 1L
}