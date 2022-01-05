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
import kotlin.reflect.KClass

object TemporalUnitGenerator : Generator {
    override fun generate(): List<FileSpec> {
        return TemporalUnitDescription.values().map { buildTemporalUnitFile(it) }
    }
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

private val KOTLIN_DURATION_CLASS_NAME = ClassName("kotlin.time", "Duration")
private val KOTLIN_DURATION_UNIT_CLASS_NAME = ClassName("kotlin.time", "DurationUnit")
private val KOTLIN_CONTRACT_CLASS_NAME = ClassName("kotlin.contracts", "contract")
private val KOTLIN_INVOCATION_KIND_CLASS_NAME = ClassName("kotlin.contracts", "InvocationKind")
private val OPT_IN_CLASS_NAME = ClassName("kotlin", "OptIn")
private val EXPERIMENTAL_CONTRACTS_CLASS_NAME = ClassName("kotlin.contracts", "ExperimentalContracts")

private val TemporalUnitDescription.kotlinDurationUnitPropertyClassName
    get() = MemberName(
        enclosingClassName = ClassName("kotlin.time", "Duration", "Companion"),
        simpleName = lowerPluralName,
    )

private fun KClass<*>.literalValueString(value: Long) = when (this) {
    Int::class -> "$value"
    Long::class -> "${value}L"
    else -> throw IllegalStateException("Unsupported primitive type")
}

private val TemporalUnitConversion.propertyClassName get() = internal(constantName)

private fun buildTemporalUnitFile(description: TemporalUnitDescription) = file(
    packageName = "io.islandtime.measures",
    fileName = "_${description.pluralName}",
    jvmName = "${description.pluralName}Kt"
) {
    annotation(OPT_IN_CLASS_NAME) {
        member {
            using("experimentalContracts", EXPERIMENTAL_CONTRACTS_CLASS_NAME)
            "%experimentalContracts:T::class"
        }
    }

    if (description.isTimeBased) {
        aliasedImports(
            KOTLIN_DURATION_CLASS_NAME to "KotlinDuration",
            KOTLIN_DURATION_UNIT_CLASS_NAME to "KotlinDurationUnit"
        )
        aliasedImport(description.kotlinDurationUnitPropertyClassName, "kotlin${description.pluralName}")
    }

    typeAlias(description.deprecatedIntName, description.className) {
        deprecated(
            message = "Replace with ${description.pluralName}.",
            replaceWith = description.pluralName,
            level = DeprecationLevel.ERROR
        )
    }

    typeAlias(description.deprecatedLongName, description.className) {
        deprecated(
            message = "Replace with ${description.pluralName}.",
            replaceWith = description.pluralName,
            level = DeprecationLevel.ERROR
        )
    }

    `class`(description.pluralName) {
        annotation(JvmInline::class)
        modifiers(KModifier.VALUE)
        superInterface(
            ClassName("kotlin", "Comparable").parameterizedBy(description.className)
        )

        primaryConstructor {
            argument(description.valueName, Long::class)
        }

        property(description.valueName, Long::class) {
            kdoc { "The underlying value." }
            initializer { description.valueName }
        }

        property("absoluteValue", description.className) {
            kdoc {
                """
                    The absolute value of this duration.
                    @throws ArithmeticException if overflow occurs
                """.trimIndent()
            }
            getter {
                code {
                    using("absExact", javamath2kmp("absExact"))
                    "return ${description.pluralName}(%absExact:T(${description.valueName}))"
                }
            }
        }

        buildUnitConversionProperties(description)

        constructor {
            argument(description.valueName, Int::class)
            callThisConstructor("${description.valueName}.toLong()")
        }

        function("isZero") {
            kdoc { "Checks if this duration is zero." }
            deprecated(
                message = "Replace with direct comparison.",
                replaceWith = "this == 0L.${description.lowerPluralName}",
                level = DeprecationLevel.ERROR
            )
            returns(Boolean::class)
            code { "return ${description.valueName} == 0L" }
        }

        function("isNegative") {
            kdoc { "Checks if this duration is negative." }
            deprecated(
                message = "Replace with direct comparison.",
                replaceWith = "this < 0L.${description.lowerPluralName}",
                level = DeprecationLevel.ERROR
            )
            returns(Boolean::class)
            code { "return ${description.valueName} < 0L" }
        }

        function("isPositive") {
            kdoc { "Checks if this duration is positive." }
            deprecated(
                message = "Replace with direct comparison.",
                replaceWith = "this > 0L.${description.lowerPluralName}",
                level = DeprecationLevel.ERROR
            )
            returns(Boolean::class)
            code { "return ${description.valueName} > 0L" }
        }

        function("compareTo") {
            modifiers(KModifier.OVERRIDE)
            argument("other", description.className)
            returns(Int::class)
            code { "return ${description.valueName}.compareTo(other.${description.valueName})" }
        }

        if (description.isTimeBased) {
            function("toKotlinDuration") {
                kdoc { "Converts this duration to a [${KOTLIN_DURATION_CLASS_NAME.canonicalName}]." }
                returns(KOTLIN_DURATION_CLASS_NAME)

                code {
                    using("kotlinDurationUnitProperty", description.kotlinDurationUnitPropertyClassName)
                    "return ${description.valueName}.%kotlinDurationUnitProperty:M"
                }
            }
        }

        function("toString") {
            kdoc { "Converts this duration to an ISO-8601 time interval representation." }
            modifiers(KModifier.OVERRIDE)
            returns(String::class)

            code {
                if (description.isoPeriodIsFractional) {
                    buildFractionalToStringCodeBlock(description)
                } else {
                    buildWholeToStringCodeBlock(description)
                }
            }
        }

        function("unaryMinus") {
            kdoc {
                """
                    Negates this duration.
                    @throws ArithmeticException if overflow occurs
                """.trimIndent()
            }
            modifiers(KModifier.OPERATOR)
            returns(description.className)

            code {
                using("negateExact", javamath2kmp("negateExact"))
                "return ${description.pluralName}(${description.valueName}.%negateExact:T())"
            }
        }

        function("negateUnchecked") {
            kdoc { "Negates this duration without checking for overflow." }
            modifiers(KModifier.INTERNAL)
            returns(description.className)
            code { "return ${description.pluralName}(-${description.valueName})" }
        }

        buildPlusAndMinusOperatorFunctions(description)

        listOf(Int::class, Long::class).forEach { primitive ->
            buildTimesFunction(primitive, description)
            buildDivFunction(primitive, description)
            buildRemFunction(primitive, description)
        }

        buildToComponentValuesFunctions(description)

        function("toInt") {
            kdoc {
                """
                    Converts this duration to an `Int` value.
                    @throws ArithmeticException if overflow occurs
                """.trimIndent()
            }
            returns(Int::class)
            code {
                using("toIntExact", javamath2kmp("toIntExact"))
                "return ${description.valueName}.%toIntExact:T()"
            }
        }

        function("toIntUnchecked") {
            kdoc { "Converts this duration to an `Int` value without checking for overflow." }
            modifiers(KModifier.INTERNAL)
            returns(Int::class)
            code { "return ${description.valueName}.toInt()" }
        }

        function("to${description.deprecatedIntName}") {
            kdoc {
                """
                    Converts this duration to [${description.deprecatedIntName}].
                    @throws ArithmeticException if overflow occurs
                """.trimIndent()
            }
            deprecated(
                message = "The 'Int' class no longer exists.",
                replaceWith = "this",
                level = DeprecationLevel.ERROR
            )
            returns(description.className)
            code { "return this" }
        }

        function("to${description.deprecatedIntName}Unchecked") {
            kdoc { "Converts this duration to [${description.deprecatedIntName}] without checking for overflow." }
            modifiers(KModifier.INTERNAL)
            deprecated(
                message = "The 'Int' class no longer exists.",
                replaceWith = "this",
                level = DeprecationLevel.ERROR
            )
            annotation(PublishedApi::class)
            returns(description.className)
            code { "return this" }
        }

        function("toLong") {
            kdoc { "Converts this duration to a `Long` value." }
            returns(Long::class)
            code { "return ${description.valueName}" }
        }

        function("toDouble") {
            kdoc { "Converts this duration to a `Double` value." }
            returns(Double::class)
            code { "return ${description.valueName}.toDouble()" }
        }

        companionObject {
            property("MIN", description.className) {
                kdoc { "The smallest supported value." }
                initializer { "${description.pluralName}(Long.MIN_VALUE)" }
            }
            property("MAX", description.className) {
                kdoc { "The largest supported value." }
                initializer { "${description.pluralName}(Long.MAX_VALUE)" }
            }
        }
    }

    listOf(Int::class, Long::class).forEach { primitive ->
        buildPrimitiveConstructorProperty(primitive, description)
        buildTimesExtensionFunction(primitive, description)
    }

    if (description.isTimeBased) {
        function("toIsland${description.pluralName}") {
            kdoc { "Converts this duration to Island Time [${description.pluralName}]." }
            receiver(KOTLIN_DURATION_CLASS_NAME)
            returns(description.className)
            code {
                using("kotlinDurationUnit", KOTLIN_DURATION_UNIT_CLASS_NAME)
                "return ${description.pluralName}(this.toLong(%kotlinDurationUnit:T.${description.pluralName.uppercase()}))"
            }
        }
    }
}

private fun CodeBlockBuilder.buildFractionalToStringCodeBlock(description: TemporalUnitDescription) {
    val value = description.valueName

    using(
        "absoluteValue" to ClassName("kotlin.math", "absoluteValue"),
        "toZeroPaddedString" to internal("toZeroPaddedString")
    )

    +"""
        | return if ($value == 0L) {
        |   "${description.isoPeriodZeroString}"
        | } else {
        |   buildString {
        |     val wholePart = ($value / ${description.isoPeriodUnitConversion.constantValue}).%absoluteValue:T
        |     val fractionalPart = ($value %% ${description.isoPeriodUnitConversion.constantValue}).toInt().%absoluteValue:T
        |     if ($value < 0) { append('-') }
        |     append("${description.isoPeriodPrefix}")
        |     append(wholePart)
        |     if (fractionalPart > 0) {
        |       append('.')
        |       append(fractionalPart.%toZeroPaddedString:T(${description.isoPeriodDecimalPlaces}).dropLastWhile { it == '0' })
        |     }
        |     append('${description.isoPeriodUnit}')
        |   }
        | }
    """.trimMargin()
}

private fun CodeBlockBuilder.buildWholeToStringCodeBlock(description: TemporalUnitDescription) {
    val value = description.valueName

    using(
        "absoluteValue" to ClassName("kotlin.math", "absoluteValue"),
        "timesExact" to javamath2kmp("timesExact")
    )

    val convertedValueString = buildString {
        append("$value.%absoluteValue:T")

        if (description.isoPeriodUnitConversion.isNecessary()) {
            append(" %timesExact:T ${description.isoPeriodUnitConversion.constantValue}")
        }
    }

    val absoluteValueOfMinValue = Long.MIN_VALUE.toBigInteger().abs()

    +"""
        | return when ($value) {
        |   0L -> "${description.isoPeriodZeroString}"
        |   Long.MIN_VALUE -> "-${description.isoPeriodPrefix}${absoluteValueOfMinValue}${description.isoPeriodUnit}"
        |   else -> buildString {
        |     if ($value < 0) { append('-') }
        |     append("${description.isoPeriodPrefix}")
        |     append($convertedValueString)
        |     append('${description.isoPeriodUnit}')
        |   }
        | }
    """.trimMargin()
}

fun ClassBuilder.buildPlusAndMinusOperatorFunctions(description: TemporalUnitDescription) =
    TemporalUnitDescription.values()
        .map { description per it }
        .filter { it.isSupported() }
        .forEach { conversion ->
            PlusOrMinusOperator.values().forEach { operator ->
                buildPlusOrMinusOperatorFunction(conversion, operator, description)
            }
        }

fun ClassBuilder.buildPlusOrMinusOperatorFunction(
    conversion: TemporalUnitConversion,
    plusOrMinusOperator: PlusOrMinusOperator,
    description: TemporalUnitDescription
) = function(plusOrMinusOperator.functionName) {
    modifiers(KModifier.OPERATOR)
    val amount = conversion.toUnit.lowerPluralName
    argument(amount, conversion.toUnit.className)

    when (conversion.operator) {
        ConversionOperator.DIV -> {
            returns(description.className)

            code {
                val operator = plusOrMinusOperator.uncheckedOperator
                "return this $operator %$amount:N.${description.inUnitPropertyName}"
            }
        }
        ConversionOperator.TIMES -> {
            returns(conversion.toUnit.className)

            code {
                val operator = plusOrMinusOperator.uncheckedOperator
                "return this.${conversion.toUnit.inUnitPropertyName} $operator %$amount:N"
            }
        }
        ConversionOperator.NONE -> {
            returns(description.className)

            code {
                using("operator", plusOrMinusOperator.operator)
                "return ${description.pluralName}(${description.valueName}·%operator:T %$amount:N.${description.valueName})"
            }
        }
    }
}

private fun ClassBuilder.buildTimesFunction(scalarPrimitive: KClass<*>, description: TemporalUnitDescription) {
    function("times") {
        kdoc {
            """
            Multiplies this duration by a scalar value.
            @throws ArithmeticException if overflow occurs
        """.trimIndent()
        }

        modifiers(KModifier.OPERATOR)
        argument("scalar", scalarPrimitive)
        returns(description.className)

        code {
            using("timesExact", javamath2kmp("timesExact"))
            "return ${description.pluralName}(${description.valueName}·%timesExact:T·%scalar:N)"
        }
    }
}

private fun ClassBuilder.buildDivFunction(scalarPrimitive: KClass<*>, description: TemporalUnitDescription) {
    function("div") {
        kdoc {
            """
                Returns this duration divided by a scalar value.
                @throws ArithmeticException if overflow occurs or the scalar is zero
            """.trimIndent()
        }

        modifiers(KModifier.OPERATOR)
        argument("scalar", scalarPrimitive)
        returns(description.className)

        code {
            """
                | return if (%scalar:N == ${scalarPrimitive.literalValueString(-1)}) {
                |   -this
                | } else {
                |   ${description.pluralName}(${description.valueName}·/·%scalar:N)
                | }
            """.trimMargin()
        }
    }
}

private fun ClassBuilder.buildRemFunction(scalarPrimitive: KClass<*>, description: TemporalUnitDescription) {
    function("rem") {
        kdoc { "Returns the remainder of this duration divided by a scalar value." }
        modifiers(KModifier.OPERATOR)
        argument("scalar", scalarPrimitive)
        returns(description.className)
        code { "return ${description.pluralName}(${description.valueName}·%%·%scalar:N)" }
    }
}

private fun ClassBuilder.buildUnitConversionProperties(description: TemporalUnitDescription) {
    TemporalUnitDescription.values()
        .map { otherUnit -> description per otherUnit }
        .filter { conversion -> conversion.isSupportedAndNecessary() }
        .forEach { conversion ->
            when (conversion.operator) {
                ConversionOperator.DIV -> buildInBiggerUnitConversionProperties(conversion, description)
                ConversionOperator.TIMES -> buildInSmallerUnitConversionProperties(conversion, description)
                else -> throw IllegalStateException("Invalid operator")
            }
        }
}

private fun ClassBuilder.buildInBiggerUnitConversionProperties(
    conversion: TemporalUnitConversion,
    description: TemporalUnitDescription
) {
    property(conversion.toUnit.inWholeUnitPropertyName, conversion.toUnit.className) {
        kdoc { "Converts this duration to the number of whole ${conversion.toUnit.lowerPluralName}." }
        getter {
            code {
                using(
                    "toUnit" to conversion.toUnit.className,
                    "conversionProperty" to conversion.propertyClassName
                )
                "return %toUnit:T(${description.valueName}·/·%conversionProperty:T)"
            }
        }
    }

    property(conversion.toUnit.deprecatedInWholeUnitPropertyName, conversion.toUnit.className) {
        deprecated(
            message = "Use ${conversion.toUnit.inWholeUnitPropertyName} instead.",
            replaceWith = "this.${conversion.toUnit.inWholeUnitPropertyName}",
            level = DeprecationLevel.ERROR
        )

        getter {
            code {
                using("deprecated", internal("deprecatedToError"))
                "return %deprecated:T()"
            }
        }
    }
}

private fun ClassBuilder.buildInSmallerUnitConversionProperties(
    conversion: TemporalUnitConversion,
    description: TemporalUnitDescription
) {
    property(conversion.toUnit.inUnitPropertyName, conversion.toUnit.className) {
        kdoc {
            """
                Converts this duration to ${conversion.toUnit.lowerPluralName}.
                @throws ArithmeticException if overflow occurs
            """.trimIndent()
        }
        getter {
            code {
                using(
                    "toUnit" to conversion.toUnit.className,
                    "timesExact" to javamath2kmp("timesExact"),
                    "conversionProperty" to conversion.propertyClassName
                )
                "return %toUnit:T(${description.valueName}·%timesExact:T·%conversionProperty:T)"
            }
        }
    }

    property(conversion.toUnit.inUnitUncheckedPropertyName, conversion.toUnit.className) {
        kdoc { "Converts this duration to ${conversion.toUnit.lowerPluralName} without checking for overflow." }
        modifiers(KModifier.INTERNAL)

        getter {
            code {
                using(
                    "toUnit" to conversion.toUnit.className,
                    "timesExact" to javamath2kmp("timesExact"),
                    "conversionProperty" to conversion.propertyClassName
                )
                "return %toUnit:T(${description.valueName}·*·%conversionProperty:T)"
            }
        }
    }
}

private fun ClassBuilder.buildToComponentValuesFunctions(thisUnit: TemporalUnitDescription) {
    TemporalUnitDescription.values()
        .filter { it > thisUnit && thisUnit.per(it).isSupported() }
        .map { biggestUnit ->
            val allComponentUnits = TemporalUnitDescription.values()
                .filter { it in thisUnit..biggestUnit }
                .sortedDescending()

            val typeVariable = TypeVariableName("T")

            function("toComponentValues") {
                modifiers(KModifier.INLINE)
                typeVariable(typeVariable)

                val lambdaParameters = allComponentUnits.map { currentUnit ->
                    buildParameterSpec(
                        currentUnit.lowerPluralName,
                        if (currentUnit == biggestUnit) Long::class else Int::class
                    )
                }

                argument(
                    "action",
                    LambdaTypeName.get(parameters = lambdaParameters, returnType = typeVariable)
                )

                returns(typeVariable)

                code {
                    exactlyOnceContractCode("action")

                    for (currentUnit in allComponentUnits) {
                        val conversionString = when (currentUnit) {
                            biggestUnit -> buildString {
                                val conversion = thisUnit per currentUnit
                                using(conversion.id, conversion.propertyClassName)
                                append("(${thisUnit.valueName} / %${conversion.id}:T)")
                            }
                            thisUnit -> buildString {
                                val conversion = thisUnit per thisUnit.nextBiggest
                                using(conversion.id, conversion.propertyClassName)
                                append("(${thisUnit.valueName} %% %${conversion.id}:T).toInt()")
                            }
                            else -> buildString {
                                val conversion1 = thisUnit per currentUnit.nextBiggest
                                val conversion2 = thisUnit per currentUnit

                                using(
                                    conversion1.id to conversion1.propertyClassName,
                                    conversion2.id to conversion2.propertyClassName
                                )

                                append("((${thisUnit.valueName} %% %${conversion1.id}:T) / %${conversion2.id}:T).toInt()")
                            }
                        }

                        +"val ${currentUnit.lowerPluralName} = $conversionString\n"
                    }

                    val allVariableNames = allComponentUnits.joinToString(separator = ", ") { it.lowerPluralName }
                    "return action($allVariableNames)"
                }
            }

            function("toComponents") {
                modifiers(KModifier.INLINE)
                typeVariable(typeVariable)

                val lambdaParameters = allComponentUnits.map { currentUnit ->
                    buildParameterSpec(currentUnit.lowerPluralName, currentUnit.className)
                }

                argument(
                    "action",
                    LambdaTypeName.get(parameters = lambdaParameters, returnType = typeVariable)
                )

                returns(typeVariable)

                code {
                    exactlyOnceContractCode("action")

                    val allVariableNames = allComponentUnits.joinToString(separator = ", ") { it.lowerPluralName }
                    val allConvertedVariables = allComponentUnits.joinToString(separator = ", ") {
                        "${it.pluralName}(${it.lowerPluralName})"
                    }

                    """
                        | return toComponentValues { $allVariableNames ->
                        |     action($allConvertedVariables)
                        | }
                    """.trimMargin()
                }
            }
        }
}

private fun CodeBlockBuilder.exactlyOnceContractCode(parameter: String) {
    using(
        "contract" to KOTLIN_CONTRACT_CLASS_NAME,
        "invocationKind" to KOTLIN_INVOCATION_KIND_CLASS_NAME
    )
    +"%contract:T { callsInPlace($parameter, %invocationKind:T.EXACTLY_ONCE) }"
}

private fun FileBuilder.buildPrimitiveConstructorProperty(primitive: KClass<*>, description: TemporalUnitDescription) {
    property(description.lowerPluralName, description.className) {
        kdoc { "Converts this value to a duration of ${description.lowerPluralName}." }
        receiver(primitive)
        getter {
            code { "return ${description.pluralName}(this)" }
        }
    }
}

private fun FileBuilder.buildTimesExtensionFunction(scalarPrimitive: KClass<*>, description: TemporalUnitDescription) {
    function("times") {
        kdoc {
            """
                Multiplies this value by a duration of ${description.lowerPluralName}.
                @throws ArithmeticException if overflow occurs
            """.trimIndent()
        }
        receiver(scalarPrimitive)
        modifiers(KModifier.OPERATOR)
        val unit = description.lowerPluralName
        argument(unit, description.className)
        returns(description.className)
        code { "return $unit * this" }
    }
}
