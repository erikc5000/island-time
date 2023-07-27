import org.jetbrains.dokka.gradle.DokkaMultiModuleTask

plugins {
    `base-convention`
    id("org.jetbrains.dokka")
    alias(libs.plugins.kover)
}

buildscript {
    dependencies {
        classpath(libs.atomicfuGradle)
    }
}

dependencies {
    kover(project(":core"))
}

tasks.register<DokkaMultiModuleTask>("dokkaMkdocsMultiModule") {
    dependencies {
        plugins("io.islandtime.gradle:mkdocs-dokka-plugin")
        plugins(libs.dokkaGfmTemplateProcessing)
    }

    // This is deprecated, but doesn't seem to have a replacement yet
    @Suppress("DEPRECATION")
    addSubprojectChildTasks("dokkaMkdocsPartial")
    outputDirectory.set(file("$rootDir/docs/api"))
}

tasks.register("codegen") {
    dependsOn(gradle.includedBuild("code-generator").task(":run"))
}
