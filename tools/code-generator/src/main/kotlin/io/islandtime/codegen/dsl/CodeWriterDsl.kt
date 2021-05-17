package io.islandtime.codegen.dsl

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import kotlin.reflect.KClass

@DslMarker
annotation class CodeWriterDsl

@CodeWriterDsl
class FileBuilder(
    packageName: String,
    fileName: String,
    jvmName: String
) {
    private val builder = FileSpec.builder(packageName, fileName).apply {
        addHeader(jvmName)
    }

    fun aliasedImport(className: ClassName, `as`: String) {
        builder.addAliasedImport(className, `as`)
    }

    fun aliasedImports(vararg imports: Pair<ClassName, String>) {
        imports.forEach { (className, `as`) -> aliasedImport(className, `as`) }
    }

    fun annotation(annotation: KClass<*>, block: AnnotationBuilder.() -> Unit = {}) {
        annotation(annotation.asClassName(), block)
    }

    fun annotation(annotation: ClassName, block: AnnotationBuilder.() -> Unit = {}) {
        builder.addAnnotation(AnnotationBuilder(annotation).apply(block).build())
    }

    fun property(name: String, returnType: KClass<*>, block: PropertyBuilder.() -> Unit) {
        property(name, returnType.asTypeName(), block)
    }

    fun property(name: String, returnType: TypeName, block: PropertyBuilder.() -> Unit) {
        builder.addProperty(PropertyBuilder(name, returnType).apply(block).build())
    }

    fun function(name: String, block: FunctionBuilder.() -> Unit) {
        builder.addFunction(FunctionBuilder(name).apply(block).build())
    }

    fun `class`(name: String, block: ClassBuilder.() -> Unit) {
        builder.addType(ClassBuilder(name).apply(block).build())
    }

    fun typeAlias(name: String, type: TypeName, block: TypeAliasBuilder.() -> Unit) {
        builder.addTypeAlias(TypeAliasBuilder(name, type).apply(block).build())
    }

    fun build(): FileSpec = builder.build()
}

@CodeWriterDsl
class PropertyBuilder(
    private val name: String,
    returnType: TypeName
) {
    private val builder = PropertySpec.builder(name, returnType)

    fun <T> kdoc(block: CodeBlockBuilder.() -> T) {
        builder.addKdoc(CodeBlockBuilder(sanitize = true).apply(block).build())
    }

    fun annotation(annotation: KClass<*>) {
        builder.addAnnotation(annotation)
    }

    fun deprecated(message: String, replaceWith: String? = null, level: DeprecationLevel = DeprecationLevel.WARNING) {
        builder.addAnnotation(buildDeprecatedAnnotationSpec(message, replaceWith, level))
    }

    fun receiver(type: TypeName) {
        builder.receiver(type)
    }

    fun receiver(type: KClass<*>) {
        builder.receiver(type)
    }

    fun modifiers(vararg modifiers: KModifier) {
        builder.addModifiers(*modifiers)
    }

    fun <T> initializer(block: CodeBlockBuilder.() -> T) {
        builder.initializer(CodeBlockBuilder(insertLineSeparators = false).apply(block).build())
    }

    fun getter(block: GetterFunctionBuilder.() -> Unit) {
        builder.getter(GetterFunctionBuilder().apply(block).build())
    }

    fun delegatesTo(memberName: String, vararg modifiers: KModifier) {
        getter {
            modifiers(*modifiers)
            code { "return $memberName.${this@PropertyBuilder.name}" }
        }
    }

    fun build(): PropertySpec = builder.build()
}

