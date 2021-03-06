package io.islandtime.codegen.generators

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.islandtime.codegen.Generator
import io.islandtime.codegen.descriptions.ConversionOperator
import io.islandtime.codegen.descriptions.TemporalUnitConversion
import io.islandtime.codegen.descriptions.TemporalUnitDescription
import io.islandtime.codegen.descriptions.per
import io.islandtime.codegen.dsl.*
import io.islandtime.codegen.internal
import io.islandtime.codegen.javamath2kmp
import io.islandtime.codegen.measures
import io.islandtime.codegen.util.zero
import java.util.*
import kotlin.reflect.KClass

private val KOTLIN_DURATION_CLASS_NAME = ClassName("kotlin.time", "Duration")
private val EXPERIMENTAL_TIME_CLASS_NAME = ClassName("kotlin.time", "ExperimentalTime")
private val INT_TYPE_NAME = Int::class.asTypeName()
private val LONG_TYPE_NAME = Long::class.asTypeName()

private val TypeName.zeroValueString
    get() = when (this) {
        INT_TYPE_NAME -> Int::class.zero
        LONG_TYPE_NAME -> Long::class.zero
        else -> throw IllegalStateException("Unsupported primitive type")
    }

private fun KClass<*>.literalValueString(value: Long) = when (this) {
    Int::class -> "$value"
    Long::class -> "${value}L"
    else -> throw IllegalStateException("Unsupported primitive type")
}

private val TemporalUnitDescription.intClassName get() = measures(intName)
private val TemporalUnitDescription.longClassName get() = measures(longName)

private val TemporalUnitDescription.nextBiggest get() = TemporalUnitDescription.values()[this.ordinal + 1]

private fun TemporalUnitDescription.classNameFor(primitiveType: KClass<*>): ClassName {
    return when (primitiveType) {
        Int::class -> intClassName
        Long::class -> longClassName
        else -> throw java.lang.IllegalArgumentException("Unsupported primitive type")
    }
}

object TemporalUnitGenerator : Generator {
    override fun generate(): List<FileSpec> {
        return TemporalUnitDescription.values().map { it.toFileSpec() }
    }
}

