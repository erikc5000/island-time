package io.islandtime.codegen

import com.squareup.kotlinpoet.*
import kotlin.reflect.KClass

inline fun buildFileSpec(
    packageName: String,
    fileName: String,
    block: FileSpec.Builder.() -> Unit
) = FileSpec.builder(packageName, fileName).apply(block).build()

inline fun buildClassTypeSpec(
    className: ClassName,
    block: TypeSpec.Builder.() -> Unit
) = TypeSpec.classBuilder(className).apply(block).build()

inline fun buildCompanionObjectTypeSpec(
    name: String? = null,
    block: TypeSpec.Builder.() -> Unit
) = TypeSpec.companionObjectBuilder(name).apply(block).build()

inline fun buildAnnotationSpec(
    type: KClass<out Annotation>,
    block: AnnotationSpec.Builder.() -> Unit
) = AnnotationSpec.builder(type).apply(block).build()

inline fun buildFunSpec(
    name: String,
    block: FunSpec.Builder.() -> Unit
) = FunSpec.builder(name).apply(block).build()

inline fun buildConstructorFunSpec(
    block: FunSpec.Builder.() -> Unit
) = FunSpec.constructorBuilder().apply(block).build()

inline fun buildGetterFunSpec(
    block: FunSpec.Builder.() -> Unit
) = FunSpec.getterBuilder().apply(block).build()

inline fun buildPropertySpec(
    name: String,
    type: TypeName,
    vararg modifiers: KModifier,
    block: PropertySpec.Builder.() -> Unit = {}
) = PropertySpec.builder(name, type, *modifiers).apply(block).build()

inline fun buildPropertySpec(
    name: String,
    type: KClass<*>,
    vararg modifiers: KModifier,
    block: PropertySpec.Builder.() -> Unit = {}
) = PropertySpec.builder(name, type, *modifiers).apply(block).build()

inline fun TypeSpec.Builder.property(
    name: String,
    type: TypeName,
    vararg modifiers: KModifier,
    block: PropertySpec.Builder.() -> Unit = {}
) = addProperty(buildPropertySpec(name, type, *modifiers) { block(this) })

inline fun TypeSpec.Builder.property(
    name: String,
    type: KClass<*>,
    vararg modifiers: KModifier,
    block: PropertySpec.Builder.() -> Unit = {}
) = addProperty(buildPropertySpec(name, type, *modifiers) { block(this) })

inline fun buildParameterSpec(
    name: String,
    type: TypeName,
    vararg modifiers: KModifier,
    block: ParameterSpec.Builder.() -> Unit = {}
) = ParameterSpec.builder(name, type, *modifiers).apply(block).build()

inline fun buildCodeBlock(
    block: CodeBlock.Builder.() -> Unit
) = CodeBlock.builder().apply(block).build()