abstract class AbstractFunctionBuilder(
    protected val builder: FunSpec.Builder
) {
    protected val parameterSpecs = mutableListOf<ParameterSpec>()

    fun annotation(annotation: KClass<*>, block: AnnotationBuilder.() -> Unit = {}) {
        annotation(annotation.asClassName(), block)
    }

    fun annotation(annotation: ClassName, block: AnnotationBuilder.() -> Unit = {}) {
        builder.addAnnotation(AnnotationBuilder(annotation).apply(block).build())
    }

    fun deprecated(message: String, replaceWith: String? = null, level: DeprecationLevel = DeprecationLevel.WARNING) {
        builder.addAnnotation(buildDeprecatedAnnotationSpec(message, replaceWith, level))
    }

    fun <T> kdoc(block: CodeBlockBuilder.() -> T) {
        builder.addKdoc(CodeBlockBuilder(sanitize = true).apply(block).build())
    }

    fun modifiers(vararg modifiers: KModifier) {
        builder.addModifiers(*modifiers)
    }

    fun <T> code(block: CodeBlockBuilder.() -> T) {
        builder.addCode(
            CodeBlockBuilder(parameterSpecs.associateBy { it.name }).apply(block).build()
        )
    }

    fun build(): FunSpec = builder.build()
}

abstract class AbstractFunctionWithArgsBuilder(builder: FunSpec.Builder) : AbstractFunctionBuilder(builder) {
    fun argument(name: String, type: KClass<*>) {
        argument(name, type.asTypeName())
    }

    fun argument(name: String, type: TypeName) {
        val parameterSpec = buildParameterSpec(name, type)
        parameterSpecs += parameterSpec
        builder.addParameter(parameterSpec)
    }
}

@CodeWriterDsl
class GetterFunctionBuilder : AbstractFunctionBuilder(FunSpec.getterBuilder()) {
    fun returns(type: TypeName) {
        builder.returns(type)
    }

    fun returns(type: KClass<*>) {
        builder.returns(type)
    }
}

@CodeWriterDsl
class FunctionBuilder(private val name: String) : AbstractFunctionWithArgsBuilder(FunSpec.builder(name)) {
    fun receiver(type: TypeName) {
        builder.receiver(type)
    }

    fun receiver(type: KClass<*>) {
        builder.receiver(type)
    }

    fun returns(type: TypeName) {
        builder.returns(type)
    }

    fun returns(type: KClass<*>) {
        builder.returns(type)
    }

    fun typeVariable(name: TypeVariableName) {
        builder.addTypeVariable(name)
    }

    fun delegatesTo(memberName: String) {
        val funName = name
        val joinedArgs = parameterSpecs.joinToString { it.name }
        code { "return $memberName.${funName}($joinedArgs)" }
    }
}

@CodeWriterDsl
class ConstructorBuilder : AbstractFunctionWithArgsBuilder(FunSpec.constructorBuilder()) {
    fun callThisConstructor(vararg arguments: String) {
        builder.callThisConstructor(*arguments)
    }
}

abstract class AbstractTypeBuilder(protected val builder: TypeSpec.Builder) {
    fun <T> kdoc(block: CodeBlockBuilder.() -> T) {
        builder.addKdoc(CodeBlockBuilder(sanitize = true).apply(block).build())
    }

    fun superInterface(type: TypeName) {
        builder.addSuperinterface(type)
    }

    fun property(name: String, returnType: KClass<*>, block: PropertyBuilder.() -> Unit) {
        property(name, returnType.asTypeName(), block)
    }

    fun property(name: String, returnType: TypeName, block: PropertyBuilder.() -> Unit) {
        builder.addProperty(PropertyBuilder(name, returnType).apply(block).build())
    }

    fun function(name: String, block: FunctionBuilder.() -> Unit) {
        builder.addFunction(FunctionBuilder(name).apply(block).build())
    }

    fun build(): TypeSpec = builder.build()
}

@CodeWriterDsl
class ClassBuilder(name: String) : AbstractTypeBuilder(TypeSpec.classBuilder(name)) {
    fun annotation(annotation: KClass<*>) {
        builder.addAnnotation(annotation)
    }

    fun modifiers(vararg modifiers: KModifier) {
        builder.addModifiers(*modifiers)
    }

    fun primaryConstructor(block: ConstructorBuilder.() -> Unit) {
        builder.primaryConstructor(ConstructorBuilder().apply(block).build())
    }

