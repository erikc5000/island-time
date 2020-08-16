plugins {
    kotlin("jvm")
    application
}

dependencies {
    implementation(Libs.kotlinpoet)
}

application {
    mainClassName = "io.islandtime.codegen.MainKt"
}
