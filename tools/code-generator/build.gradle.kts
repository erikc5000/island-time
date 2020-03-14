plugins {
    kotlin("jvm")
    application
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(Libs.kotlinpoet)
}

application {
    mainClassName = "io.islandtime.codegen.MainKt"
}