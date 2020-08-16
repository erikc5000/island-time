package io.islandtime.codegen

import com.squareup.kotlinpoet.FileSpec

interface Generator {
    fun generate(): List<FileSpec>
}

abstract class SingleFileGenerator : Generator {
    override fun generate(): List<FileSpec> = listOf(generateSingle())

    protected abstract fun generateSingle(): FileSpec
}
