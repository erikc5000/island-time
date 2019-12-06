pluginManagement {
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.android.library" -> useModule("com.android.tools.build:gradle:${requested.version}")
            }
        }
    }

    repositories {
        google()
        gradlePluginPortal()
    }
}

include(
    ":core",
    ":tools:code-generator",
    ":extensions:parcelize",
    ":extensions:threetenabp"
)