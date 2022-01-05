plugins {
    id("android-library")
    id("published-library")
}

android {
    publishing {
        singleVariant("release")
    }
}

afterEvaluate {
    val androidSourcesJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(android.sourceSets["main"].java.srcDirs)
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
