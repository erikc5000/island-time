repositories {
    google()
    jcenter()
}

plugins {
    id("com.android.library")
    kotlin("android")
    id("published-library")
}

android {
    compileSdkVersion(29)

    defaultConfig {
        minSdkVersion(15)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        buildConfig = false
    }
}

afterEvaluate {
    val androidSourcesJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(android.sourceSets["main"].java.sourceFiles)
    }

    publishing {
        publications {
            create<MavenPublication>("releaseAar") {
                artifact(androidSourcesJar.get())
                from(components["release"])
            }
        }
    }
}