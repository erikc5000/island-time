package io.islandtime.codegen

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.lang.IllegalStateException
import kotlin.reflect.KClass

private val INT_TYPE_NAME = Int::class.asTypeName()
private val LONG_TYPE_NAME = Long::class.asTypeName()

private val TypeName.zeroValueString
    get() = when (this) {
        INT_TYPE_NAME -> Int::class.zero
        LONG_TYPE_NAME -> Long::class.zero
        else -> throw IllegalStateException("Unsupported primitive type")
    }

private val TemporalUnitDescription.intClassName get() = ClassName(MEASURES_PACKAGE_NAME, intName)
private val TemporalUnitDescription.longClassName get() = ClassName(MEASURES_PACKAGE_NAME, longName)

private fun TemporalUnitDescription.classNameFor(primitiveType: KClass<*>): ClassName {
    return when (primitiveType) {
        Int::class -> intClassName
        Long::class -> longClassName
        else -> throw java.lang.IllegalArgumentException("Unsupported primitive type")
    }
}

fun generateTemporalUnitFileSpecs(): List<FileSpec> {
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
        TemporalUnitClassGenerator(this, Int::class),
        TemporalUnitClassGenerator(this, Long::class)
    ).map { it.build() }
}

fun TemporalUnitDescription.buildExtensionProperties(): List<PropertySpec> {
    return buildExtensionProperties(intClassName, Int::class) +
        buildExtensionProperties(longClassName, Long::class)
}

