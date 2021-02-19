pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "kotlinx-atomicfu") {
                useModule("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${requested.version}")
            }
        }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        jcenter()
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
