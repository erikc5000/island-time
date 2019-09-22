package io.islandtime.codegen

import java.io.File

const val OUTPUT_PATH = "../../core/src/commonMain/generated"

const val INTERVAL_PACKAGE_NAME = "io.islandtime.interval"
const val INTERNAL_PACKAGE_NAME = "io.islandtime.internal"

fun main() {
    (generateDurationUnitFileSpecs() + generateConstantsFileSpec())
        .forEach { it.writeTo(File(OUTPUT_PATH)) }
}