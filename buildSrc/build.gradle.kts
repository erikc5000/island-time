repositories {
    jcenter()
    google()
}

plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.72")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:0.10.1")
    implementation("com.android.tools.build:gradle:4.0.1")
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}