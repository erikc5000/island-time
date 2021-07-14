import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.dokka.gradle.DokkaTaskPartial
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask

plugins {
    id("org.jetbrains.dokka")
    `maven-publish`
    signing
}

val isReleaseBuild get() = !version.toString().endsWith("SNAPSHOT")

tasks.register<DokkaTaskPartial>("dokkaMkdocsPartial") {
    dependencies {
        plugins("io.islandtime.gradle:mkdocs-dokka-plugin")
    }
}

val javadocJar by tasks.registering(Jar::class) {
    val dokkaHtml = tasks.named<DokkaTask>("dokkaHtml")
    dependsOn(dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaHtml.get().outputDirectory)
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

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType(AbstractTestTask::class).configureEach {
    testLogging {
        events = setOf(TestLogEvent.FAILED)
        exceptionFormat = TestExceptionFormat.FULL
        showStackTraces = true
    }
}

tasks.withType<AbstractDokkaLeafTask>().configureEach {
    val pomArtifactId: String? by project
    val pomMppArtifactId: String? by project
    (pomArtifactId ?: pomMppArtifactId)?.let { moduleName.set(it) }

    dokkaSourceSets {
        configureEach {
            includes.from(project.file("MODULE.md"))
            skipEmptyPackages.set(true)
            skipDeprecated.set(true)
        }
    }
}
