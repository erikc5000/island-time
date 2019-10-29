package io.islandtime.codegen

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import kotlin.reflect.KClass

val DurationUnit.intClassName get() = ClassName(MEASURES_PACKAGE_NAME, intName)
val DurationUnit.longClassName get() = ClassName(MEASURES_PACKAGE_NAME, longName)

fun generateDurationUnitFileSpecs(): List<FileSpec> {
    return DurationUnit.values().map { it.toFileSpec() }
}

fun DurationUnit.toFileSpec(): FileSpec {
    return buildFileSpec(
        MEASURES_PACKAGE_NAME,
        "_$pluralName"
    ) {
        addHeader("${pluralName}Kt")
        buildClasses().forEach { addType(it) }
        buildExtensionProperties().forEach { addProperty(it) }
    }
}

fun DurationUnit.buildClasses(): List<TypeSpec> {
    return listOf(
        buildClass(intClassName, Int::class),
        buildClass(longClassName, Long::class)
    )
}

fun DurationUnit.buildExtensionProperties(): List<PropertySpec> {
    return buildExtensionProperties(intClassName, Int::class) +
        buildExtensionProperties(longClassName, Long::class)
}

fun DurationUnit.buildClass(
    className: ClassName,
    primitiveType: KClass<*>
): TypeSpec {
    return buildClassTypeSpec(className) {
        addModifiers(KModifier.INLINE)
        addAnnotation(
            buildAnnotationSpec(Suppress::class) {
                addMember("%S", "NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
            }
        )
        primaryConstructor(
            buildConstructorFunSpec {
                // addModifiers(KModifier.INTERNAL)
                addParameter(valueName, primitiveType)
            }
        )
        addProperty(
            buildPropertySpec(valueName, primitiveType) { initializer(valueName) }
        )
        addSuperinterface(
            ClassName("kotlin", "Comparable").parameterizedBy(className)
        )
        addFunction(
            buildFunSpec("compareTo") {
                addModifiers(KModifier.OVERRIDE)
                addParameter("other", className)
                returns(Int::class)
                addStatement("return this.value.compareTo(other.value)")
            }
        )
        addFunction(
            buildFunSpec("toString") {
                addModifiers(KModifier.OVERRIDE)
                returns(String::class)

                if (isoPeriodIsFractional) {
                    val fractionalPartConversionString = if (primitiveType != Int::class) {
                        "(absValue %% $isoPeriodUnitConversionFactor).toInt()"
                    } else {
                        "absValue %% $isoPeriodUnitConversionFactor"
                    }

                    addStatement(
                        """
                            |return if (this.isZero) {
                            |    "$isoPeriodZeroString"
                            |} else {
                            |    buildString {
                            |        append("$isoPeriodPrefix")
                            |        val absValue = $valueName.%T
                            |        val wholePart = absValue / $isoPeriodUnitConversionFactor
                            |        val fractionalPart = $fractionalPartConversionString
                            |        if (isNegative) { append('-') }
                            |        append(wholePart)
                            |        if (fractionalPart != 0) {
                            |            append('.')
                            |            append(fractionalPart.%T($isoPeriodDecimalPlaces).dropLastWhile { it == '0' })
                            |        }
                            |        append('$isoPeriodUnit')
                            |    }
                            |}
                        """.trimMargin(),
                        ClassName("kotlin.math", "absoluteValue"),
                        ClassName(INTERNAL_PACKAGE_NAME, "toZeroPaddedString")
                    )
                } else {
                    addStatement(
                        """
                            |return if (this.isZero) {
                            |    "$isoPeriodZeroString"
                            |} else {
                            |    buildString {
                            |        append("$isoPeriodPrefix")
                            |        append($valueName)
                            |        append('$isoPeriodUnit')
                            |    }
                            |}
                        """.trimMargin()
                    )
                }
            }
        )
        buildUnitConversionPropertiesAndFunctions(primitiveType).forEach {
            when (it) {
                is FunSpec -> addFunction(it)
                is PropertySpec -> addProperty(it)
            }
        }
        addFunctions(
            buildOperators(className, primitiveType) +
                    buildToComponentsFunctions(primitiveType) +
//                buildNonExpandedToComponentsFunctions(primitiveType) +
                    buildPrimitiveConversionFunctions(primitiveType)
        )
        addProperties(
            listOf(
                buildPropertySpec("isZero", Boolean::class) {
                    getter(buildGetterFunSpec {
                        addModifiers(KModifier.INLINE)
                        addStatement("return this.$valueName == ${primitiveType.zero}")
                    })
                },
                buildPropertySpec("isNegative", Boolean::class) {
                    getter(buildGetterFunSpec {
                        addModifiers(KModifier.INLINE)
                        addStatement("return this.$valueName < ${primitiveType.zero}")
                    })
                },
                buildPropertySpec("isPositive", Boolean::class) {
                    getter(buildGetterFunSpec {
                        addModifiers(KModifier.INLINE)
                        addStatement("return this.$valueName > ${primitiveType.zero}")
                    })
                },
                buildPropertySpec("absoluteValue", className) {
                    getter(
                        buildGetterFunSpec {
                            addStatement(
                                "return %T(this.$valueName.%T)",
                                className,
                                ClassName("kotlin.math", "absoluteValue")
                            )
                        }
                    )
                }
            )
        )
        addType(
            buildCompanionObjectTypeSpec {
                addProperty(
                    buildPropertySpec("MIN", className) {
                        initializer("%T(%T.MIN_VALUE)", className, primitiveType)
                    }
                )
                addProperty(
                    buildPropertySpec("MAX", className) {
                        initializer("%T(%T.MAX_VALUE)", className, primitiveType)
                    }
                )
            }
        )
    }
}

fun DurationUnit.buildPrimitiveConversionFunctions(
    primitiveType: KClass<*>
): List<FunSpec> {
    return when (primitiveType) {
        Int::class -> listOf(
            buildFunSpec("toLong") {
                addStatement("return %T(this.$valueName.toLong())", longClassName)
            }
        )
        Long::class -> listOf(
            buildFunSpec("toInt") {
                addStatement("return %T(this.$valueName.toInt())", intClassName)
            },
            buildFunSpec("toIntExact") {
                addStatement(
                    "return %T(this.$valueName.%T())", intClassName,
                    ClassName(INTERNAL_PACKAGE_NAME, "toIntExact")
                )
            }
        )
        else -> throw IllegalArgumentException("Unsupported primitive type: $primitiveType")
    }
}

fun DurationUnit.buildExtensionProperties(
    className: ClassName,
    primitiveType: KClass<*>
): List<PropertySpec> {
    return listOf(
        buildPropertySpec(lowerCaseName, className) {
            receiver(primitiveType)
            getter(buildGetterFunSpec {
                addStatement("return %T(this)", className)
            })
        }
    )
}

fun DurationUnit.buildOperators(
    className: ClassName,
    primitiveType: KClass<*>
): List<FunSpec> {
    return buildUnaryOperators(className) +
        buildPlusOperators(className, primitiveType) +
            buildMinusOperators() +
        buildTimesOperators(className, primitiveType) +
        buildDivOperators(className, primitiveType) +
        buildRemOperators(className, primitiveType)
}

fun DurationUnit.buildUnaryOperators(className: ClassName): List<FunSpec> {
    return listOf(
        buildFunSpec("unaryMinus") {
            addModifiers(KModifier.OPERATOR)
            addStatement("return %T(-$valueName)", className)
        }
    )
}

fun DurationUnit.buildPlusOperators(
    className: ClassName,
    primitiveType: KClass<*>
): List<FunSpec> {
    return DurationUnit.values().flatMap { other ->
        listOf(
            buildFunSpec("plus") {
                addModifiers(KModifier.OPERATOR)
                addParameter(other.lowerCaseName, other.intClassName)

                when {
                    other.ordinal < ordinal -> {
                        if (forceLongInOperators && primitiveType != Long::class) {
                            addStatement("return this.toLong() + ${other.lowerCaseName}.$inUnitPropertyName")
                        } else {
                            addStatement("return this + ${other.lowerCaseName}.$inUnitPropertyName")
                        }
                    }
                    other.ordinal > ordinal -> {
                        if (forceLongInOperators && primitiveType != Long::class) {
                            addStatement("return this.toLong().${other.inUnitPropertyName} + ${other.lowerCaseName}.toLong()")
                        } else {
                            addStatement("return this.${other.inUnitPropertyName} + ${other.lowerCaseName}")
                        }
                    }
                    else -> {
                        if (forceLongInOperators && primitiveType != Long::class) {
                            addStatement(
                                "return %T(this.$valueName.toLong() + ${other.lowerCaseName}.$valueName)",
                                longClassName
                            )
                        } else {
                            addStatement(
                                "return %T(this.$valueName + ${other.lowerCaseName}.$valueName)",
                                className
                            )
                        }
                    }
                }
            },
            buildFunSpec("plus") {
                addModifiers(KModifier.OPERATOR)
                addParameter(other.lowerCaseName, other.longClassName)

                when {
                    other.ordinal < ordinal -> {
                        if (primitiveType != Long::class) {
                            addStatement("return this.toLong() + ${other.lowerCaseName}.$inUnitPropertyName")
                        } else {
                            addStatement("return this + ${other.lowerCaseName}.$inUnitPropertyName")
                        }
                    }
                    other.ordinal > ordinal -> {
                        if (primitiveType != Long::class) {
                            addStatement("return this.toLong().${other.inUnitPropertyName} + ${other.lowerCaseName}")
                        } else {
                            addStatement("return this.${other.inUnitPropertyName} + ${other.lowerCaseName}")
                        }
                    }
                    else -> {
                        if (primitiveType != Long::class) {
                            addStatement(
                                "return %T(this.$valueName.toLong() + ${other.lowerCaseName}.$valueName)",
                                longClassName
                            )
                        } else {
                            addStatement(
                                "return %T(this.$valueName + ${other.lowerCaseName}.$valueName)",
                                className
                            )
                        }
                    }
                }
            }
        )
    }
}

fun buildMinusOperators(): List<FunSpec> {
    return DurationUnit.values().flatMap { other ->
        listOf(
            buildFunSpec("minus") {
                addModifiers(KModifier.OPERATOR)
                addParameter(other.lowerCaseName, other.intClassName)
                addStatement("return plus(-${other.lowerCaseName})")
            },
            buildFunSpec("minus") {
                addModifiers(KModifier.OPERATOR)
                addParameter(other.lowerCaseName, other.longClassName)
                addStatement("return plus(-${other.lowerCaseName})")
            }
        )
    }
}

fun DurationUnit.buildTimesOperators(
    className: ClassName,
    primitiveType: KClass<*>
): List<FunSpec> {
    return listOf(
        buildFunSpec("times") {
            addModifiers(KModifier.OPERATOR)
            addParameter("scalar", Int::class)

            if (forceLongInOperators && primitiveType != Long::class) {
                addStatement("return this.toLong() * scalar")
            } else {
                addStatement("return %T(this.$valueName * scalar)", className)
            }
        },
        buildFunSpec("times") {
            addModifiers(KModifier.OPERATOR)
            addParameter("scalar", Long::class)

            if (primitiveType != Long::class) {
                addStatement("return this.toLong() * scalar")
            } else {
                addStatement("return %T(this.$valueName * scalar)", className)
            }
        }
    )
}

fun DurationUnit.buildDivOperators(
    className: ClassName,
    primitiveType: KClass<*>
): List<FunSpec> {
    return listOf(
        buildFunSpec("div") {
            addModifiers(KModifier.OPERATOR)
            addParameter("scalar", Int::class)
            addStatement("return %T(this.$valueName / scalar)", className)
        },
        buildFunSpec("div") {
            addModifiers(KModifier.OPERATOR)
            addParameter("scalar", Long::class)

            if (primitiveType == Int::class) {
                addStatement("return this.toLong() / scalar")
            } else {
                addStatement("return %T(this.$valueName / scalar)", className)
            }
        }
    )
}

fun DurationUnit.buildRemOperators(
    className: ClassName,
    primitiveType: KClass<*>
): List<FunSpec> {
    return listOf(
        buildFunSpec("rem") {
            addModifiers(KModifier.OPERATOR)
            addParameter("scalar", Int::class)
            addStatement("return %T(this.$valueName %% scalar)", className)
        },
        buildFunSpec("rem") {
            addModifiers(KModifier.OPERATOR)
            addParameter("scalar", Long::class)

            if (primitiveType == Int::class) {
                addStatement("return this.toLong() %% scalar")
            } else {
                addStatement("return %T(this.$valueName %% scalar)", className)
            }
        }
    )
}

val DurationConstant.propertyClassName get() = ClassName(INTERNAL_PACKAGE_NAME, propertyName)

fun DurationUnit.classNameFor(primitiveType: KClass<*>): ClassName {
    return when (primitiveType) {
        Int::class -> intClassName
        Long::class -> longClassName
        else -> throw IllegalArgumentException("Unsupported class type")
    }
}

fun DurationUnit.operatorReturnClassNameFor(primitiveType: KClass<*>): ClassName {
    return when (primitiveType) {
        Int::class -> if (forceLongInOperators) longClassName else intClassName
        Long::class -> longClassName
        else -> throw IllegalArgumentException("Unsupported class type")
    }
}

fun DurationUnit.buildUnitConversionPropertiesAndFunctions(
    primitiveType: KClass<*>
): List<Any?> {
    return DurationUnit.values()
        .filter { it != this }
        .flatMap {
            val conversion = this.per(it)

            if (it < this) {
                listOf(
                    buildPropertySpec(
                        it.inWholeUnitPropertyName,
                        it.classNameFor(primitiveType)
                    ) {
                        getter(
                            buildGetterFunSpec {
                                val statement = buildString {
                                    append("return (this.$valueName / %T)")

                                    if (primitiveType == Int::class && !conversion.valueFitsInInt) {
                                        append(".toInt()")
                                    }

                                    append(".${it.lowerCaseName}")
                                }

                                addStatement(statement, conversion.propertyClassName)
                            }
                        )
                    }
                )
            } else {
                val className = it.operatorReturnClassNameFor(primitiveType)

                val overflowSafeMethodRequired = primitiveType != Int::class ||
                    !it.forceLongInOperators ||
                    conversion.isSafeMultiplicationRequiredForInt

                listOf(
                    if (overflowSafeMethodRequired) {
                        buildFunSpec(it.inUnitExactMethodName) {
                            addStatement(
                                if (primitiveType == Int::class && it.forceLongInOperators) {
                                    "return (this.$valueName.toLong() %T %T).${it.lowerCaseName}"
                                } else {
                                    "return (this.$valueName %T %T).${it.lowerCaseName}"
                                },
                                ClassName(INTERNAL_PACKAGE_NAME, "timesExact"),
                                conversion.propertyClassName
                            )
                        }
                    } else {
                        null
                    },
                    buildPropertySpec(it.inUnitPropertyName, className) {
                        getter(
                            buildGetterFunSpec {
                                if (primitiveType == Int::class && it.forceLongInOperators) {
                                    addStatement(
                                        "return (this.$valueName.toLong() * %T).${it.lowerCaseName}",
                                        conversion.propertyClassName
                                    )
                                } else {
                                    addStatement(
                                        "return (this.$valueName * %T).${it.lowerCaseName}",
                                        conversion.propertyClassName
                                    )
                                }
                            }
                        )
                    }
                )
            }
        }
}

fun DurationUnit.buildToComponentsFunctions(
    primitiveType: KClass<*>
): List<FunSpec> {
    return DurationUnit.values()
        .filter { it < this }
        .map { biggestUnit ->
            val allComponentUnits = DurationUnit.values().filter {
                it >= biggestUnit && it <= this
            }

            buildFunSpec("toComponents") {
                addTypeVariable(TypeVariableName("T"))
                addModifiers(KModifier.INLINE)
                returns(TypeVariableName("T"))

                val lambdaParameters = allComponentUnits.mapIndexed { index, unit ->
                    buildParameterSpec(
                        unit.lowerCaseName,
                        if (index == 0 && primitiveType == Long::class) unit.longClassName else unit.intClassName
                    )
                }

                addParameter(
                    "action",
                    LambdaTypeName.get(parameters = lambdaParameters, returnType = TypeVariableName("T"))
                )

                allComponentUnits.forEach { unit ->
                    val conversionComponents = listOf("this") +
                            allComponentUnits.filter { it < unit }.map { it.lowerCaseName }

                    var conversionString = conversionComponents.joinToString(" - ")

                    if (conversionComponents.count() > 1) {
                        conversionString = "($conversionString)"

                        if (forceLongInOperators || primitiveType == Long::class) {
                            conversionString = "$conversionString.toInt()"
                        }
                    }

                    if (unit != this@buildToComponentsFunctions) {
                        conversionString = "$conversionString.${unit.inWholeUnitPropertyName}"
                    }

                    addStatement("val ${unit.lowerCaseName} = $conversionString")
                }

                val allVariableNames = allComponentUnits.joinToString(", ") { it.lowerCaseName }
                addStatement("return action($allVariableNames)")
            }
        }
}

//fun DurationUnit.buildNonExpandedToComponentsFunctions(
//    primitiveType: KClass<*>
//): List<FunSpec> {
//    return DurationUnit.values()
//        .filter { it < this }
//        .map { biggestUnit ->
//            val allComponentUnits = DurationUnit.values().filter {
//                (it >= biggestUnit && it <= this && !it.isoPeriodIsFractional) || it == DurationUnit.NANOSECONDS
//            }
//
//            buildFunSpec("toComponents") {
//                addTypeVariable(TypeVariableName("T"))
//                addModifiers(KModifier.INLINE)
//                returns(TypeVariableName("T"))
//
//                val lambdaParameters = allComponentUnits.mapIndexed { index, unit ->
//                    buildParameterSpec(
//                        unit.lowerCaseName,
//                        if (index == 0 && primitiveType == Long::class) unit.longClassName else unit.intClassName
//                    )
//                }
//
//                addParameter(
//                    "action",
//                    LambdaTypeName.get(parameters = lambdaParameters, returnType = TypeVariableName("T"))
//                )
//
//                allComponentUnits.forEach { unit ->
//                    val conversionComponents = listOf("this") +
//                        allComponentUnits.filter { it < unit }.map { it.lowerCaseName }
//
//                    var conversionString = conversionComponents.joinToString(" - ")
//
//                    if (conversionComponents.count() > 1) {
//                        conversionString = "($conversionString)"
//
//                        if (forceLongInOperators || primitiveType == Long::class) {
//                            conversionString = "$conversionString.toInt()"
//                        }
//                    }
//
//                    if (unit != this@buildNonExpandedToComponentsFunctions) {
//                        conversionString = "$conversionString.${unit.inWholeUnitPropertyName}"
//                    }
//
//                    addStatement("val ${unit.lowerCaseName} = $conversionString")
//                }
//
//                val allVariableNames = allComponentUnits.joinToString(", ") { it.lowerCaseName }
//                addStatement("return action($allVariableNames)")
//            }
//        }
//}