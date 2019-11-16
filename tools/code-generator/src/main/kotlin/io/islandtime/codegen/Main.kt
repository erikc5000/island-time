package io.islandtime.codegen

import java.io.File

const val OUTPUT_PATH = "../../core/src/commonMain/generated"

const val MEASURES_PACKAGE_NAME = "io.islandtime.measures"
const val INTERNAL_PACKAGE_NAME = "io.islandtime.internal"

fun main() {
    (generateTemporalUnitFileSpecs() + generateConstantsFileSpec())
        .forEach { it.writeTo(File(OUTPUT_PATH)) }
}