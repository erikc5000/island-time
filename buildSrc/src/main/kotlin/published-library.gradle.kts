import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("org.jetbrains.dokka")
    `maven-publish`
    signing
}

val isReleaseBuild get() = !version.toString().endsWith("SNAPSHOT")

val dokka by tasks.existing(DokkaTask::class) {
    outputDirectory = "$buildDir/dokka"
    outputFormat = "html"
}

val generateMkdocsApiDocs by tasks.creating(DokkaTask::class) {
    outputDirectory = "$rootDir/docs/api"
    outputFormat = "gfm"
}

val javadocJar by tasks.registering(Jar::class) {
    dependsOn(dokka)
    archiveClassifier.set("javadoc")
    from(dokka.get().outputDirectory)
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications)
}

tasks.withType<Sign>().configureEach {
    onlyIf { isReleaseBuild }
}

publishing {
    repositories {
        maven {
            val snapshotRepositoryUrl: String by project
            val releaseRepositoryUrl: String by project
            val repositoryUrl = if (isReleaseBuild) releaseRepositoryUrl else snapshotRepositoryUrl

            url = uri(repositoryUrl)

            val repositoryUsername: String? by project
            val repositoryPassword: String? by project

            credentials {
                username = repositoryUsername.orEmpty()
                password = repositoryPassword.orEmpty()
            }
        }
    }

    publications.withType<MavenPublication>().configureEach {
        artifact(javadocJar.get())

        val pomName: String by project
        val pomDescription: String by project
        val pomScmUrl: String? by project
        val pomUrl: String? by project
        val pomScmConnection: String? by project
        val pomLicenseName: String? by project
        val pomLicenseUrl: String? by project
        val pomLicenseDist: String? by project
        val pomDeveloperId: String? by project
        val pomDeveloperName: String? by project
        val pomArtifactId: String? by project

        if (pomArtifactId != null) {
            artifactId = pomArtifactId
        }

        pom {
            name.set(pomName)
            description.set(pomDescription)
            url.set(pomUrl)
            licenses {
                license {
                    name.set(pomLicenseName)
                    url.set(pomLicenseUrl)
                    distribution.set(pomLicenseDist)
                }
            }
            developers {
                developer {
                    id.set(pomDeveloperId)
                    name.set(pomDeveloperName)

                }
            }
            scm {
                connection.set(pomScmConnection)
                url.set(pomScmUrl)
            }
        }
    }
}