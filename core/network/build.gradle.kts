plugins {
    alias(libs.plugins.rcfh.library)
    alias(libs.plugins.rcfh.koin)
    alias(libs.plugins.rcfh.serialization)
}

android.namespace = "ru.rcfh.network"

dependencies {
    implementation(projects.core.common)
    api(libs.ktor.core)
    implementation(libs.ktor.cio)
    implementation(libs.ktor.logging)
    implementation(libs.ktor.contentNegotiation)
    implementation(libs.ktor.kotlinx.json)
    implementation(libs.ktor.auth)
}