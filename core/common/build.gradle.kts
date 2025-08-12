plugins {
    alias(libs.plugins.rcfh.library.jvm)
    alias(libs.plugins.rcfh.koin)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    api(libs.clog)
    api(libs.apiResult)
    api(libs.kotlinx.datetime)
    api(libs.kotlinx.collections.immutable)
}