pluginManagement {
    repositories {
        gradlePluginPortal()
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
    }
}

include(
    ":core",
    ":extensions:parcelize",
    ":extensions:serialization"
)

includeBuild("tools/code-generator")

includeBuild("tools/mkdocs-dokka-plugin") {
    dependencySubstitution {
        substitute(module("io.islandtime.gradle:mkdocs-dokka-plugin")).with(project(":"))
    }
}
