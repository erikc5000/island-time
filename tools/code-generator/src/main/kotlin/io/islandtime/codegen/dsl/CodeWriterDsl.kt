package io.islandtime.codegen.dsl

import com.squareup.kotlinpoet.*
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

    fun property(name: String, returnType: KClass<*>, block: PropertyBuilder.() -> Unit) {
        property(name, returnType.asTypeName(), block)
    }

    fun property(name: String, returnType: TypeName, block: PropertyBuilder.() -> Unit) {
        builder.addProperty(PropertyBuilder(name, returnType).apply(block).build())
    }

    fun function(name: String, block: FunctionBuilder.() -> Unit) {
        builder.addFunction(FunctionBuilder(name).apply(block).build())
    }

    fun build(): FileSpec = builder.build()
}

@CodeWriterDsl
class PropertyBuilder(
    private val name: String,
    returnType: TypeName
) {
    private val builder = PropertySpec.builder(name, returnType)
    private val modifiers = mutableListOf<KModifier>()

    fun <T> kdoc(block: CodeBlockBuilder.() -> T) {
        builder.addKdoc(CodeBlockBuilder(sanitize = true).apply(block).build())
    }

    fun annotation(annotation: KClass<*>) {
        builder.addAnnotation(annotation)
    }

    fun receiver(type: TypeName) {
        builder.receiver(type)
    }

    fun modifiers(vararg modifiers: KModifier) {
        this.modifiers += modifiers
    }

    fun <T> initializer(block: CodeBlockBuilder.() -> T) {
        builder.apply {
            addModifiers(this@PropertyBuilder.modifiers)
            initializer(CodeBlockBuilder(insertLineSeparators = false).apply(block).build())
        }
    }

    fun <T> getter(block: CodeBlockBuilder.() -> T) {
        builder.getter(
            buildGetterFunSpec {
                addModifiers(this@PropertyBuilder.modifiers)
                addCode(CodeBlockBuilder().apply(block).build())
            }
        )
    }

    fun delegatesTo(memberName: String) {
        getter { "return $memberName.${this@PropertyBuilder.name}" }
    }

    fun build(): PropertySpec = builder.build()
}

@CodeWriterDsl
class FunctionBuilder(private val name: String) {
    private val builder = FunSpec.builder(name)
    private val parameterSpecs = mutableListOf<ParameterSpec>()

    fun annotation(annotation: KClass<*>) {
        builder.addAnnotation(annotation)
    }

    fun <T> kdoc(block: CodeBlockBuilder.() -> T) {
        builder.addKdoc(CodeBlockBuilder(sanitize = true).apply(block).build())
    }

    fun receiver(type: TypeName) {
        builder.receiver(type)
    }

    fun modifiers(vararg modifiers: KModifier) {
        builder.addModifiers(*modifiers)
    }

    fun returns(type: TypeName) {
        builder.returns(type)
    }

    fun returns(type: KClass<*>) {
        builder.returns(type)
    }

    fun argument(name: String, type: TypeName) {
        val parameterSpec = buildParameterSpec(name, type)
        parameterSpecs += parameterSpec
        builder.addParameter(parameterSpec)
    }

    fun <T> code(block: CodeBlockBuilder.() -> T) {
        builder.addCode(
            CodeBlockBuilder(parameterSpecs.associateBy { it.name }).apply(block).build()
        )
    }

    fun delegatesTo(memberName: String) {
        val funName = name
        val joinedArgs = parameterSpecs.joinToString { it.name }
        code { "return $memberName.${funName}($joinedArgs)" }
    }

    fun build(): FunSpec = builder.build()
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

    operator fun String.unaryPlus() {
        codeBlocks += if (sanitize) {
            split("\n\n").joinToString(separator = "\n\n") { it.replace(oldChar = '\n', newChar = ' ') }
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
