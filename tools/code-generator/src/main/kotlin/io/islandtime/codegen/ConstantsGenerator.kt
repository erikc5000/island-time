package io.islandtime.codegen

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier

val DurationConstant.classType get() = if (valueFitsInInt) Int::class else Long::class
val DurationConstant.valueString get() = "$value" + if (valueFitsInInt) "" else "L"

fun generateConstantsFileSpec(): FileSpec {
    return buildFileSpec(INTERNAL_PACKAGE_NAME, "_Constants") {
        addHeader("ConstantsKt")

        DurationUnit.values().flatMap { firstUnit ->
            DurationUnit.values().map { secondUnit -> firstUnit.per(secondUnit) }
        }
            .filter { constant -> !constant.isEmpty }
            .map { constant ->
                buildPropertySpec(constant.propertyName, constant.classType) {
                    addAnnotation(PublishedApi::class)
                    addModifiers(KModifier.CONST, KModifier.INTERNAL)
                    initializer(constant.valueString)
                }
            }
            .forEach { addProperty(it) }
    }
}