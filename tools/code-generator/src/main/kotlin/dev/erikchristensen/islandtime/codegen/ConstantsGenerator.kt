package dev.erikchristensen.islandtime.codegen

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier

fun generateConstantsFileSpec(): FileSpec {
    return buildFileSpec(INTERNAL_PACKAGE_NAME, "_Constants") {
        addHeader("ConstantsKt")

        DurationUnit.values().forEach { firstUnit ->
            DurationUnit.values().mapNotNull { secondUnit ->
                val constant = firstUnit.per(secondUnit)

                if (constant.isEmpty) {
                    null
                } else {
                    if (constant.valueFitsInInt) {
                        buildPropertySpec(constant.propertyName, Int::class) {
                            addModifiers(KModifier.CONST, KModifier.INTERNAL)
                            initializer("${constant.value}")
                        }
                    } else {
                        buildPropertySpec(constant.propertyName, Long::class) {
                            addModifiers(KModifier.CONST, KModifier.INTERNAL)
                            initializer("${constant.value}L")
                        }
                    }
                }
            }.forEach { addProperty(it) }
        }
    }
}