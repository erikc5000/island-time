package io.islandtime.codegen.generators

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import io.islandtime.codegen.SingleFileGenerator
import io.islandtime.codegen.descriptions.IntervalDescription
import io.islandtime.codegen.descriptions.IntervalDescription.OffsetDateTimeInterval
import io.islandtime.codegen.descriptions.IntervalDescription.ZonedDateTimeInterval
import io.islandtime.codegen.dsl.FileBuilder
import io.islandtime.codegen.dsl.file
import io.islandtime.codegen.rangesInternal

object IntervalOperatorsGenerator : SingleFileGenerator() {
    override fun generateSingle(): FileSpec = buildOperatorsFile()
}

private fun buildOperatorsFile() = file(
    packageName = "io.islandtime.ranges",
    fileName = "_Operators",
    jvmName = "RangesKt"
) {
    IntervalDescription.entries.forEach { buildOperatorsForClass(it) }
}

private fun FileBuilder.buildOperatorsForClass(receiverClass: IntervalDescription) {
    function(name = "random") {
        kdoc {
            """
                Returns a random date within this ${receiverClass.simpleName} using the default random number generator.
                ${receiverClass.additionalOperatorKdocs}
                
                @throws NoSuchElementException if the ${receiverClass.simpleName} is empty
                @throws UnsupportedOperationException if the ${receiverClass.simpleName} is unbounded
                @see ${receiverClass}.randomOrNull
            """.trimIndent()
        }
        receiver(receiverClass.typeName)
        returns(receiverClass.elementDescription.typeName)

        code {
            using("impl", rangesInternal("randomImpl"))
            using("random", ClassName("kotlin.random", "Random"))
            "return %impl:T(%random:T)"
        }
    }

    function(name = "random") {
        kdoc {
            """
                Returns a random date within this ${receiverClass.simpleName} using the supplied random number
                generator. ${receiverClass.additionalOperatorKdocs}
                
                @throws NoSuchElementException if the ${receiverClass.simpleName} is empty
                @throws UnsupportedOperationException if the ${receiverClass.simpleName} is unbounded
                @see ${receiverClass}.randomOrNull
            """.trimIndent()
        }
        receiver(receiverClass.typeName)
        argument("random", ClassName("kotlin.random", "Random"))
        returns(receiverClass.elementDescription.typeName)

        code {
            using("impl", rangesInternal("randomImpl"))
            "return %impl:T(%random:N)"
        }
    }

    function(name = "randomOrNull") {
        kdoc {
            """
                Returns a random date within this ${receiverClass.simpleName} using the default random number generator
                or `null` if the interval is empty or unbounded. ${receiverClass.additionalOperatorKdocs}
                
                @see ${receiverClass}.random
            """.trimIndent()
        }
        receiver(receiverClass.typeName)
        returns(receiverClass.elementDescription.typeName.copy(nullable = true))

        code {
            using("impl", rangesInternal("randomOrNullImpl"))
            using("random", ClassName("kotlin.random", "Random"))
            "return %impl:T(%random:T)"
        }
    }

    function(name = "randomOrNull") {
        kdoc {
            """
                Returns a random date within this ${receiverClass.simpleName} using the supplied random number
                generator or `null` if the interval is empty or unbounded. ${receiverClass.additionalOperatorKdocs}
                
                @see ${receiverClass}.random
            """.trimIndent()
        }
        receiver(receiverClass.typeName)
        argument("random", ClassName("kotlin.random", "Random"))
        returns(receiverClass.elementDescription.typeName.copy(nullable = true))

        code {
            using("impl", rangesInternal("randomOrNullImpl"))
            "return %impl:T(%random:N)"
        }
    }
}

private val IntervalDescription.additionalOperatorKdocs: String
    get() = when (this) {
        OffsetDateTimeInterval -> "The offset of the start date-time will be used."
        ZonedDateTimeInterval -> "The zone of the start date-time will be used."
        else -> ""
    }
