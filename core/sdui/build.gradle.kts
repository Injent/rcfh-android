plugins {
    alias(libs.plugins.rcfh.library)
    alias(libs.plugins.rcfh.library.compose)
    alias(libs.plugins.rcfh.serialization)
    alias(libs.plugins.rcfh.koin)
}

android.namespace = "ru.rcfh.glpm.core.sdui"

dependencies {
    implementation(libs.compose.runtime)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.gms.location)
    implementation(projects.core.account)
    implementation(projects.core.common)
    implementation(projects.core.datastore)
    implementation(projects.core.network)
    implementation(projects.core.database)
}