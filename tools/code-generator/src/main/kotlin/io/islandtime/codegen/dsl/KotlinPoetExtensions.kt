package io.islandtime.codegen.dsl

import com.squareup.kotlinpoet.*
import kotlin.reflect.KClass

inline fun buildAnnotationSpec(
    type: KClass<out Annotation>,
    block: AnnotationSpec.Builder.() -> Unit
) = AnnotationSpec.builder(type).apply(block).build()

inline fun buildParameterSpec(
    name: String,
    type: TypeName,
    vararg modifiers: KModifier,
    block: ParameterSpec.Builder.() -> Unit = {}
) = ParameterSpec.builder(name, type, *modifiers).apply(block).build()

inline fun buildParameterSpec(
    name: String,
    type: KClass<*>,
    vararg modifiers: KModifier,
    block: ParameterSpec.Builder.() -> Unit = {}
) = ParameterSpec.builder(name, type, *modifiers).apply(block).build()

inline fun buildCodeBlock(
    block: CodeBlock.Builder.() -> Unit
) = CodeBlock.builder().apply(block).build()
