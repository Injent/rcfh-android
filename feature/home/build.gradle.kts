plugins {
    alias(libs.plugins.rcfh.feature)
}

android.namespace = "ru.rcfh.feature.home"

dependencies {
    implementation(projects.core.account)
}