package dev.erikchristensen.islandtime.codegen

import java.io.File

const val OUTPUT_PATH = "../../core/src/commonMain/generated"

const val INTERVAL_PACKAGE_NAME = "dev.erikchristensen.islandtime.interval"
const val INTERNAL_PACKAGE_NAME = "dev.erikchristensen.islandtime.internal"

fun main() {
    (generateDurationUnitFileSpecs() + generateConstantsFileSpec())
        .forEach { it.writeTo(File(OUTPUT_PATH)) }
}