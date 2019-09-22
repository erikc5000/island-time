plugins {
    kotlin("jvm")
    application
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.squareup:kotlinpoet:1.3.0")
}

application {
    mainClassName = "io.islandtime.codegen.MainKt"
}

tasks.named<Jar>("jar") {
    manifest {
        attributes("Main-Class" to "io.islandtime.codegen.MainKt")
    }
}