plugins {
    alias(libs.plugins.rcfh.library)
    alias(libs.plugins.rcfh.library.compose)
}

android.namespace = "ru.rcfh.designsystem"

dependencies {
    implementation(libs.sheetsComposeDialog.core)
    implementation(libs.sheetsComposeDialog.calendar)
    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.core.ktx)
    api(libs.compose.ui)
    api(libs.compose.runtime)
    api(libs.compose.material3)
    api(libs.compose.adaptive)
    api(libs.androidx.window)
    api(libs.compose.foundation)
    api(libs.compose.ui.graphics)
    api(libs.compose.ui.text)
    api(libs.compose.ui.util)
    api(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
}