    fun constructor(block: ConstructorBuilder.() -> Unit) {
        builder.addFunction(ConstructorBuilder().apply(block).build())
    }

    fun companionObject(block: CompanionObjectBuilder.() -> Unit) {
        builder.addType(CompanionObjectBuilder().apply(block).build())
    }
}

@CodeWriterDsl
class CompanionObjectBuilder(name: String? = null) : AbstractTypeBuilder(TypeSpec.companionObjectBuilder(name))

@CodeWriterDsl
class TypeAliasBuilder(name: String, type: TypeName) {
    private val builder = TypeAliasSpec.builder(name, type)

    fun annotation(annotation: KClass<*>, block: AnnotationBuilder.() -> Unit = {}) {
        annotation(annotation.asClassName(), block)
    }

    fun annotation(annotation: ClassName, block: AnnotationBuilder.() -> Unit = {}) {
        builder.addAnnotation(AnnotationBuilder(annotation).apply(block).build())
    }

    fun deprecated(message: String, replaceWith: String? = null, level: DeprecationLevel = DeprecationLevel.WARNING) {
        builder.addAnnotation(buildDeprecatedAnnotationSpec(message, replaceWith, level))
    }

    fun build(): TypeAliasSpec = builder.build()
}

@CodeWriterDsl
class AnnotationBuilder(type: ClassName) {
    private val builder = AnnotationSpec.builder(type)

    fun <T> member(block: CodeBlockBuilder.() -> T) {
        builder.addMember(CodeBlockBuilder(insertLineSeparators = false).apply(block).build())
    }

    fun build(): AnnotationSpec = builder.build()
}

@CodeWriterDsl
class CodeBlockBuilder(
    arguments: Map<String, Any?> = emptyMap(),
    private val sanitize: Boolean = false,
    private val insertLineSeparators: Boolean = true
) {
    private val arguments = mutableMapOf<String, Any?>().apply { this += arguments }
    private val codeBlocks = mutableListOf<String>()

    fun using(name: String, type: Any?) {
        arguments += name to type
    }

    fun using(vararg arguments: Pair<String, Any?>) {
        arguments.forEach { (name, type) -> using(name, type) }
    }

    operator fun String.unaryPlus() {
        codeBlocks += if (sanitize) {
            split("\n\n").joinToString(separator = "\n\n") {
                val trimmed = it.trimStart()

                if (!trimmed.startsWith('@')) {
                    trimmed.replace(oldChar = '\n', newChar = ' ')
                } else {
                    trimmed
                }
            }
        } else {
            this
        }
    }

    fun build() = buildCodeBlock {
        codeBlocks
            .map { if (insertLineSeparators && !it.endsWith('\n')) "$it\n" else it }
            .forEach { addNamed(it, arguments) }
    }
}

inline fun file(
    packageName: String,
    fileName: String,
    jvmName: String,
    builder: FileBuilder.() -> Unit
): FileSpec {
    return FileBuilder(packageName, fileName, jvmName).apply(builder).build()
}

private fun <T> CodeBlockBuilder.apply(block: CodeBlockBuilder.() -> T): CodeBlockBuilder {
    val returnValue = block()
    if (returnValue is String) +returnValue
    return this
}

fun FileSpec.Builder.addHeader(jvmName: String): FileSpec.Builder {
    addAnnotation(JvmMultifileClass::class)
    addAnnotation(buildAnnotationSpec(JvmName::class) {
        addMember("%S", jvmName)
    })
    addComment("\nThis file is auto-generated by 'tools:code-generator'\n")
    return this
}

private fun buildDeprecatedAnnotationSpec(
    message: String,
    replaceWith: String?,
    level: DeprecationLevel
): AnnotationSpec = buildAnnotationSpec(Deprecated::class) {
    addMember("message = %S", message)
    if (replaceWith != null) addMember("replaceWith = ReplaceWith(%S)", replaceWith)
    addMember("level = DeprecationLevel.$level")
}
