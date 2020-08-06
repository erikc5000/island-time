pluginManagement {
    repositories {
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        gradlePluginPortal()
    }

    resolutionStrategy.eachPlugin {
        if (requested.id.id == "kotlinx-atomicfu")
            useModule("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${requested.version}")
    }
}

include(
    ":core",
    ":tools:code-generator",
    ":extensions:parcelize",
    ":extensions:serialization"
)
