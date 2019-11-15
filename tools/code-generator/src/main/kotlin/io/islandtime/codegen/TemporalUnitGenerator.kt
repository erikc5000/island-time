package io.islandtime.codegen

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import kotlin.reflect.KClass

val DurationUnit.intClassName get() = ClassName(MEASURES_PACKAGE_NAME, intName)
val DurationUnit.longClassName get() = ClassName(MEASURES_PACKAGE_NAME, longName)

fun generateDurationUnitFileSpecs(): List<FileSpec> {
    return TemporalUnitDescription.values().map { it.toFileSpec() }
}

fun TemporalUnitDescription.toFileSpec(): FileSpec {
    return buildFileSpec(
        MEASURES_PACKAGE_NAME,
        "_$pluralName"
    ) {
        addHeader("${pluralName}Kt")
        buildClasses().forEach { addType(it) }
        buildExtensionProperties().forEach { addProperty(it) }
    }
}

fun TemporalUnitDescription.buildClasses(): List<TypeSpec> {
    return listOf(
        buildClass(intClassName, Int::class),
        buildClass(longClassName, Long::class)
    )
}

fun TemporalUnitDescription.buildExtensionProperties(): List<PropertySpec> {
    return buildExtensionProperties(intClassName, Int::class) +
        buildExtensionProperties(longClassName, Long::class)
}

