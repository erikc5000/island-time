package io.islandtime.codegen.generators

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import io.islandtime.codegen.SingleFileGenerator
import io.islandtime.codegen.descriptions.TemporalUnitConversion
import io.islandtime.codegen.descriptions.TemporalUnitDescription
import io.islandtime.codegen.descriptions.per
import io.islandtime.codegen.dsl.file

private val TemporalUnitConversion.classType get() = if (valueFitsInInt) Int::class else Long::class
private val TemporalUnitConversion.valueString get() = "$constantValue" + if (valueFitsInInt) "" else "L"

object ConstantsGenerator : SingleFileGenerator() {
    override fun generateSingle(): FileSpec = buildConstantsFile()
}

private fun buildConstantsFile() = file(
    packageName = "io.islandtime.internal",
    fileName = "_Constants",
    jvmName = "ConstantsKt"
) {
    TemporalUnitDescription.values()
        .flatMap { firstUnit ->
            TemporalUnitDescription.values()
                .filter { secondUnit -> secondUnit > firstUnit }
                .map { secondUnit -> firstUnit per secondUnit }
                .filter { conversion -> conversion.isSupportedAndNecessary() }
        }
        .map { conversion ->
            property(conversion.constantName, conversion.classType) {
                annotation(PublishedApi::class)
                modifiers(KModifier.CONST, KModifier.INTERNAL)
                initializer { conversion.valueString }
            }
        }
}