import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "ru.rcfh.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("application") {
            id = "rcfh.application"
            implementationClass = "ApplicationPlugin"
        }
        register("applicationCompose") {
            id = "rcfh.application.compose"
            implementationClass = "ApplicationComposePlugin"
        }
        register("library") {
            id = "rcfh.library"
            implementationClass = "LibraryPlugin"
        }
        register("libraryCompose") {
            id = "rcfh.library.compose"
            implementationClass = "LibraryComposePlugin"
        }
        register("feature") {
            id = "rcfh.feature"
            implementationClass = "FeaturePlugin"
        }
        register("koin") {
            id = "rcfh.koin"
            implementationClass = "KoinPlugin"
        }
        register("serialization") {
            id = "rcfh.serialization"
            implementationClass = "SerializationPlugin"
        }
    }
}