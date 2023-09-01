plugins {
    alias(libs.plugins.kotlinJvm)
}

dependencies {
    compileOnly(libs.dokkaCore)
    compileOnly(libs.dokkaAnalysisKotlinApi)
    implementation(libs.dokkaBase)
    implementation(libs.dokkaGfm)
}
