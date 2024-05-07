package me.androidbox.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>
) {
    commonExtension.apply {
        this.compileSdk = libs.findVersion("projectCompileSdkVersion").get().toString().toInt()

        this.defaultConfig.minSdk = libs.findVersion("projectMinSdkVersion").get().toString().toInt()

        this.compileOptions {
            this.isCoreLibraryDesugaringEnabled = true
            this.sourceCompatibility = JavaVersion.VERSION_17
            this.targetCompatibility = JavaVersion.VERSION_17
        }
    }
    
    configureKotlin()

    dependencies {
        "coreLibraryDesugaring"(libs.findLibrary("desugar.jdk.libs").get())
    }
}

private fun Project.configureKotlin() {
    this.tasks.withType(KotlinCompile::class.java).configureEach {
        this.kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    }
}
