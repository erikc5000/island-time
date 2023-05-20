plugins {
    alias(libs.plugins.kotlinJvm)
}

dependencies {
    compileOnly(libs.dokkaCore)
    implementation(libs.dokkaBase)
    implementation(libs.dokkaGfm)
}
