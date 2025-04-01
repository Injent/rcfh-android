plugins {
    alias(libs.plugins.rcfh.library)
    alias(libs.plugins.rcfh.library.compose)
    alias(libs.plugins.rcfh.serialization)
}

android.namespace = "ru.rcfh.core.navigation"

dependencies {
    api(libs.navigation.compose)
    implementation(libs.kotlinx.coroutines.android)
}