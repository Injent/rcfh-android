plugins {
    alias(libs.plugins.rcfh.feature)
    alias(libs.plugins.rcfh.serialization)
}

android.namespace = "ru.rcfh.feature.forms"

dependencies {
    implementation(libs.sheetsComposeDialog.core)
    implementation(libs.sheetsComposeDialog.calendar)
    implementation(libs.grid)
    implementation(libs.gms.location)
}