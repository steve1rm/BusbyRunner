import me.androidbox.convention.configureKotlinJvm
import org.gradle.api.Plugin
import org.gradle.api.Project

class JvmLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            this.pluginManager.run {
                this.apply("org.jetbrains.kotlin.jvm")
            }

            this.configureKotlinJvm()
        }
    }
}
