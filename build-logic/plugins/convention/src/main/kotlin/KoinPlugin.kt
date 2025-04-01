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
            implementation(libs["koin-android"])
        }
    }
}