plugins {
    alias(libs.plugins.rcfh.feature)
}

android.namespace = "ru.rcfh.feature.templates"

dependencies {
    implementation(projects.core.sdui)
}