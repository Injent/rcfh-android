plugins {
    alias(libs.plugins.rcfh.feature)
}

android.namespace = "ru.rcfh.feature.login"

dependencies {
    implementation(projects.core.account)
}