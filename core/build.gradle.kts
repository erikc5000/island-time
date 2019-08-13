plugins {
    kotlin("multiplatform")
}

apply(plugin = "maven-publish")

kotlin {
    jvm {
        withJava()
    }
//    js {
//        browser {
//        }
//        nodejs {
//        }
//    }
    // For ARM, should be changed to iosArm32 or iosArm64
    // For Linux, should be changed to e.g. linuxX64
    // For MacOS, should be changed to e.g. macosX64
    // For Windows, should be changed to e.g. mingwX64
//    mingwX64("mingw")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        jvm().compilations["main"].defaultSourceSet {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }
        jvm().compilations["test"].defaultSourceSet {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("com.google.truth:truth:1.0")
            }
        }
//        jsMain {
//            dependencies {
//                implementation kotlin('stdlib-js')
//            }
//        }
//        jsTest {
//            dependencies {
//                implementation kotlin('test-js')
//            }
//        }
//        mingwMain {
//        }
//        mingwTest {
//        }
    }
}