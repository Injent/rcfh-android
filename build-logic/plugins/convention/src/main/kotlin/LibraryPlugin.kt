import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import ru.rcfh.buildlogic.configureKotlin
import ru.rcfh.buildlogic.libs

class LibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("com.android.library")
            apply("org.jetbrains.kotlin.android")
        }

        extensions.configure<LibraryExtension> {
            configureKotlin(this)
            defaultConfig.targetSdk = libs.findVersion("targetSdk").get().toString().toInt()

            defaultConfig {
                consumerProguardFiles("consumer-proguard-rules.pro")
            }
        }
    }
}