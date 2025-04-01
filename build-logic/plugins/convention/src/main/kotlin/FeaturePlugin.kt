import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import ru.rcfh.buildlogic.get
import ru.rcfh.buildlogic.implementation
import ru.rcfh.buildlogic.libs

class FeaturePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("rcfh.library")
            apply("rcfh.library.compose")
            apply("rcfh.serialization")
        }

        dependencies {
            implementation(project(":core:common"))
            implementation(project(":core:data"))
            implementation(project(":core:designsystem"))
            implementation(project(":core:navigation"))
            implementation(project(":core:model"))

            implementation(libs["kotlinx.coroutines.android"])
            implementation(libs["koin.compose"])
            implementation(libs["koin.navigation"])
            implementation(libs["lifecycle.compose"])
        }
    }
}