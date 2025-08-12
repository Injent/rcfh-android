import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import ru.rcfh.buildlogic.configureCompose
import ru.rcfh.buildlogic.configureKotlinAndroid

class ApplicationComposePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("com.android.application")
            apply("org.jetbrains.kotlin.plugin.compose")
        }
        configureKotlinAndroid(extensions.getByType<ApplicationExtension>())
        configureCompose(extensions.getByType<ApplicationExtension>())
    }
}