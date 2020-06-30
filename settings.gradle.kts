pluginManagement {
    repositories {
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
        gradlePluginPortal()
    }
}

include(
    ":core",
    ":tools:code-generator",
    ":extensions:parcelize",
    ":extensions:serialization",
    ":extensions:threetenabp"
)