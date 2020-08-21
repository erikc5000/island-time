plugins {
    kotlin("jvm")
    application
}

repositories {
    jcenter()
}

dependencies {
    implementation(Libs.kotlinpoet)
}

application {
    mainClassName = "io.islandtime.codegen.MainKt"
}
