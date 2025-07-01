plugins {
    alias(libs.plugins.rcfh.library)
    alias(libs.plugins.rcfh.koin)
}

android.namespace = "ru.rcfh.core.account"

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.datastore)
    implementation(projects.core.network)

    implementation(libs.androidx.crypto)
}