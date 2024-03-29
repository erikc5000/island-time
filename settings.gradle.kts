pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
}

include(
    ":core",
    ":extensions:parcelize",
    ":integration-test:serialization"
)

includeBuild("tools/code-generator")

includeBuild("tools/mkdocs-dokka-plugin") {
    dependencySubstitution {
        substitute(module("io.islandtime.gradle:mkdocs-dokka-plugin")).using(project(":"))
    }
}
