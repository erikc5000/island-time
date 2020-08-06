plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
    google()
}

dependencies {
    implementation(kotlin("gradle-plugin", "1.4.0-rc"))
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:0.10.1")
    implementation("com.android.tools.build:gradle:4.0.1")
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}
