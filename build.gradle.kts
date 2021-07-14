import org.jetbrains.dokka.gradle.DokkaMultiModuleTask

plugins {
    id("org.jetbrains.dokka")
}

tasks.register<DokkaMultiModuleTask>("dokkaMkdocsMultiModule") {
    dependencies {
        plugins("io.islandtime.gradle:mkdocs-dokka-plugin")
        plugins("org.jetbrains.dokka:gfm-template-processing-plugin:1.5.0")
    }

    addSubprojectChildTasks("dokkaMkdocsPartial")
    outputDirectory.set(file("$rootDir/docs/api"))
}

tasks.register("codegen") {
    dependsOn(gradle.includedBuild("code-generator").task(":run"))
}
