package io.islandtime.codegen.generators

import com.squareup.kotlinpoet.FileSpec
import io.islandtime.codegen.SingleFileGenerator
import io.islandtime.codegen.descriptions.DateTimeDescription
import io.islandtime.codegen.descriptions.DateTimeDescription.*
import io.islandtime.codegen.descriptions.TemporalUnitDescription
import io.islandtime.codegen.dsl.FileBuilder
import io.islandtime.codegen.dsl.FunctionBuilder
import io.islandtime.codegen.dsl.file

object DateConversionsGenerator : SingleFileGenerator() {
    override fun generateSingle(): FileSpec = buildDateConversionsFile()
}

private fun buildDateConversionsFile() = file(
    packageName = "io.islandtime",
    fileName = "_Conversions",
    jvmName = "DateTimesKt"
) {
    DateTimeDescription.entries.forEach { receiverClass ->
        DateTimeDescription.entries
            .filter { otherClass -> receiverClass.convertsDirectlyTo(otherClass) }
            .forEach { otherClass -> buildConversionFunction(from = receiverClass, to = otherClass) }
    }
}

private fun FileBuilder.buildConversionFunction(from: DateTimeDescription, to: DateTimeDescription) {
    function(name = "to${to.name}") {
        kdoc {
            "Returns this ${from.simpleName} with the precision reduced to the ${to.smallestUnit.lowerSingularName}."
        }

        receiver(from.typeName)
        returns(to.typeName)

        if (from.smallestUnit < TemporalUnitDescription.DAYS) {
            delegatesTo(from.datePropertyName)
        } else {
            construct(from, to)
        }
    }
}

private fun FunctionBuilder.construct(from: DateTimeDescription, to: DateTimeDescription) {
    code {
        when (to) {
            Year -> {
                require(from in arrayOf(Date, YearMonth)) { "Cannot construct '$to' from '$from'" }
                "return Year(year)"
            }
            YearMonth -> {
                require(from == Date) { "Cannot construct '$to' from '$from'" }
                "return YearMonth(year, month)"
            }
            else -> throw IllegalArgumentException("Cannot construct '$to'")
        }
    }
}
