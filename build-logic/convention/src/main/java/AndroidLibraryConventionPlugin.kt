import com.android.build.api.dsl.LibraryExtension
import me.androidbox.convention.ExtensionType
import me.androidbox.convention.configureBuildTypes
import me.androidbox.convention.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            this.pluginManager.run {
                this.apply("com.android.library")
                this.apply("org.jetbrains.kotlin.android")

                extensions.configure<LibraryExtension> {
                    configureKotlinAndroid(this)

                    configureBuildTypes(this, ExtensionType.LIBRARY)

                    this.defaultConfig {
                        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                        consumerProguardFiles("consumer-rules.pro")
                    }
                }
            }

            dependencies {
                "testImplementation"(kotlin("test"))
            }
        }
    }
}