fun TemporalUnitDescription.toFileSpec(): FileSpec {
    return io.islandtime.codegen.dsl.buildFileSpec(
        "io.islandtime.measures",
        "_$pluralName"
    ) {
        addHeader("${pluralName}Kt")

        if (isTimeBased) {
            addAliasedImport(KOTLIN_DURATION_CLASS_NAME, "KotlinDuration")
            addAliasedImport(ClassName("kotlin.time", lowerPluralName), "kotlin$pluralName")
        }

        listOf(
            IntTemporalUnitClassGenerator(this@toFileSpec),
            LongTemporalUnitClassGenerator(this@toFileSpec)
        )
            .flatMap { it.build() }
            .forEach {
                when (it) {
                    is TypeSpec -> addType(it)
                    is PropertySpec -> addProperty(it)
                    is FunSpec -> addFunction(it)
                    else -> throw IllegalStateException("Invalid type '${it.javaClass.simpleName}' encountered!")
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
    val toKotlinDurationFunSpec by lazy(::buildToKotlinDurationFunSpec)

    val unaryMinusFunSpec by lazy(::buildUnaryMinusFunSpec)
    val negateUncheckedFunSpec by lazy(::buildNegateUncheckedFunSpec)
    val timesIntFunSpec by lazy { buildTimesFunSpec(Int::class) }
    val timesLongFunSpec by lazy { buildTimesFunSpec(Long::class) }
    val divIntFunSpec by lazy { buildDivFunSpec(Int::class) }
    val divLongFunSpec by lazy { buildDivFunSpec(Long::class) }
    val remIntFunSpec by lazy { buildRemFunSpec(Int::class) }
    val remLongFunSpec by lazy { buildRemFunSpec(Long::class) }

    val companionObjectTypeSpec by lazy(::buildCompanionObjectTypeSpec)

    val primitiveExtensionPropertySpec by lazy(::buildPrimitiveExtensionPropertySpec)
    val timesIntExtensionFunSpec by lazy { buildTimesExtensionFunSpec(Int::class) }
    val timesLongExtensionFunSpec by lazy { buildTimesExtensionFunSpec(Long::class) }

    private val baseClassSpecList: List<Any>
        get() {
            val list = listOf(
                valuePropertySpec,
                absoluteValuePropertySpec,
                isZeroFunSpec,
                isNegativeFunSpec,
                isPositiveFunSpec,
                compareToFunSpec,
                toStringFunSpec,
                unaryMinusFunSpec,
                negateUncheckedFunSpec,
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

            return if (description.isTimeBased) {
                list + toKotlinDurationFunSpec
            } else {
                list
            }
        }

    protected open fun allClassSpecs(): List<Any> {
        return baseClassSpecList
    }

    protected open fun allExtensionSpecs(): List<Any> {
        return listOf(
            primitiveExtensionPropertySpec,
            timesIntExtensionFunSpec,
            timesLongExtensionFunSpec
        )
    }

    fun build() = listOf(buildClass()) + allExtensionSpecs()

    fun buildClass() = io.islandtime.codegen.dsl.buildClassTypeSpec(className) {
        addKdoc("A number of ${description.lowerPluralName}.")
        addModifiers(KModifier.INLINE)
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

    val toLongUnitFunSpec by lazy(::buildToLongUnitFunSpec)
    val toLongFunSpec by lazy(::buildToLongFunSpec)

    override fun allClassSpecs(): List<Any> {
        return super.allClassSpecs() + listOf(
            toLongUnitFunSpec,
            toLongFunSpec
        )
    }
}

class LongTemporalUnitClassGenerator(
    description: TemporalUnitDescription
) : TemporalUnitClassGenerator(description, Long::class) {

    val toIntUnitFunSpec by lazy(::buildToIntUnitFunSpec)
    val toIntUnitUncheckedFunSpec by lazy(::buildToIntUnitUncheckedFunSpec)
    val toIntFunSpec by lazy(::buildToIntFunSpec)
    val toIntUncheckedFunSpec by lazy(::buildToIntUncheckedFunSpec)

    val kotlinDurationExtensionFunSpec by lazy(::buildKotlinDurationExtensionFunSpec)

    override fun allClassSpecs(): List<Any> {
        return super.allClassSpecs() + listOf(
            toIntUnitFunSpec,
            toIntUnitUncheckedFunSpec,
            toIntFunSpec,
            toIntUncheckedFunSpec
        )
    }

    override fun allExtensionSpecs(): List<Any> {
        return if (description.isTimeBased) {
            super.allExtensionSpecs() + listOf(kotlinDurationExtensionFunSpec)
        } else {
            super.allExtensionSpecs()
        }
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
    addKdoc(
        """
            The absolute value of this duration.
            @throws ArithmeticException if overflow occurs
        """.trimIndent()
    )
    getter(
        buildGetterFunSpec {
            val arguments = mapOf("value" to valuePropertySpec)

            addNamedCode(
                "return if (%value:N < 0) -this else this",
                arguments
            )
        }
    )
}

fun TemporalUnitClassGenerator.buildConstructorFunSpec() = buildConstructorFunSpec {
    addParameter(valuePropertySpec.name, valuePropertySpec.type)
}

fun TemporalUnitClassGenerator.buildIsZeroFunSpec() = buildFunSpec("isZero") {
    addKdoc("Checks if this duration is zero.")
    returns(Boolean::class)
    addStatement("return %N == ${valuePropertySpec.type.zeroValueString}", valuePropertySpec)
}

fun TemporalUnitClassGenerator.buildIsNegativeFunSpec() = buildFunSpec("isNegative") {
    addKdoc("Checks if this duration is negative.")
    returns(Boolean::class)
    addStatement("return %N < ${valuePropertySpec.type.zeroValueString}", valuePropertySpec)
}

fun TemporalUnitClassGenerator.buildIsPositiveFunSpec() = buildFunSpec("isPositive") {
    addKdoc("Checks if this duration is positive.")
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
    addKdoc("Converts this duration to an ISO-8601 time interval representation.")
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

fun TemporalUnitClassGenerator.buildToKotlinDurationFunSpec() =
    buildFunSpec("toKotlinDuration") {
        addAnnotation(EXPERIMENTAL_TIME_CLASS_NAME)
        addKdoc("Converts this duration to a [${KOTLIN_DURATION_CLASS_NAME.canonicalName}].")
        returns(KOTLIN_DURATION_CLASS_NAME)
        addStatement("return %N.%T", valuePropertySpec, ClassName("kotlin.time", description.lowerPluralName))
    }

fun TemporalUnitClassGenerator.buildFractionalToStringCodeBlock() = io.islandtime.codegen.dsl.buildCodeBlock {
    var fractionalPartConversion = "%value:N %% ${description.isoPeriodUnitConversion.constantValue}"

    if (primitive == Long::class) {
        fractionalPartConversion = "($fractionalPartConversion).toInt()"
    }

    val arguments = mapOf(
        "value" to valuePropertySpec,
        "absoluteValue" to ClassName("kotlin.math", "absoluteValue"),
        "toZeroPaddedString" to internal("toZeroPaddedString")
    )

    addNamed(
        """
            | return if (%value:N == ${primitive.zero}) {
            |   "${description.isoPeriodZeroString}"
            | } else {
            |   buildString {
            |     val wholePart = (%value:N / ${description.isoPeriodUnitConversion.constantValue}).%absoluteValue:T
            |     val fractionalPart = (${fractionalPartConversion}).%absoluteValue:T
            |     if (%value:N < 0) { append('-') }
            |     append("${description.isoPeriodPrefix}")
            |     append(wholePart)
            |     if (fractionalPart > 0) {
            |       append('.')
            |       append(fractionalPart.%toZeroPaddedString:T(${description.isoPeriodDecimalPlaces}).dropLastWhile { it == '0' })
            |     }
            |     append('${description.isoPeriodUnit}')
            |   }
            | }
        """.trimMargin(),
        arguments
    )
}

fun TemporalUnitClassGenerator.buildWholeToStringCodeBlock() = io.islandtime.codegen.dsl.buildCodeBlock {
    val arguments = mapOf(
        "value" to valuePropertySpec,
        "absoluteValue" to ClassName("kotlin.math", "absoluteValue"),
        "timesExact" to javamath2kmp("timesExact"),
        "minValue" to "${primitiveTypeName.simpleName}.MIN_VALUE"
    )

    val convertedValueString = buildString {
        append("%value:N.%absoluteValue:T")

        if (description.isoPeriodUnitConversion.isNecessary()) {
            append(" %timesExact:T ${description.isoPeriodUnitConversion.constantValue}")
        }
    }

    val absoluteValueOfMinValue = if (primitive == Long::class) {
        Long.MIN_VALUE.toBigInteger().abs()
    } else {
        Int.MIN_VALUE.toBigInteger().abs()
    }

    addNamed(
        """
            | return when (%value:N) {
            |   ${primitive.zero} -> "${description.isoPeriodZeroString}"
            |   %minValue:L -> "-${description.isoPeriodPrefix}${absoluteValueOfMinValue}${description.isoPeriodUnit}"
            |   else -> buildString {
            |     if (%value:N < 0) { append('-') }
            |     append("${description.isoPeriodPrefix}")
            |     append($convertedValueString)
            |     append('${description.isoPeriodUnit}')
            |   }
            | }
        """.trimMargin(),
        arguments
    )
}

fun TemporalUnitClassGenerator.buildCompanionObjectTypeSpec() = io.islandtime.codegen.dsl.buildCompanionObjectTypeSpec {
    property("MIN", className) {
        addKdoc("The smallest supported value.")
        initializer("%T(%T.MIN_VALUE)", className, primitive)
    }
    property("MAX", className) {
        addKdoc("The largest supported value.")
        initializer("%T(%T.MAX_VALUE)", className, primitive)
    }
}

fun IntTemporalUnitClassGenerator.buildToLongUnitFunSpec() = buildFunSpec(
    "to${description.longName}"
) {
    addKdoc("Converts this duration to [${description.longName}].")
    addStatement("return %T(%N.toLong())", description.longClassName, valuePropertySpec)
}

fun IntTemporalUnitClassGenerator.buildToLongFunSpec() = buildFunSpec("toLong") {
    addKdoc("Converts this duration to a `Long` value.")
    addStatement("return %N.toLong()", valuePropertySpec)
}

fun LongTemporalUnitClassGenerator.buildToIntFunSpec() = buildFunSpec("toInt") {
    addKdoc(
        """
            Converts this duration to an `Int` value.
            @throws ArithmeticException if overflow occurs
        """.trimIndent()
    )
    addStatement("return %N.%T()", valuePropertySpec, javamath2kmp("toIntExact"))
}

fun LongTemporalUnitClassGenerator.buildToIntUncheckedFunSpec() = buildFunSpec(
    "toIntUnchecked"
) {
    addModifiers(KModifier.INTERNAL)
    addKdoc("Converts this duration to an `Int` value without checking for overflow.")
    addStatement("return %N.toInt()", valuePropertySpec)
}

fun LongTemporalUnitClassGenerator.buildToIntUnitFunSpec() = buildFunSpec(
    "to${description.intName}"
) {
    addKdoc(
        """
            Converts this duration to [${description.intName}].
            @throws ArithmeticException if overflow occurs
        """.trimIndent()
    )
    addStatement(
        "return %T(%N.%T())",
        description.intClassName,
        valuePropertySpec,
        javamath2kmp("toIntExact")
    )
}

fun LongTemporalUnitClassGenerator.buildToIntUnitUncheckedFunSpec() = buildFunSpec(
    "to${description.intName}Unchecked"
) {
    addModifiers(KModifier.INTERNAL)
    addKdoc("Converts this duration to [${description.intName}] without checking for overflow.")
    addAnnotation(PublishedApi::class)
    addStatement("return %T(%N.toInt())", description.intClassName, valuePropertySpec)
}

fun TemporalUnitClassGenerator.buildUnaryMinusFunSpec() = buildFunSpec("unaryMinus") {
    addModifiers(KModifier.OPERATOR)
    addKdoc(
        """
            Negates this duration.
            @throws ArithmeticException if overflow occurs
        """.trimIndent()
    )

    addStatement(
        "return %T(%N.%T())",
        className,
        valuePropertySpec,
        javamath2kmp("negateExact")
    )
}

fun TemporalUnitClassGenerator.buildNegateUncheckedFunSpec() =
    buildFunSpec("negateUnchecked") {
        addModifiers(KModifier.INTERNAL)
        addKdoc("Negates this duration without checking for overflow.")
        addStatement("return %T(-%N)", className, valuePropertySpec)
    }

enum class PlusOrMinusOperator(
    val functionName: String,
    val operator: ClassName,
    val uncheckedOperator: String
) {
    PLUS(
        functionName = "plus",
        operator = javamath2kmp("plusExact"),
        uncheckedOperator = "+"
    ),
    MINUS(
        functionName = "minus",
        operator = javamath2kmp("minusExact"),
        uncheckedOperator = "-"
    )
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

    val amount = ParameterSpec(conversion.toUnit.lowerPluralName, conversion.toUnit.intClassName)
    addParameter(amount)

    when (conversion.operator) {
        ConversionOperator.DIV -> {
            if (this@buildIntPlusMinusOperatorFunSpec is IntTemporalUnitClassGenerator &&
                description.forceLongInOperators
            ) {
                addStatement(
                    "return this.%N() %L %N.${description.inUnitPropertyName}",
                    toLongUnitFunSpec,
                    plusOrMinusOperator.uncheckedOperator,
                    amount
                )
            } else {
                addStatement(
                    "return this %L %N.${description.inUnitPropertyName}",
                    plusOrMinusOperator.uncheckedOperator,
                    amount
                )
            }
        }
        ConversionOperator.TIMES -> {
            if (this@buildIntPlusMinusOperatorFunSpec is IntTemporalUnitClassGenerator &&
                description.forceLongInOperators
            ) {
                addStatement(
                    "return this.%N().${conversion.toUnit.inUnitPropertyName} %L %N.to${conversion.toUnit.longName}()",
                    toLongUnitFunSpec,
                    plusOrMinusOperator.uncheckedOperator,
                    amount
                )
            } else {
                addStatement(
                    "return this.${conversion.toUnit.inUnitPropertyName} %L %N",
                    plusOrMinusOperator.uncheckedOperator,
                    amount
                )
            }
        }
        ConversionOperator.NONE -> {
            if (primitive == Int::class && description.forceLongInOperators) {
                addStatement(
                    "return %T(%N.toLong() %T %N.${description.valueName})",
                    description.longClassName,
                    valuePropertySpec,
                    plusOrMinusOperator.operator,
                    amount
                )
            } else {
                addStatement(
                    "return %T(%N %T %N.${description.valueName})",
                    className,
                    valuePropertySpec,
                    plusOrMinusOperator.operator,
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

    val amount = ParameterSpec(conversion.toUnit.lowerPluralName, conversion.toUnit.longClassName)
    addParameter(amount)

    when (conversion.operator) {
        ConversionOperator.DIV -> {
            if (this@buildLongPlusMinusOperatorFunSpec is IntTemporalUnitClassGenerator) {
                addStatement(
                    "return this.%N() %L %N.${description.inUnitPropertyName}",
                    toLongUnitFunSpec,
                    plusOrMinusOperator.uncheckedOperator,
                    amount
                )
            } else {
                addStatement(
                    "return this %L %N.${description.inUnitPropertyName}",
                    plusOrMinusOperator.uncheckedOperator,
                    amount
                )
            }
        }
        ConversionOperator.TIMES -> {
            if (this@buildLongPlusMinusOperatorFunSpec is IntTemporalUnitClassGenerator) {
                addStatement(
                    "return this.%N().${conversion.toUnit.inUnitPropertyName} %L %N",
                    toLongUnitFunSpec,
                    plusOrMinusOperator.uncheckedOperator,
                    amount
                )
            } else {
                addStatement(
                    "return this.${conversion.toUnit.inUnitPropertyName} %L %N",
                    plusOrMinusOperator.uncheckedOperator,
                    amount
                )
            }
        }
        ConversionOperator.NONE -> {
            if (primitive == Int::class) {
                addStatement(
                    "return %T(%N.toLong() %T %N.${description.valueName})",
                    description.longClassName,
                    valuePropertySpec,
                    plusOrMinusOperator.operator,
                    amount
                )
            } else {
                addStatement(
                    "return %T(%N %T %N.${description.valueName})",
                    className,
                    valuePropertySpec,
                    plusOrMinusOperator.operator,
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
    addKdoc(
        """
            Multiplies this duration by a scalar value.
            @throws ArithmeticException if overflow occurs
        """.trimIndent()
    )

    val scalar = ParameterSpec("scalar", scalarPrimitive.asTypeName())
    addParameter(scalar)

    if (this@buildTimesFunSpec is IntTemporalUnitClassGenerator &&
        (scalarPrimitive == Long::class || description.forceLongInOperators)
    ) {
        addStatement(
            "return this.%N() * %N",
            toLongUnitFunSpec,
            scalar
        )
    } else {
        addStatement(
            "return %T(%N %T %N)",
            className,
            valuePropertySpec,
            javamath2kmp("timesExact"),
            scalar
        )
    }
}

fun TemporalUnitClassGenerator.buildDivFunSpec(
    scalarPrimitive: KClass<*>
) = buildFunSpec("div") {
    addModifiers(KModifier.OPERATOR)
    returns(
        if (primitive == Long::class) {
            className
        } else {
            description.classNameFor(scalarPrimitive)
        }
    )

    val scalar = ParameterSpec("scalar", scalarPrimitive.asTypeName())
    addParameter(scalar)

    val arguments = mutableMapOf(
        "scalar" to scalar,
        "className" to className,
        "value" to valuePropertySpec,
        "negateUnchecked" to negateUncheckedFunSpec
    )

    if (this@buildDivFunSpec is IntTemporalUnitClassGenerator && scalarPrimitive == Long::class) {
        addKdoc(
            """
                Divides this duration by a scalar value.
                @throws ArithmeticException if the scalar is zero
            """.trimIndent()
        )
        arguments += "toLongUnit" to toLongUnitFunSpec
        addNamedCode("return this.%toLongUnit:N() / %scalar:N", arguments)
    } else {
        addKdoc(
            """
                Divides this duration by a scalar value.
                @throws ArithmeticException if overflow occurs or the scalar is zero
            """.trimIndent()
        )
        addNamedCode(
            """
                | return if (%scalar:N == ${scalarPrimitive.literalValueString(-1)}) {
                |   -this
                | } else {
                |   %className:T(%value:N / %scalar:N)
                | }
            """.trimMargin(),
            arguments
        )
    }
}

fun TemporalUnitClassGenerator.buildRemFunSpec(
    scalarPrimitive: KClass<*>
) = buildFunSpec("rem") {
    addModifiers(KModifier.OPERATOR)

    val scalar = ParameterSpec("scalar", scalarPrimitive.asTypeName())
    addParameter(scalar)

    if (this@buildRemFunSpec is IntTemporalUnitClassGenerator && scalarPrimitive == Long::class) {
        addStatement("return this.%N() %% %N", toLongUnitFunSpec, scalar)
    } else {
        addStatement("return %T(%N %% %N)", className, valuePropertySpec, scalar)
    }
}

val TemporalUnitConversion.propertyClassName get() = internal(constantName)

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
        addKdoc("Converts this duration to the number of whole ${conversion.toUnit.lowerPluralName}.")
        getter(
            buildGetterFunSpec {
                val statement = buildString {
                    append("return (%N / %T)")

                    if (primitive == Int::class && !conversion.valueFitsInInt) {
                        append(".toInt()")
                    }

                    append(".${conversion.toUnit.lowerPluralName}")
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
        operators += buildPropertySpec(
            conversion.toUnit.inUnitPropertyName,
            conversion.toUnit.operatorReturnClassNameFor(primitive)
        ) {
            addKdoc(
                """
                    Converts this duration to ${conversion.toUnit.lowerPluralName}.
                    @throws ArithmeticException if overflow occurs
                """.trimIndent()
            )
            getter(
                buildGetterFunSpec {
                    addStatement(
                        if (primitive == Int::class && conversion.toUnit.forceLongInOperators) {
                            "return (%N.toLong() %T %T).${conversion.toUnit.lowerPluralName}"
                        } else {
                            "return (%N %T %T).${conversion.toUnit.lowerPluralName}"
                        },
                        valuePropertySpec,
                        javamath2kmp("timesExact"),
                        conversion.propertyClassName
                    )
                }
            )
        }
    }

    operators += buildPropertySpec(
        if (overflowSafeMethodRequired) {
            conversion.toUnit.inUnitUncheckedPropertyName
        } else {
            conversion.toUnit.inUnitPropertyName
        },
        conversion.toUnit.operatorReturnClassNameFor(primitive)
    ) {
        if (overflowSafeMethodRequired) {
            addModifiers(KModifier.INTERNAL)
            addKdoc("Converts this duration to ${conversion.toUnit.lowerPluralName} without checking for overflow.")
        } else {
            addKdoc("Converts this duration to ${conversion.toUnit.lowerPluralName}.")
        }

        getter(
            buildGetterFunSpec {
                if (primitive == Int::class && conversion.toUnit.forceLongInOperators) {
                    addStatement(
                        "return (%N.toLong() * %T).${conversion.toUnit.lowerPluralName}",
                        valuePropertySpec,
                        conversion.propertyClassName
                    )
                } else {
                    addStatement(
                        "return (%N * %T).${conversion.toUnit.lowerPluralName}",
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
    description.lowerPluralName,
    className
) {
    addKdoc("Converts this value to a duration of ${description.lowerPluralName}.", className)
    receiver(primitive)
    getter(buildGetterFunSpec {
        addStatement("return %T(this)", className)
    })
}

fun TemporalUnitClassGenerator.buildTimesExtensionFunSpec(
    scalarPrimitive: KClass<*>
) = buildFunSpec("times") {
    receiver(scalarPrimitive)
    addModifiers(KModifier.OPERATOR)
    addKdoc(
        """
            Multiplies this value by a duration of ${description.lowerPluralName}.
            @throws ArithmeticException if overflow occurs
        """.trimIndent()
    )

    val unit = ParameterSpec(description.lowerPluralName, className)
    addParameter(unit)
    addStatement("return %N * this", unit)
}

fun TemporalUnitClassGenerator.buildKotlinDurationExtensionFunSpec() =
    buildFunSpec("toIsland${description.pluralName}") {
        receiver(KOTLIN_DURATION_CLASS_NAME)
        addAnnotation(EXPERIMENTAL_TIME_CLASS_NAME)
        addKdoc("Converts this duration to Island Time [%T].", className)
        addStatement(
            "return %T(this.toLong(kotlin.time.DurationUnit.${description.pluralName.toUpperCase(Locale.US)}))",
            className
        )
    }

fun TemporalUnitClassGenerator.buildToComponentsFunctions(): List<FunSpec> {
    val thisUnit = this.description

    return TemporalUnitDescription.values()
        .filter { it > thisUnit && thisUnit.per(it).isSupported() }
        .map { biggestUnit ->
            val allComponentUnits = TemporalUnitDescription.values()
                .filter { it in thisUnit..biggestUnit }
                .sortedDescending()

            buildFunSpec("toComponents") {
                addTypeVariable(TypeVariableName("T"))
                addModifiers(KModifier.INLINE)
                returns(TypeVariableName("T"))

                val lambdaParameters = allComponentUnits.map { currentUnit ->
                    buildParameterSpec(
                        currentUnit.lowerPluralName,
                        if (currentUnit == biggestUnit && primitive == Long::class) {
                            currentUnit.longClassName
                        } else {
                            currentUnit.intClassName
                        }
                    )
                }

                addParameter(
                    "action",
                    LambdaTypeName.get(parameters = lambdaParameters, returnType = TypeVariableName("T"))
                )

                for (currentUnit in allComponentUnits) {
                    val arguments = mutableMapOf<String, Any>(
                        "value" to valuePropertySpec
                    )

                    val conversionString = when (currentUnit) {
                        biggestUnit -> buildString {
                            val conversion = thisUnit per currentUnit
                            arguments["conversion"] = conversion.propertyClassName
                            append("(%value:N / %conversion:T)")

                            if (primitive == Int::class && !conversion.valueFitsInInt) {
                                append(".toInt()")
                            }
                        }
                        thisUnit -> buildString {
                            val conversion = thisUnit per thisUnit.nextBiggest
                            arguments["conversion"] = conversion.propertyClassName
                            append("(%value:N %% %conversion:T)")

                            if (primitive == Long::class || !conversion.valueFitsInInt) {
                                append(".toInt()")
                            }
                        }
                        else -> buildString {
                            val conversion1 = thisUnit per currentUnit.nextBiggest
                            val conversion2 = thisUnit per currentUnit

                            arguments += mapOf(
                                "conversion1" to conversion1.propertyClassName,
                                "conversion2" to conversion2.propertyClassName
                            )

                            append("((%value:N %% %conversion1:T) / %conversion2:T)")

                            if (primitive == Long::class || !conversion1.valueFitsInInt) {
                                append(".toInt()")
                            }
                        }
                    }

                    addNamedCode(
                        "val ${currentUnit.lowerPluralName} = ${conversionString}.${currentUnit.lowerPluralName}\n",
                        arguments
                    )
                }

                val allVariableNames = allComponentUnits.joinToString(", ") { it.lowerPluralName }
                addStatement("return action($allVariableNames)")
            }
        }
}
