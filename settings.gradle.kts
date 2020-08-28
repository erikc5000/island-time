pluginManagement {
    repositories {
        gradlePluginPortal()
        maven(url = "https://maven.pkg.jetbrains.space/kotlin/p/dokka/dev")
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
