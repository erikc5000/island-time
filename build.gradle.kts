import org.jetbrains.dokka.gradle.DokkaMultiModuleTask

plugins {
    `base-convention`
    id("org.jetbrains.dokka")
    alias(libs.plugins.kover)
}

repositories {
    mavenCentral()
}

buildscript {
    dependencies {
        classpath(libs.atomicfuGradle)
    }
}

tasks.register<DokkaMultiModuleTask>("dokkaMkdocsMultiModule") {
    dependencies {
        plugins("io.islandtime.gradle:mkdocs-dokka-plugin")
        plugins(libs.dokkaGfmTemplateProcessing)
    }

    addSubprojectChildTasks("dokkaMkdocsPartial")
    outputDirectory.set(file("$rootDir/docs/api"))
}

tasks.register("codegen") {
    dependsOn(gradle.includedBuild("code-generator").task(":run"))
}
