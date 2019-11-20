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

        listOf(
            IntTemporalUnitClassGenerator(this@toFileSpec),
            LongTemporalUnitClassGenerator(this@toFileSpec)
        )
            .flatMap { it.build() }
            .forEach {
                when (it) {
                    is TypeSpec -> addType(it)
                    is PropertySpec -> addProperty(it)
                }
            }
    }
}

abstract class TemporalUnitClassGenerator(
    val description: TemporalUnitDescription,
    val primitive: KClass<*>
) {
    val className = description.classNameFor(primitive)
    val primitiveTypeName = primitive.asTypeName()

    val constructorFunSpec by lazy(::buildConstructorFunSpec)

    val valuePropertySpec by lazy(::buildValuePropertySpec)
    val absoluteValuePropertySpec by lazy(::buildAbsoluteValuePropertySpec)

    val isZeroFunSpec by lazy(::buildIsZeroFunSpec)
    val isNegativeFunSpec by lazy(::buildIsNegativeFunSpec)
    val isPositiveFunSpec by lazy(::buildIsPositiveFunSpec)
    val compareToFunSpec by lazy(::buildCompareToFunSpec)
    val toStringFunSpec by lazy(::buildToStringFunSpec)

    val unaryMinusFunSpec by lazy(::buildUnaryMinusFunSpec)
    val timesIntFunSpec by lazy { buildTimesFunSpec(Int::class) }
    val timesLongFunSpec by lazy { buildTimesFunSpec(Long::class) }
    val divIntFunSpec by lazy { buildDivFunSpec(Int::class) }
    val divLongFunSpec by lazy { buildDivFunSpec(Long::class) }
    val remIntFunSpec by lazy { buildRemFunSpec(Int::class) }
    val remLongFunSpec by lazy { buildRemFunSpec(Long::class) }

    val companionObjectTypeSpec by lazy(::buildCompanionObjectTypeSpec)

    val primitiveExtensionPropertySpec by lazy(::buildPrimitiveExtensionPropertySpec)

    private val baseClassSpecList
        get() = listOf(
            valuePropertySpec,
            absoluteValuePropertySpec,
            isZeroFunSpec,
            isNegativeFunSpec,
            isPositiveFunSpec,
            compareToFunSpec,
            toStringFunSpec,
            unaryMinusFunSpec,
            timesIntFunSpec,
            timesLongFunSpec,
            divIntFunSpec,
            divLongFunSpec,
            remIntFunSpec,
            remLongFunSpec,
            companionObjectTypeSpec
        ) +
            buildPlusMinusOperatorFunSpecs() +
            buildUnitConversionSpecs() +
            buildToComponentsFunctions()

    protected open fun allClassSpecs(): List<Any> {
        return baseClassSpecList
    }

    fun build() = listOf(
        buildClass(),
        primitiveExtensionPropertySpec
    )

    fun buildClass() = buildClassTypeSpec(className) {
        addKdoc("A number of ${description.lowerCaseName}.")
        addModifiers(KModifier.INLINE)
        addAnnotation(
            buildAnnotationSpec(Suppress::class) {
                addMember("%S", "NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
            }
        )
        primaryConstructor(constructorFunSpec)
        addSuperinterface(ClassName("kotlin", "Comparable").parameterizedBy(className))

        allClassSpecs().forEach {
            when (it) {
                is PropertySpec -> addProperty(it)
                is FunSpec -> addFunction(it)
                is TypeSpec -> addType(it)
            }
        }
    }
}

class IntTemporalUnitClassGenerator(
    description: TemporalUnitDescription
) : TemporalUnitClassGenerator(description, Int::class) {

    val toLongFunSpec by lazy(::buildToLongFunSpec)

    override fun allClassSpecs(): List<Any> {
        return super.allClassSpecs() + listOf(
            toLongFunSpec
        )
    }
}

class LongTemporalUnitClassGenerator(
    description: TemporalUnitDescription
) : TemporalUnitClassGenerator(description, Long::class) {

    val toIntFunSpec by lazy(::buildToIntFunSpec)
    val toIntExactFunSpec by lazy(::buildToIntExactFunSpec)

    override fun allClassSpecs(): List<Any> {
        return super.allClassSpecs() + listOf(
            toIntFunSpec,
            toIntExactFunSpec
        )
    }
}

fun TemporalUnitClassGenerator.buildValuePropertySpec() = buildPropertySpec(
    description.valueName,
    primitiveTypeName
) {
    addKdoc("The underlying value.")
    initializer(description.valueName)
}

fun TemporalUnitClassGenerator.buildAbsoluteValuePropertySpec() = buildPropertySpec(
    "absoluteValue",
    className
) {
    addKdoc("Get the absolute value.")
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
    addKdoc("Is this duration zero?")
    returns(Boolean::class)
    addStatement("return %N == ${valuePropertySpec.type.zeroValueString}", valuePropertySpec)
}

fun TemporalUnitClassGenerator.buildIsNegativeFunSpec() = buildFunSpec("isNegative") {
    addKdoc("Is this duration negative?")
    returns(Boolean::class)
    addStatement("return %N < ${valuePropertySpec.type.zeroValueString}", valuePropertySpec)
}

fun TemporalUnitClassGenerator.buildIsPositiveFunSpec() = buildFunSpec("isPositive") {
    addKdoc("Is this duration positive?")
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
    addKdoc("Convert to an ISO-8601 time interval representation.")
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
    property("MIN", className) {
        addKdoc("The smallest supported value.")
        initializer("%T(%T.MIN_VALUE)", className, primitive)
    }
    property("MAX", className) {
        addKdoc("The largest supported value.")
        initializer("%T(%T.MAX_VALUE)", className, primitive)
    }
}

fun IntTemporalUnitClassGenerator.buildToLongFunSpec() = buildFunSpec("toLong") {
    addStatement("return %T(%N.toLong())", description.longClassName, valuePropertySpec)
}

fun LongTemporalUnitClassGenerator.buildToIntFunSpec() = buildFunSpec("toInt") {
    addStatement("return %T(%N.toInt())", description.intClassName, valuePropertySpec)
}

fun LongTemporalUnitClassGenerator.buildToIntExactFunSpec() = buildFunSpec("toIntExact") {
    addStatement(
        "return %T(%N.%T())",
        description.intClassName,
        valuePropertySpec,
        ClassName(INTERNAL_PACKAGE_NAME, "toIntExact")
    )
}

fun TemporalUnitClassGenerator.buildUnaryMinusFunSpec() = buildFunSpec("unaryMinus") {
    addModifiers(KModifier.OPERATOR)
    addStatement("return %T(-%N)", className, valuePropertySpec)
}

enum class PlusOrMinusOperator(val functionName: String, val operator: String) {
    PLUS("plus", "+"),
    MINUS("minus", "-")
}

fun TemporalUnitClassGenerator.buildPlusMinusOperatorFunSpecs() = TemporalUnitDescription.values()
    .map { description per it }
    .filter { it.isSupported() }
    .flatMap { conversion ->
        listOf(
            buildIntPlusMinusOperatorFunSpec(conversion, PlusOrMinusOperator.PLUS),
            buildIntPlusMinusOperatorFunSpec(conversion, PlusOrMinusOperator.MINUS),
            buildLongPlusMinusOperatorFunSpec(conversion, PlusOrMinusOperator.PLUS),
            buildLongPlusMinusOperatorFunSpec(conversion, PlusOrMinusOperator.MINUS)
        )
    }

fun TemporalUnitClassGenerator.buildIntPlusMinusOperatorFunSpec(
    conversion: TemporalUnitConversion,
    plusOrMinusOperator: PlusOrMinusOperator
) = buildFunSpec(plusOrMinusOperator.functionName) {
    addModifiers(KModifier.OPERATOR)

    val plusOrMinus = plusOrMinusOperator.operator
    val amount = ParameterSpec(conversion.toUnit.lowerCaseName, conversion.toUnit.intClassName)
    addParameter(amount)

    when (conversion.operator) {
        ConversionOperator.DIV -> {
            if (this@buildIntPlusMinusOperatorFunSpec is IntTemporalUnitClassGenerator &&
                description.forceLongInOperators
            ) {
                addStatement(
                    "return this.%N() $plusOrMinus %N.${description.inUnitPropertyName}",
                    toLongFunSpec,
                    amount
                )
            } else {
                addStatement("return this $plusOrMinus %N.${description.inUnitPropertyName}", amount)
            }
        }
        ConversionOperator.TIMES -> {
            if (this@buildIntPlusMinusOperatorFunSpec is IntTemporalUnitClassGenerator &&
                description.forceLongInOperators
            ) {
                addStatement(
                    "return this.%N().${conversion.toUnit.inUnitPropertyName} $plusOrMinus %N.toLong()",
                    toLongFunSpec,
                    amount
                )
            } else {
                addStatement("return this.${conversion.toUnit.inUnitPropertyName} $plusOrMinus %N", amount)
            }
        }
        ConversionOperator.NONE -> {
            if (primitive == Int::class && description.forceLongInOperators) {
                addStatement(
                    "return %T(%N.toLong() $plusOrMinus %N.${description.valueName})",
                    description.longClassName,
                    valuePropertySpec,
                    amount
                )
            } else {
                addStatement(
                    "return %T(%N $plusOrMinus %N.${description.valueName})",
                    className,
                    valuePropertySpec,
                    amount
                )
            }
        }
    }
}

fun TemporalUnitClassGenerator.buildLongPlusMinusOperatorFunSpec(
    conversion: TemporalUnitConversion,
    plusOrMinusOperator: PlusOrMinusOperator
) = buildFunSpec(plusOrMinusOperator.functionName) {
    addModifiers(KModifier.OPERATOR)

    val plusOrMinus = plusOrMinusOperator.operator
    val amount = ParameterSpec(conversion.toUnit.lowerCaseName, conversion.toUnit.longClassName)
    addParameter(amount)

    when (conversion.operator) {
        ConversionOperator.DIV -> {
            if (this@buildLongPlusMinusOperatorFunSpec is IntTemporalUnitClassGenerator) {
                addStatement(
                    "return this.%N() $plusOrMinus %N.${description.inUnitPropertyName}",
                    toLongFunSpec,
                    amount
                )
            } else {
                addStatement("return this $plusOrMinus %N.${description.inUnitPropertyName}", amount)
            }
        }
        ConversionOperator.TIMES -> {
            if (this@buildLongPlusMinusOperatorFunSpec is IntTemporalUnitClassGenerator) {
                addStatement(
                    "return this.%N().${conversion.toUnit.inUnitPropertyName} $plusOrMinus %N",
                    toLongFunSpec,
                    amount
                )
            } else {
                addStatement("return this.${conversion.toUnit.inUnitPropertyName} $plusOrMinus %N", amount)
            }
        }
        ConversionOperator.NONE -> {
            if (primitive == Int::class) {
                addStatement(
                    "return %T(%N.toLong() $plusOrMinus %N.${description.valueName})",
                    description.longClassName,
                    valuePropertySpec,
                    amount
                )
            } else {
                addStatement(
                    "return %T(%N $plusOrMinus %N.${description.valueName})",
                    className,
                    valuePropertySpec,
                    amount
                )
            }
        }
    }
}

fun TemporalUnitClassGenerator.buildTimesFunSpec(
    scalarPrimitive: KClass<*>
) = buildFunSpec("times") {
    addModifiers(KModifier.OPERATOR)

    val scalar = ParameterSpec("scalar", scalarPrimitive.asTypeName())
    addParameter(scalar)

    if (this@buildTimesFunSpec is IntTemporalUnitClassGenerator &&
        (scalarPrimitive == Long::class || description.forceLongInOperators)
    ) {
        addStatement("return this.%N() * %N", toLongFunSpec, scalar)
    } else {
        addStatement("return %T(%N * %N)", className, valuePropertySpec, scalar)
    }
}

fun TemporalUnitClassGenerator.buildDivFunSpec(
    scalarPrimitive: KClass<*>
) = buildFunSpec("div") {
    addModifiers(KModifier.OPERATOR)

    val scalar = ParameterSpec("scalar", scalarPrimitive.asTypeName())
    addParameter(scalar)

    if (this@buildDivFunSpec is IntTemporalUnitClassGenerator && scalarPrimitive == Long::class) {
        addStatement("return this.%N() / %N", toLongFunSpec, scalar)
    } else {
        addStatement("return %T(%N / %N)", className, valuePropertySpec, scalar)
    }
}

fun TemporalUnitClassGenerator.buildRemFunSpec(
    scalarPrimitive: KClass<*>
) = buildFunSpec("rem") {
    addModifiers(KModifier.OPERATOR)

    val scalar = ParameterSpec("scalar", scalarPrimitive.asTypeName())
    addParameter(scalar)

    if (this@buildRemFunSpec is IntTemporalUnitClassGenerator && scalarPrimitive == Long::class) {
        addStatement("return this.%N() %% %N", toLongFunSpec, scalar)
    } else {
        addStatement("return %T(%N %% %N)", className, valuePropertySpec, scalar)
    }
}

val TemporalUnitConversion.propertyClassName get() = ClassName(INTERNAL_PACKAGE_NAME, constantName)

fun TemporalUnitDescription.operatorReturnClassNameFor(primitiveType: KClass<*>): ClassName {
    return when (primitiveType) {
        Int::class -> if (forceLongInOperators) longClassName else intClassName
        Long::class -> longClassName
        else -> throw IllegalArgumentException("Unsupported class type")
    }
}

fun TemporalUnitClassGenerator.buildUnitConversionSpecs() = TemporalUnitDescription.values()
    .map { otherUnit -> description per otherUnit }
    .filter { conversion -> conversion.isSupportedAndNecessary() }
    .flatMap { conversion ->
        when (conversion.operator) {
            ConversionOperator.DIV -> buildInBiggerUnitConversionSpecs(conversion)
            ConversionOperator.TIMES -> buildInSmallerUnitConversionSpecs(conversion)
            else -> throw IllegalStateException("Invalid operator")
        }
    }

fun TemporalUnitClassGenerator.buildInBiggerUnitConversionSpecs(
    conversion: TemporalUnitConversion
) = listOf(
    buildPropertySpec(
        conversion.toUnit.inWholeUnitPropertyName,
        conversion.toUnit.classNameFor(primitive)
    ) {
        addKdoc("Convert to whole ${conversion.toUnit.lowerCaseName}.")
        getter(
            buildGetterFunSpec {
                val statement = buildString {
                    append("return (%N / %T)")

                    if (primitive == Int::class && !conversion.valueFitsInInt) {
                        append(".toInt()")
                    }

                    append(".${conversion.toUnit.lowerCaseName}")
                }

                addStatement(statement, valuePropertySpec, conversion.propertyClassName)
            }
        )
    }
)

fun TemporalUnitClassGenerator.buildInSmallerUnitConversionSpecs(
    conversion: TemporalUnitConversion
): List<Any> {
    val operators = mutableListOf<Any>()

    val overflowSafeMethodRequired = primitive == Long::class ||
        !conversion.toUnit.forceLongInOperators ||
        conversion.requiresSafeMultiplicationForInt()

    if (overflowSafeMethodRequired) {
        operators += buildFunSpec(conversion.toUnit.inUnitExactMethodName) {
            addKdoc("Convert to ${conversion.toUnit.lowerCaseName}.")
            addStatement(
                if (primitive == Int::class && conversion.toUnit.forceLongInOperators) {
                    "return (%N.toLong() %T %T).${conversion.toUnit.lowerCaseName}"
                } else {
                    "return (%N %T %T).${conversion.toUnit.lowerCaseName}"
                },
                valuePropertySpec,
                ClassName(INTERNAL_PACKAGE_NAME, "timesExact"),
                conversion.propertyClassName
            )
        }
    }

    operators += buildPropertySpec(
        conversion.toUnit.inUnitPropertyName,
        conversion.toUnit.operatorReturnClassNameFor(primitive)
    ) {
        addKdoc("Convert to ${conversion.toUnit.lowerCaseName}.")
        getter(
            buildGetterFunSpec {
                if (primitive == Int::class && conversion.toUnit.forceLongInOperators) {
                    addStatement(
                        "return (%N.toLong() * %T).${conversion.toUnit.lowerCaseName}",
                        valuePropertySpec,
                        conversion.propertyClassName
                    )
                } else {
                    addStatement(
                        "return (%N * %T).${conversion.toUnit.lowerCaseName}",
                        valuePropertySpec,
                        conversion.propertyClassName
                    )
                }
            }
        )
    }

    return operators
}

fun TemporalUnitClassGenerator.buildPrimitiveExtensionPropertySpec() = buildPropertySpec(
    description.lowerCaseName,
    className
) {
    addKdoc("Convert to [%T].", className)
    receiver(primitive)
    getter(buildGetterFunSpec {
        addStatement("return %T(this)", className)
    })
}

fun TemporalUnitClassGenerator.buildToComponentsFunctions(): List<FunSpec> {
    return TemporalUnitDescription.values()
        .filter { it > this.description && (this.description per it).isSupported() }
        .map { biggestUnit ->
            val allComponentUnits = TemporalUnitDescription.values()
                .filter { it in description..biggestUnit }
                .sortedDescending()

            buildFunSpec("toComponents") {
                addTypeVariable(TypeVariableName("T"))
                addModifiers(KModifier.INLINE)
                returns(TypeVariableName("T"))

                val lambdaParameters = allComponentUnits.mapIndexed { index, unit ->
                    buildParameterSpec(
                        unit.lowerCaseName,
                        if (index == 0 && primitive == Long::class) unit.longClassName else unit.intClassName
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

                        if (description.forceLongInOperators || primitive == Long::class) {
                            conversionString = "$conversionString.toInt()"
                        }
                    }

                    if (unit != this@buildToComponentsFunctions.description) {
                        conversionString = "$conversionString.${unit.inWholeUnitPropertyName}"
                    }

                    addStatement("val ${unit.lowerCaseName} = $conversionString")
                }

                val allVariableNames = allComponentUnits.joinToString(", ") { it.lowerCaseName }
                addStatement("return action($allVariableNames)")
            }
        }
}