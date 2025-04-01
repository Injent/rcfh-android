import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import ru.rcfh.buildlogic.configureCompose
import ru.rcfh.buildlogic.configureKotlin

class ApplicationComposePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("com.android.application")
            apply("org.jetbrains.kotlin.plugin.compose")
        }
        configureKotlin(extensions.getByType<ApplicationExtension>())
        configureCompose(extensions.getByType<ApplicationExtension>())
    }
}