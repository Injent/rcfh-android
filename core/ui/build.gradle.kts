plugins {
    alias(libs.plugins.rcfh.library)
}

android.namespace = "ru.rcfh.glpm.ui"

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.model)
}