fun TemporalUnitDescription.buildClass(
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
                    var fractionalPartConversion = "absValue %% ${isoPeriodUnitConversion.constantValue}"

                    if (primitiveType == Long::class) {
                        fractionalPartConversion = "($fractionalPartConversion).toInt()"
                    }

                    addStatement(
                        """
                            |return if (this.isZero) {
                            |    "$isoPeriodZeroString"
                            |} else {
                            |    buildString {
                            |        val absValue = $valueName.%T
                            |        val wholePart = absValue / ${isoPeriodUnitConversion.constantValue}
                            |        val fractionalPart = $fractionalPartConversion
                            |        if (isNegative) { append('-') }
                            |        append("$isoPeriodPrefix")
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
                    val addStatementArgs = mutableListOf<Any>()
                    val convertedValueString = buildString {
                        append("$valueName.%T")
                        addStatementArgs += ClassName("kotlin.math", "absoluteValue")

                        if (isoPeriodUnitConversion.isNecessary()) {
                            append(" %T ${isoPeriodUnitConversion.constantValue}")
                            addStatementArgs += ClassName(INTERNAL_PACKAGE_NAME, "timesExact")
                        }
                    }

                    addStatement(
                        """
                            |return if (this.isZero) {
                            |    "$isoPeriodZeroString"
                            |} else {
                            |    buildString {
                            |        if (isNegative) { append('-') }
                            |        append("$isoPeriodPrefix")
                            |        append($convertedValueString)
                            |        append('$isoPeriodUnit')
                            |    }
                            |}
                        """.trimMargin(),
                        *addStatementArgs.toTypedArray()
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

fun TemporalUnitDescription.buildPrimitiveConversionFunctions(
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

fun TemporalUnitDescription.buildExtensionProperties(
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

fun TemporalUnitDescription.buildOperators(
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

fun TemporalUnitDescription.buildUnaryOperators(className: ClassName): List<FunSpec> {
    return listOf(
        buildFunSpec("unaryMinus") {
            addModifiers(KModifier.OPERATOR)
            addStatement("return %T(-$valueName)", className)
        }
    )
}

fun TemporalUnitDescription.buildPlusOperators(
    className: ClassName,
    primitiveType: KClass<*>
): List<FunSpec> {
    return TemporalUnitDescription.values()
        .filter { (this per it).isSupported() }
        .flatMap { other ->
            listOf(
                buildFunSpec("plus") {
                    addModifiers(KModifier.OPERATOR)
                    addParameter(other.lowerCaseName, other.intClassName)

                    when {
                        other.ordinal > ordinal -> {
                            if (forceLongInOperators && primitiveType == Int::class) {
                                addStatement("return this.toLong() + ${other.lowerCaseName}.$inUnitPropertyName")
                            } else {
                                addStatement("return this + ${other.lowerCaseName}.$inUnitPropertyName")
                            }
                        }
                        other.ordinal < ordinal -> {
                            if (forceLongInOperators && primitiveType == Int::class) {
                                addStatement("return this.toLong().${other.inUnitPropertyName} + ${other.lowerCaseName}.toLong()")
                            } else {
                                addStatement("return this.${other.inUnitPropertyName} + ${other.lowerCaseName}")
                            }
                        }
                        else -> {
                            if (forceLongInOperators && primitiveType == Int::class) {
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
                        other.ordinal > ordinal -> {
                            if (primitiveType == Int::class) {
                                addStatement("return this.toLong() + ${other.lowerCaseName}.$inUnitPropertyName")
                            } else {
                                addStatement("return this + ${other.lowerCaseName}.$inUnitPropertyName")
                            }
                        }
                        other.ordinal < ordinal -> {
                            if (primitiveType == Int::class) {
                                addStatement("return this.toLong().${other.inUnitPropertyName} + ${other.lowerCaseName}")
                            } else {
                                addStatement("return this.${other.inUnitPropertyName} + ${other.lowerCaseName}")
                            }
                        }
                        else -> {
                            if (primitiveType == Int::class) {
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

fun TemporalUnitDescription.buildMinusOperators(): List<FunSpec> {
    return TemporalUnitDescription.values()
        .filter { (this per it).isSupported() }
        .flatMap { other ->
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

fun TemporalUnitDescription.buildTimesOperators(
    className: ClassName,
    primitiveType: KClass<*>
): List<FunSpec> {
    return listOf(
        buildFunSpec("times") {
            addModifiers(KModifier.OPERATOR)
            addParameter("scalar", Int::class)

            if (forceLongInOperators && primitiveType == Int::class) {
                addStatement("return this.toLong() * scalar")
            } else {
                addStatement("return %T(this.$valueName * scalar)", className)
            }
        },
        buildFunSpec("times") {
            addModifiers(KModifier.OPERATOR)
            addParameter("scalar", Long::class)

            if (primitiveType == Int::class) {
                addStatement("return this.toLong() * scalar")
            } else {
                addStatement("return %T(this.$valueName * scalar)", className)
            }
        }
    )
}

fun TemporalUnitDescription.buildDivOperators(
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

fun TemporalUnitDescription.buildRemOperators(
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

val TemporalUnitConversion.propertyClassName get() = ClassName(INTERNAL_PACKAGE_NAME, constantName)

fun TemporalUnitDescription.operatorReturnClassNameFor(primitiveType: KClass<*>): ClassName {
    return when (primitiveType) {
        Int::class -> if (forceLongInOperators) longClassName else intClassName
        Long::class -> longClassName
        else -> throw IllegalArgumentException("Unsupported class type")
    }
}

fun TemporalUnitDescription.buildUnitConversionPropertiesAndFunctions(
    primitiveType: KClass<*>
): List<Any?> {
    return TemporalUnitDescription.values()
        .map { otherUnit -> this per otherUnit }
        .filter { conversion -> conversion.isSupportedAndNecessary() }
        .flatMap { conversion ->
            when (conversion.operator) {
                ConversionOperator.DIV -> listOf(
                    buildPropertySpec(
                        conversion.toUnit.inWholeUnitPropertyName,
                        conversion.toUnit.classNameFor(primitiveType)
                    ) {
                        getter(
                            buildGetterFunSpec {
                                val statement = buildString {
                                    append("return (this.$valueName / %T)")

                                    if (primitiveType == Int::class && !conversion.valueFitsInInt) {
                                        append(".toInt()")
                                    }

                                    append(".${conversion.toUnit.lowerCaseName}")
                                }

                                addStatement(statement, conversion.propertyClassName)
                            }
                        )
                    }
                )
                ConversionOperator.TIMES -> {
                    val className = conversion.toUnit.operatorReturnClassNameFor(primitiveType)

                    val overflowSafeMethodRequired = primitiveType == Long::class ||
                        !conversion.toUnit.forceLongInOperators ||
                        conversion.requiresSafeMultiplicationForInt()

                    listOf(
                        if (overflowSafeMethodRequired) {
                            buildFunSpec(conversion.toUnit.inUnitExactMethodName) {
                                addStatement(
                                    if (primitiveType == Int::class && conversion.toUnit.forceLongInOperators) {
                                        "return (this.$valueName.toLong() %T %T).${conversion.toUnit.lowerCaseName}"
                                    } else {
                                        "return (this.$valueName %T %T).${conversion.toUnit.lowerCaseName}"
                                    },
                                    ClassName(INTERNAL_PACKAGE_NAME, "timesExact"),
                                    conversion.propertyClassName
                                )
                            }
                        } else {
                            null
                        },
                        buildPropertySpec(conversion.toUnit.inUnitPropertyName, className) {
                            getter(
                                buildGetterFunSpec {
                                    if (primitiveType == Int::class && conversion.toUnit.forceLongInOperators) {
                                        addStatement(
                                            "return (this.$valueName.toLong() * %T).${conversion.toUnit.lowerCaseName}",
                                            conversion.propertyClassName
                                        )
                                    } else {
                                        addStatement(
                                            "return (this.$valueName * %T).${conversion.toUnit.lowerCaseName}",
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
}

fun TemporalUnitDescription.buildToComponentsFunctions(
    primitiveType: KClass<*>
): List<FunSpec> {
    return TemporalUnitDescription.values()
        .filter { it > this && (this per it).isSupported() }
        .map { biggestUnit ->
            val allComponentUnits = TemporalUnitDescription.values()
                .filter { it in this..biggestUnit }
                .sortedDescending()

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
                        allComponentUnits.filter { it > unit }.map { it.lowerCaseName }

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