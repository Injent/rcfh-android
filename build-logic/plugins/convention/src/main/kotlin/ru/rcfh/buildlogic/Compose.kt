package ru.rcfh.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File

internal fun Project.configureCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        dependencies {
            val bom = libs["compose.bom"]
            implementation(platform(bom))
            add("androidTestImplementation", platform(bom))
        }

        tasks.withType<KotlinCompile>().configureEach {
            compilerOptions {
                freeCompilerArgs.apply {
                    addAll(buildComposeStabilityConfig())
                    addAll(buildComposeMetricsParameters())
                }
            }
        }
    }

    extensions.configure<ComposeCompilerGradlePluginExtension> {
        fun Provider<String>.onlyIfTrue() = flatMap { provider { it.takeIf(String::toBoolean) } }
        fun Provider<*>.relativeToRootProject(dir: String) = map {
            isolated.rootProject.projectDirectory
                .dir("build")
                .dir(projectDir.toRelativeString(rootDir))
        }.map { it.dir(dir) }

        project.providers.gradleProperty("enableComposeCompilerMetrics").onlyIfTrue()
            .relativeToRootProject("compose-metrics")
            .let(metricsDestination::set)

        project.providers.gradleProperty("enableComposeCompilerReports").onlyIfTrue()
            .relativeToRootProject("compose-reports")
            .let(reportsDestination::set)

        stabilityConfigurationFiles
            .add(isolated.rootProject.projectDirectory.file("config/compose.conf"))
    }
}

private fun Project.buildComposeStabilityConfig(): List<String> {
    val stabilityConfig = File(rootDir, "config/compose.conf")
    val params = mutableListOf<String>()
    if (stabilityConfig.exists()) {
        params.add("-P")
        params.add("plugin:androidx.compose.compiler.plugins.kotlin:stabilityConfigurationPath=" + stabilityConfig.absolutePath)
    }

    return params
}

private fun Project.buildComposeMetricsParameters(): List<String> {
    val buildDir = layout.buildDirectory.asFile.get()
    val metricParameters = mutableListOf<String>()

    val metricsFolder = File(buildDir, "compose-metrics")
    metricParameters.add("-P")
    metricParameters.add(
        "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=" + metricsFolder.absolutePath
    )

    val reportsFolder = File(buildDir, "compose-reports")
    metricParameters.add("-P")
    metricParameters.add(
        "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=" + reportsFolder.absolutePath
    )
    return metricParameters.toList()
}