package io.islandtime.codegen

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier

val TemporalUnitConversion.classType get() = if (valueFitsInInt) Int::class else Long::class
val TemporalUnitConversion.valueString get() = "$constantValue" + if (valueFitsInInt) "" else "L"

fun generateConstantsFileSpec(): FileSpec {
    return buildFileSpec(INTERNAL_PACKAGE_NAME, "_Constants") {
        addHeader("ConstantsKt")

        TemporalUnitDescription.values().flatMap { firstUnit ->
            TemporalUnitDescription.values()
                .filter { secondUnit -> secondUnit > firstUnit }
                .map { secondUnit -> firstUnit per secondUnit }
                .filter { conversion -> conversion.isSupportedAndNecessary() }
        }
            .map { conversion ->
                buildPropertySpec(conversion.constantName, conversion.classType) {
                    addAnnotation(PublishedApi::class)
                    addModifiers(KModifier.CONST, KModifier.INTERNAL)
                    initializer(conversion.valueString)
                }
            }
            .forEach { addProperty(it) }
    }
}