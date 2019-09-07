plugins {
    kotlin("multiplatform")
    `maven-publish`
    signing
}

kotlin {
    jvm {
        withJava()
    }
//    js {
//        useCommonJs()
//        browser()
//        nodejs()
//    }
    // For ARM, should be changed to iosArm32 or iosArm64
    // For Linux, should be changed to e.g. linuxX64
    // For MacOS, should be changed to e.g. macosX64
    // For Windows, should be changed to e.g. mingwX64
//    mingwX64("mingw")
    sourceSets {
        val commonMain by getting {
            kotlin.srcDirs("src/commonMain/generated", "src/commonMain/kotlin")

            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("co.touchlab:stately:0.9.3")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("com.google.truth:truth:1.0")
            }
        }
//        val jsMain by getting {
//            dependencies {
//                implementation(kotlin("stdlib-js"))
//            }
//        }
//        val jsTest by getting {
//            dependencies {
//                implementation(kotlin("test-js"))
//            }
//        }
//        mingwMain {
//        }
//        mingwTest {
//        }
    }
}