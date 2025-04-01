plugins {
    alias(libs.plugins.rcfh.library)
    alias(libs.plugins.rcfh.koin)
}

android.namespace = "ru.rcfh.core.sync"

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.data)
    implementation(libs.androidx.work)
}