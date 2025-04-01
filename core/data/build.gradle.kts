plugins {
    alias(libs.plugins.rcfh.library)
    alias(libs.plugins.rcfh.koin)
    alias(libs.plugins.rcfh.serialization)
}

android.namespace = "ru.rcfh.data"


dependencies {
    implementation(projects.core.common)
    api(projects.core.database)
    api(projects.core.datastore)
    api(projects.core.network)
    api(projects.core.model)
}