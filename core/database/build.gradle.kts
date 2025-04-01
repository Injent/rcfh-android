plugins {
    alias(libs.plugins.rcfh.library)
    alias(libs.plugins.rcfh.koin)
    alias(libs.plugins.ksp)
    alias(libs.plugins.rcfh.serialization)
}

android.namespace = "ru.rcfh.database"

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(projects.core.common)
    implementation(libs.kotlinx.datetime)
    implementation(libs.room)
    implementation(libs.room.ktx)
    ksp(libs.room.ksp)
}