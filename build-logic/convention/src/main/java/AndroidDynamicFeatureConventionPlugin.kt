import com.android.build.api.dsl.DynamicFeatureExtension
import me.androidbox.convention.ExtensionType
import me.androidbox.convention.addUiLayerDependencies
import me.androidbox.convention.configureAndroidCompose
import me.androidbox.convention.configureBuildTypes
import me.androidbox.convention.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidDynamicFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {

        target.run {
            this.pluginManager.run {
                this.apply("com.android.dynamic-feature")
                this.apply("org.jetbrains.kotlin.android")
                this.apply("org.jetbrains.kotlin.plugin.compose")
            }

            this.extensions.configure(DynamicFeatureExtension::class.java) {
                configureKotlinAndroid(this)
                configureAndroidCompose(this)

                configureBuildTypes(
                    this,
                    ExtensionType.DYNAMIC_FEATURE
                )
            }

            dependencies {
                addUiLayerDependencies(target)
                "testImplementation"(kotlin("test"))
            }
        }
    }
}
