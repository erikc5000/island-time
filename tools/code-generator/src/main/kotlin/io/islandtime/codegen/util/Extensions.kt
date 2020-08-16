package io.islandtime.codegen.util

import kotlin.reflect.KClass

val KClass<*>.zero: String
    get() = when (this) {
        Long::class -> "0L"
        Float::class -> "0.0f"
        Double::class -> "0.0"
        else -> "0"
    }
