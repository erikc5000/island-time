plugins {
    kotlin("jvm")
    application
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.squareup:kotlinpoet:1.3.0")
}

application {
    mainClassName = "dev.erikchristensen.islandtime.codegen.MainKt"
}

tasks.named<Jar>("jar") {
    manifest {
        attributes("Main-Class" to "dev.erikchristensen.islandtime.codegen.MainKt")
    }
}