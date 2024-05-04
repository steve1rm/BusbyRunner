import com.android.build.api.dsl.ApplicationExtension
import me.androidbox.convention.configureKotlinAndroid
import me.androidbox.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project


class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            this.pluginManager.run {
                this.apply("com.android.application")
                this.apply("org.jetbrains.kotlin.android")
            }

            this.extensions.configure(ApplicationExtension::class.java) {
                defaultConfig {
                    this.applicationId = libs.findVersion("projectApplicationId").get().toString()
                    this.targetSdk = libs.findVersion("projectTargetSdkVersion").get().toString().toInt()
                    this.versionName = libs.findVersion("projectVersionName").get().toString()
                    this.versionCode = libs.findVersion("projectVersionCode").get().toString().toInt()
                }

                configureKotlinAndroid(this)
            }
        }
    }
}
