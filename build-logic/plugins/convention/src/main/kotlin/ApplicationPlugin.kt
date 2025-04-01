import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.internal.builtins.StandardNames.FqNames.set
import ru.rcfh.buildlogic.configureKotlin
import ru.rcfh.buildlogic.libs

class ApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("com.android.application")
            apply("org.jetbrains.kotlin.android")
        }

        extensions.configure<ApplicationExtension> {
            configureKotlin(this)
            defaultConfig.targetSdk = libs.findVersion("targetSdk").get().toString().toInt()

            defaultConfig.resourceConfigurations += setOf(
                "en",
                "ru",
                "af",
                "ar",
                "be",
                "bn",
                "cs",
                "da",
                "de",
                "es",
                "eu",
                "fa",
                "fil",
                "fr",
                "hi",
                "hu",
                "ia",
                "in",
                "it",
                "iw",
                "ja",
                "kk",
                "kn",
                "ko",
                "nl",
                "pl",
                "pt",
                "pt-rBR",
                "ro",
                "sk",
                "sr",
                "te",
                "th",
                "tr",
                "uk",
                "vi",
                "zh-rCN",
                "zh-rTW"
            )
        }
    }
}