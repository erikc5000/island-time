package io.islandtime.codegen.descriptions

enum class TemporalUnitDescription(
    val pluralName: String,
    val conversionFactor: Int,
    val isoPeriodUnit: Char
) {
    NANOSECONDS("Nanoseconds", 1_000, 'S') {
        override val isoPeriodIsFractional = true
        override val isoPeriodDecimalPlaces = 9
        override val isoPeriodUnitConversion get() = per(SECONDS)
        override val forceLongInOperators = true
    },
    MICROSECONDS("Microseconds", 1_000, 'S') {
        override val isoPeriodIsFractional = true
        override val isoPeriodDecimalPlaces = 6
        override val isoPeriodUnitConversion get() = per(SECONDS)
        override val forceLongInOperators = true
    },
    MILLISECONDS("Milliseconds", 1_000, 'S') {
        override val isoPeriodIsFractional = true
        override val isoPeriodDecimalPlaces = 3
        override val isoPeriodUnitConversion get() = per(SECONDS)
        override val forceLongInOperators = true
    },
    SECONDS("Seconds", 60, 'S'),
    MINUTES("Minutes", 60, 'M'),
    HOURS("Hours", 24, 'H'),
    DAYS("Days", 7, 'D') {
        override val isoPeriodPrefix = "P"
    },
    WEEKS("Weeks", 0, 'W'),
    MONTHS("Months", 12, 'M'),
    YEARS("Years", 10, 'Y'),
    DECADES("Decades", 10, 'Y') {
        override val isoPeriodUnitConversion get() = YEARS.per(DECADES)
    },
    CENTURIES("Centuries", 1, 'Y') {
        override val isoPeriodUnitConversion get() = YEARS.per(CENTURIES)
        override val singularName: String = "Century"
    };

    val deprecatedIntName: String get() = "Int$pluralName"
    val deprecatedLongName: String get() = "Long$pluralName"
    open val singularName: String get() = pluralName.dropLast(1)
    val lowerSingularName: String get() = singularName.lowercase()
    val lowerPluralName: String get() = pluralName.lowercase()
    val valueName: String get() = "value"
    val inUnitPropertyName: String get() = "in$pluralName"
    val inUnitUncheckedPropertyName: String get() = "${inUnitPropertyName}Unchecked"
    val inWholeUnitPropertyName: String get() = "inWhole$pluralName"
    val deprecatedInWholeUnitPropertyName: String get() = "in$pluralName"

    open val isoPeriodPrefix: String get() = if (isTimeBased) "PT" else "P"
    open val isoPeriodIsFractional: Boolean = false
    open val isoPeriodDecimalPlaces: Int = 0
    open val isoPeriodUnitConversion: TemporalUnitConversion get() = TemporalUnitConversion(this, this)
    val isoPeriodZeroString: String get() = "${isoPeriodPrefix}0$isoPeriodUnit"
    open val forceLongInOperators: Boolean = false
    val isDateBased get() = this >= DAYS
    val isTimeBased get() = this <= DAYS
}

infix fun TemporalUnitDescription.per(unit: TemporalUnitDescription): TemporalUnitConversion {
    return TemporalUnitConversion(this, unit)
}

enum class ConversionOperator {
    NONE,
    TIMES,
    DIV
}

data class TemporalUnitConversion(
    val fromUnit: TemporalUnitDescription,
    val toUnit: TemporalUnitDescription
) {
    fun isSupportedAndNecessary() = isSupported() && isNecessary()

    fun isSupported(): Boolean {
        return ((fromUnit.isDateBased && toUnit.isDateBased) || (fromUnit.isTimeBased && toUnit.isTimeBased)) &&
            constantValue > 0L
    }

    fun isNecessary() = operator != ConversionOperator.NONE

    val constantName: String by lazy(LazyThreadSafetyMode.NONE) {
        val (smallerUnit, largerUnit) = orderedFromSmallerToLargerUnit()
        "${smallerUnit.pluralName}_PER_${largerUnit.singularName}".uppercase()
    }

    val id: String by lazy(LazyThreadSafetyMode.NONE) {
        val (smallerUnit, largerUnit) = orderedFromSmallerToLargerUnit()
        "${smallerUnit.lowerPluralName}Per${largerUnit.singularName}"
    }

    val operator: ConversionOperator
        get() = when {
            toUnit == fromUnit -> ConversionOperator.NONE
            toUnit < fromUnit -> ConversionOperator.TIMES
            else -> ConversionOperator.DIV
        }

    val constantValue: Long by lazy {
        val (smallerUnit, largerUnit) = orderedFromSmallerToLargerUnit()

        (smallerUnit.ordinal until largerUnit.ordinal)
            .map { TemporalUnitDescription.entries[it].conversionFactor.toLong() }
            .fold(1L) { total, factor -> Math.multiplyExact(total, factor) }
    }

    val valueFitsInInt: Boolean
        get() = constantValue in Int.MIN_VALUE..Int.MAX_VALUE

    private fun orderedFromSmallerToLargerUnit() = if (fromUnit <= toUnit) {
        fromUnit to toUnit
    } else {
        toUnit to fromUnit
    }
}
