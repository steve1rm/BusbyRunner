import me.androidbox.convention.addUiLayerDependencies
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureUiConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            this.pluginManager.run {
                this.apply("busbyrunner.android.library.compose")
            }

            dependencies {
                this.addUiLayerDependencies(target)
            }
        }
    }
}
