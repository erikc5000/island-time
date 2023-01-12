plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.kotlinGradle)
    implementation(libs.dokka)
    implementation(libs.androidGradle)
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
}