data class TemporalUnitClassGenerator(
    val description: TemporalUnitDescription,
    val primitive: KClass<*>
) {
    val className = description.classNameFor(primitive)
    val primitiveTypeName = primitive.asTypeName()

    val constructorFunSpec by lazy { buildConstructorFunSpec() }

    val valuePropertySpec by lazy { buildValuePropertySpec() }
    val absoluteValuePropertySpec by lazy { buildAbsoluteValuePropertySpec() }

    val isZeroFunSpec by lazy { buildIsZeroFunSpec() }
    val isNegativeFunSpec by lazy { buildIsNegativeFunSpec() }
    val isPositiveFunSpec by lazy { buildIsPositiveFunSpec() }
    val compareToFunSpec by lazy { buildCompareToFunSpec() }
    val toStringFunSpec by lazy { buildToStringFunSpec() }

    val companionObjectTypeSpec by lazy { buildCompanionObjectTypeSpec() }

    fun build() = buildClassTypeSpec(className) {
        addModifiers(KModifier.INLINE)
        addAnnotation(
            buildAnnotationSpec(Suppress::class) {
                addMember("%S", "NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
            }
        )
        primaryConstructor(constructorFunSpec)
        addSuperinterface(ClassName("kotlin", "Comparable").parameterizedBy(className))

        listOf(
            valuePropertySpec,
            absoluteValuePropertySpec,
            isZeroFunSpec,
            isNegativeFunSpec,
            isPositiveFunSpec,
            compareToFunSpec,
            toStringFunSpec,
            companionObjectTypeSpec
        ).forEach {
            when (it) {
                is PropertySpec -> addProperty(it)
                is FunSpec -> addFunction(it)
                is TypeSpec -> addType(it)
            }
        }

        // TODO: Refactor the rest of this to make it more maintainable
        (description.buildUnitConversionPropertiesAndFunctions(primitive)
        + description.buildOperators(className, primitive) +
            description.buildToComponentsFunctions(primitive) +
            description.buildPrimitiveConversionFunctions(primitive)).forEach {
            when (it) {
                is FunSpec -> addFunction(it)
                is PropertySpec -> addProperty(it)
            }
        }
    }
}

fun TemporalUnitClassGenerator.buildValuePropertySpec() = buildPropertySpec(
    description.valueName,
    primitiveTypeName
) {
    initializer(description.valueName)
}

fun TemporalUnitClassGenerator.buildAbsoluteValuePropertySpec() = buildPropertySpec(
    "absoluteValue",
    className
) {
    getter(
        buildGetterFunSpec {
            val arguments = mapOf(
                "className" to className,
                "value" to valuePropertySpec,
                "absoluteValue" to ClassName("kotlin.math", "absoluteValue")
            )

            addNamedCode(
                "return %className:T(%value:N.%absoluteValue:T)",
                arguments
            )
        }
    )
}

fun TemporalUnitClassGenerator.buildConstructorFunSpec() = buildConstructorFunSpec {
    // addModifiers(KModifier.INTERNAL)
    addParameter(valuePropertySpec.name, valuePropertySpec.type)
}

fun TemporalUnitClassGenerator.buildIsZeroFunSpec() = buildFunSpec("isZero") {
    returns(Boolean::class)
    addStatement("return %N == ${valuePropertySpec.type.zeroValueString}", valuePropertySpec)
}

fun TemporalUnitClassGenerator.buildIsNegativeFunSpec() = buildFunSpec("isNegative") {
    returns(Boolean::class)
    addStatement("return %N < ${valuePropertySpec.type.zeroValueString}", valuePropertySpec)
}

fun TemporalUnitClassGenerator.buildIsPositiveFunSpec() = buildFunSpec("isPositive") {
    returns(Boolean::class)
    addStatement("return %N > ${valuePropertySpec.type.zeroValueString}", valuePropertySpec)
}

fun TemporalUnitClassGenerator.buildCompareToFunSpec() = buildFunSpec("compareTo") {
    addModifiers(KModifier.OVERRIDE)
    addParameter("other", className)
    returns(Int::class)
    addStatement("return %N.compareTo(other.%N)", valuePropertySpec, valuePropertySpec)
}

fun TemporalUnitClassGenerator.buildToStringFunSpec() = buildFunSpec("toString") {
    addModifiers(KModifier.OVERRIDE)
    returns(String::class)

    addCode(
        if (description.isoPeriodIsFractional) {
            buildFractionalToStringCodeBlock()
        } else {
            buildWholeToStringCodeBlock()
        }
    )
}

fun TemporalUnitClassGenerator.buildFractionalToStringCodeBlock() = buildCodeBlock {
    var fractionalPartConversion = "absValue %% ${description.isoPeriodUnitConversion.constantValue}"

    if (primitive == Long::class) {
        fractionalPartConversion = "($fractionalPartConversion).toInt()"
    }

    val arguments = mapOf(
        "isZero" to isZeroFunSpec,
        "value" to valuePropertySpec,
        "absoluteValue" to ClassName("kotlin.math", "absoluteValue"),
        "isNegative" to isNegativeFunSpec,
        "toZeroPaddedString" to ClassName(INTERNAL_PACKAGE_NAME, "toZeroPaddedString")
    )

    addNamed(
        """
            |return if (%isZero:N()) {
            |    "$description.isoPeriodZeroString"
            |} else {
            |    buildString {
            |        val absValue = %value:N.%absoluteValue:T
            |        val wholePart = absValue / ${description.isoPeriodUnitConversion.constantValue}
            |        val fractionalPart = $fractionalPartConversion
            |        if (%isNegative:N()) { append('-') }
            |        append("${description.isoPeriodPrefix}")
            |        append(wholePart)
            |        if (fractionalPart != 0) {
            |            append('.')
            |            append(fractionalPart.%toZeroPaddedString:T(${description.isoPeriodDecimalPlaces}).dropLastWhile { it == '0' })
            |        }
            |        append('${description.isoPeriodUnit}')
            |    }
            |}
        """.trimMargin(),
        arguments
    )
}

fun TemporalUnitClassGenerator.buildWholeToStringCodeBlock() = buildCodeBlock {
    val arguments = mapOf(
        "isZero" to isZeroFunSpec,
        "value" to valuePropertySpec,
        "absoluteValue" to ClassName("kotlin.math", "absoluteValue"),
        "isNegative" to isNegativeFunSpec,
        "timesExact" to ClassName(INTERNAL_PACKAGE_NAME, "timesExact")
    )

    val convertedValueString = buildString {
        append("%value:N.%absoluteValue:T")

        if (description.isoPeriodUnitConversion.isNecessary()) {
            append(" %timesExact:T ${description.isoPeriodUnitConversion.constantValue}")
        }
    }

    addNamed(
        """
            |return if (%isZero:N()) {
            |    "${description.isoPeriodZeroString}"
            |} else {
            |    buildString {
            |        if (%isNegative:N()) { append('-') }
            |        append("${description.isoPeriodPrefix}")
            |        append($convertedValueString)
            |        append('${description.isoPeriodUnit}')
            |    }
            |}
        """.trimMargin(),
        arguments
    )
}

fun TemporalUnitClassGenerator.buildCompanionObjectTypeSpec() = buildCompanionObjectTypeSpec {
    listOf(
        buildPropertySpec("MIN", className) {
            initializer("%T(%T.MIN_VALUE)", className, primitive)
        },
        buildPropertySpec("MAX", className) {
            initializer("%T(%T.MAX_VALUE)", className, primitive)
        }
    ).forEach { addProperty(it) }
}

// TODO: Refactor everything below
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