plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(kotlin("gradle-plugin", "1.5.21"))
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.4.32")
    implementation("com.android.tools.build:gradle:4.2.2")
    implementation("org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.16.2")
}
