plugins {
    alias(libs.plugins.rcfh.library)
    alias(libs.plugins.rcfh.koin)
}

android.namespace = "ru.rcfh.common"

dependencies {
    api(libs.apiResult)
    api(libs.kotlinx.datetime)
    api(libs.kotlinx.coroutines.android)
}