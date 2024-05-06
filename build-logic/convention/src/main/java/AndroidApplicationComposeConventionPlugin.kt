import com.android.build.api.dsl.ApplicationExtension
import me.androidbox.convention.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            this.pluginManager.apply("busbyrunner.application.android")

            this.configure<ApplicationExtension> {
                configureAndroidCompose(this)
            }
        }
    }
}