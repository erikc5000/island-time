plugins {
    id("com.android.library")
    kotlin("android")
    id("base-convention")
}

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        buildConfig = false
    }
}
