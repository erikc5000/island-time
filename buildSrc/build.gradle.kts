repositories {
    jcenter()
    google()
}

plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.61")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:0.10.1")
    implementation("com.android.tools.build:gradle:3.6.0")
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}