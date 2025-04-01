plugins {
    alias(libs.plugins.rcfh.library)
    alias(libs.plugins.rcfh.library.compose)
    alias(libs.plugins.rcfh.serialization)
    alias(libs.plugins.rcfh.koin)
}

android.namespace = "ru.rcfh.core.sdui"

dependencies {
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.koin.compose)
    implementation(projects.core.network)
    implementation(projects.core.database)
    implementation(projects.core.designsystem)
}