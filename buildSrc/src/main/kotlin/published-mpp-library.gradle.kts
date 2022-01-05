plugins {
    id("mpp-library")
    id("published-library")
}

publishing {
    val pomMppArtifactId: String? by project

    publications.withType<MavenPublication>().configureEach {
        if (pomMppArtifactId != null) {
            artifactId = if (name == "kotlinMultiplatform") {
                pomMppArtifactId
            } else {
                "${pomMppArtifactId}-$name"
            }
        }
    }
}
