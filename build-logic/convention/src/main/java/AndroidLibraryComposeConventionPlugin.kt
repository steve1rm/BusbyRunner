import com.android.build.api.dsl.LibraryExtension
import me.androidbox.convention.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            this.pluginManager.run {
                this.apply("busbyrunner.android.library")
            }
            // val extension = this.extensions.getByType<CommonExtension<*, *, *, *, *, *>>()
            val extension = this.extensions.getByType<LibraryExtension>()
            configureAndroidCompose(extension)
        }
    }
}