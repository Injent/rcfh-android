plugins {
    alias(libs.plugins.rcfh.feature)
}

android.namespace = "ru.rcfh.feature.documents"

dependencies {
    implementation(projects.core.database)
    implementation(projects.core.sdui)
    implementation(projects.core.account)
}