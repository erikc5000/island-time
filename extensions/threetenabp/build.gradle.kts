project.repositories {
    google()

    jcenter {
        content {
            includeGroup("org.jetbrains.trove4j")
        }
    }
}

plugins {
    id("com.android.library")
    kotlin("android")
    `maven-publish`
    signing
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

    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
        getByName("test").java.srcDirs("src/test/java", "src/test/kotlin")
        getByName("androidTest").java.srcDirs("src/test/kotlin")
    }
}

dependencies {
    implementation(project(":core"))
    implementation(kotlin("stdlib-jdk8"))

    api("com.jakewharton.threetenabp:threetenabp:1.2.1")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("com.google.truth:truth:1.0")
}

//project.afterEvaluate {
//    publishing {
//        publications {
//            create<MavenPublication>("mavenThreetenabp") {
//                artifactId = "threetenabp-extensions"
//                artifact(tasks.getByName("bundleReleaseAar"))
//            }
//        }
//    }
//}