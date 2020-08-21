pluginManagement {
    repositories {
        gradlePluginPortal()
        maven(url = "https://maven.pkg.jetbrains.space/kotlin/p/dokka/dev")
    }
}

include(
    ":core",
    ":tools:code-generator",
    ":extensions:parcelize",
    ":extensions:serialization",
    ":extensions:threetenabp"
)

includeBuild("tools/mkdocs-dokka-plugin") {
    dependencySubstitution {
        substitute(module("io.islandtime.gradle:mkdocs-dokka-plugin")).with(project(":"))
    }
}
