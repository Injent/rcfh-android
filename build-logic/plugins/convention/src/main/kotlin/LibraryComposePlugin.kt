import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import ru.rcfh.buildlogic.configureCompose

class LibraryComposePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("rcfh.library")
            apply("org.jetbrains.kotlin.plugin.compose")
        }
        configureCompose(extensions.getByType<LibraryExtension>())
    }
}