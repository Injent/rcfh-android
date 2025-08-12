plugins {
    alias(libs.plugins.rcfh.feature)
    alias(libs.plugins.rcfh.serialization)
}

android.namespace = "ru.rcfh.glpm.feature.blank"

dependencies {
    implementation(libs.gms.location)
    implementation(libs.androidx.altitude)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
}