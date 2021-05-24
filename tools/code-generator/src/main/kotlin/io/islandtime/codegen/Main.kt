package io.islandtime.codegen

import com.squareup.kotlinpoet.ClassName
import io.islandtime.codegen.generators.*
import java.io.File

private const val OUTPUT_PATH = "../../core/src/commonMain/generated"

private const val ROOT_PACKAGE_NAME = "io.islandtime"
private const val BASE_PACKAGE_NAME = "io.islandtime.base"
private const val CALENDAR_PACKAGE_NAME = "io.islandtime.calendar"
private const val INTERNAL_PACKAGE_NAME = "io.islandtime.internal"
private const val LOCALE_PACKAGE_NAME = "io.islandtime.locale"
private const val MEASURES_PACKAGE_NAME = "io.islandtime.measures"
private const val RANGES_PACKAGE_NAME = "io.islandtime.ranges"
private const val RANGES_INTERNAL_PACKAGE_NAME = "io.islandtime.ranges.internal"

fun root(name: String) = ClassName(ROOT_PACKAGE_NAME, name)
fun base(name: String) = ClassName(BASE_PACKAGE_NAME, name)
fun calendar(name: String) = ClassName(CALENDAR_PACKAGE_NAME, name)
fun internal(name: String) = ClassName(INTERNAL_PACKAGE_NAME, name)
fun locale(name: String) = ClassName(LOCALE_PACKAGE_NAME, name)
fun measures(name: String) = ClassName(MEASURES_PACKAGE_NAME, name)
fun ranges(name: String) = ClassName(RANGES_PACKAGE_NAME, name)
fun rangesInternal(name: String) = ClassName(RANGES_INTERNAL_PACKAGE_NAME, name)

fun javamath2kmp(name: String) = ClassName("dev.erikchristensen.javamath2kmp", name)

private val generators = arrayOf(
    TemporalUnitGenerator,
    ConstantsGenerator,
    DatePropertiesGenerator,
    DateConversionsGenerator,
    IntervalOperatorsGenerator,
    IntervalPropertiesGenerator
)

fun main() {
    generators.flatMap { it.generate() }.forEach { it.writeTo(File(OUTPUT_PATH)) }
}
