plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(kotlin("gradle-plugin", "1.4.30"))
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.4.10")
    implementation("com.android.tools.build:gradle:4.1.2")
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}
