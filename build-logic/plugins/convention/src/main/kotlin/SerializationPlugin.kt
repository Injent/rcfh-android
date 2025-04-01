import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import ru.rcfh.buildlogic.get
import ru.rcfh.buildlogic.implementation
import ru.rcfh.buildlogic.libs

class SerializationPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

        dependencies {
            implementation(libs["kotlinx.serialization"])
        }
    }
}