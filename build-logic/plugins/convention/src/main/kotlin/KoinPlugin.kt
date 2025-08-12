import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import ru.rcfh.buildlogic.get
import ru.rcfh.buildlogic.implementation
import ru.rcfh.buildlogic.libs

class KoinPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        dependencies {
            implementation(platform(libs["koin-bom"]))

            pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
                dependencies {
                    implementation(libs["koin-core"])
                }
            }

            pluginManager.withPlugin("com.android.base") {
                dependencies {
                    implementation(libs["koin-android"])
                }
            }
        }
    }
}