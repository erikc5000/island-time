repositories {
    jcenter()
    google()
}

plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.61")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:0.10.0")
    implementation("com.android.tools.build:gradle:3.6.0-beta04")
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}