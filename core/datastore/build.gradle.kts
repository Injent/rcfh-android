plugins {
    alias(libs.plugins.rcfh.library)
    alias(libs.plugins.rcfh.koin)
    alias(libs.plugins.rcfh.serialization)
    alias(libs.plugins.ksp)
}

android.namespace = "ru.rcfh.core.datastore"

dependencies {
    implementation(libs.datastore)
    implementation(projects.core.model)
}