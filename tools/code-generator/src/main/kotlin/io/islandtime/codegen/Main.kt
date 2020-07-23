package io.islandtime.codegen

import com.squareup.kotlinpoet.ClassName
import io.islandtime.codegen.generators.ConstantsGenerator
import io.islandtime.codegen.generators.DatePropertiesGenerator
import io.islandtime.codegen.generators.TemporalUnitGenerator
import java.io.File

private const val OUTPUT_PATH = "../../core/src/commonMain/generated"

private const val BASE_PACKAGE_NAME = "io.islandtime"
private const val MEASURES_PACKAGE_NAME = "io.islandtime.measures"
private const val CALENDAR_PACKAGE_NAME = "io.islandtime.calendar"
private const val INTERNAL_PACKAGE_NAME = "io.islandtime.internal"

fun base(name: String) = ClassName(BASE_PACKAGE_NAME, name)
fun internal(name: String) = ClassName(INTERNAL_PACKAGE_NAME, name)
fun measures(name: String) = ClassName(MEASURES_PACKAGE_NAME, name)
fun calendar(name: String) = ClassName(CALENDAR_PACKAGE_NAME, name)

private val generators = arrayOf(
    TemporalUnitGenerator,
    ConstantsGenerator,
    DatePropertiesGenerator
)

fun main() {
    generators.flatMap { it.generate() }.forEach { it.writeTo(File(OUTPUT_PATH)) }
}