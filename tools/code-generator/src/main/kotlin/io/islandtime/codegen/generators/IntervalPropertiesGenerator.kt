package io.islandtime.codegen.generators

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import io.islandtime.codegen.*
import io.islandtime.codegen.descriptions.IntervalDescription
import io.islandtime.codegen.descriptions.IntervalDescription.*
import io.islandtime.codegen.descriptions.TemporalUnitDescription
import io.islandtime.codegen.dsl.FileBuilder
import io.islandtime.codegen.dsl.file

object IntervalPropertiesGenerator : SingleFileGenerator() {
    override fun generateSingle(): FileSpec = buildPropertiesFile()
}

private fun buildPropertiesFile() = file(
    packageName = "io.islandtime.ranges",
    fileName = "_Properties",
    jvmName = "RangesKt"
) {
    IntervalDescription.values().forEach { buildPropertiesForClass(it) }
    buildPropertiesForTimePoint()
}

private fun FileBuilder.buildPropertiesForClass(receiverClass: IntervalDescription) {
    TemporalUnitDescription.values()
        .filter { unit ->
            unit >= receiverClass.elementDescription.smallestUnit &&
                ((unit.isDateBased && receiverClass.isDateBased) || !receiverClass.isTimePointInterval)
        }
        .forEach { unit ->
            val additionalText = if (receiverClass == DateRange && unit == TemporalUnitDescription.DAYS) {
                "A range is inclusive, so if the start and end date are the same, the length will be one day."
            } else {
                ""
            }

            buildLengthProperty(
                receiverTypeName = receiverClass.typeName,
                receiverSimpleName = receiverClass.simpleName,
                unit = unit,
                isSmallestUnit = unit == receiverClass.elementDescription.smallestUnit,
                additionalText = additionalText
            )
        }
}

private fun FileBuilder.buildPropertiesForTimePoint() {
    TemporalUnitDescription.values()
        .filter { it.isTimeBased && !it.isDateBased }
        .forEach { unit ->
            buildLengthProperty(
                receiverTypeName = ranges("TimePointInterval").parameterizedBy(STAR),
                receiverSimpleName = "interval",
                unit = unit,
                isSmallestUnit = unit == TemporalUnitDescription.NANOSECONDS
            )
        }
}

private fun FileBuilder.buildLengthProperty(
    receiverTypeName: TypeName,
    receiverSimpleName: String,
    unit: TemporalUnitDescription,
    isSmallestUnit: Boolean,
    additionalText: String = ""
) {
    property("lengthIn${unit.pluralName}", unit.className) {
        receiver(receiverTypeName)

        kdoc {
            val unitText = if (isSmallestUnit) unit.lowerPluralName else "whole ${unit.lowerPluralName}"

            """
                The number of $unitText between the start and end of this $receiverSimpleName. $additionalText
                
                @throws UnsupportedOperationException if the $receiverSimpleName isn't bounded
            """.trimIndent()
        }

        getter {
            code {
                using(
                    "unitClassName" to unit.className,
                    "unitConstructorProperty" to measures(unit.lowerPluralName),
                    "between" to root("between"),
                    "exception" to rangesInternal("throwUnboundedIntervalException")
                )
                """
                    |return when {
                    |    isEmpty() -> 0.%unitConstructorProperty:T
                    |    isBounded() -> %unitClassName:T.%between:T(start, endExclusive)
                    |    else -> %exception:T()
                    |}
                """.trimMargin()
            }
        }
    }
}
