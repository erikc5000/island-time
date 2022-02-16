plugins {
    id("android-library")
    id("published-library")
}

android {
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("releaseAar") {
                from(components["release"])
            }
        }
    }
}
