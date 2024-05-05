package me.androidbox.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.gradle.api.Project

internal fun Project.configureBuildTypes(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    extensionType: ExtensionType
) {

    commonExtension.apply {

        this.buildFeatures {
            this.buildConfig = true
        }

        val apiKey = gradleLocalProperties(rootDir, this@configureBuildTypes.providers).getProperty("API_KEY")

            when(extensionType) {
                ExtensionType.APPLICATION -> {
                    extensions.configure(ApplicationExtension::class.java) {
                        buildTypes {
                            this.release {
                                configureReleaseBuildType(apiKey, commonExtension)
                            }

                            this.debug {
                                configureDebugBuildType(apiKey, commonExtension)
                            }
                        }
                    }
                }

                ExtensionType.LIBRARY -> {
                    extensions.configure(LibraryExtension::class.java) {
                        buildTypes {
                            this.release {
                                configureReleaseBuildType(apiKey, commonExtension)
                            }

                            this.debug {
                                configureDebugBuildType(apiKey, commonExtension)
                            }
                        }
                    }
                }
            }
    }
}

private fun BuildType.configureDebugBuildType(apiKey: String,  commonExtension: CommonExtension<*, *, *, *, *, *>) {
    this.buildConfigField("String", "API_KEY", "\"${apiKey}\"")
    this.buildConfigField("String", "BASE_URL", "\"https://runique.pl-coding.com:8080\"")

    this.isMinifyEnabled = false

    this.proguardFiles(
        commonExtension.getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
    )
}

private fun BuildType.configureReleaseBuildType(apiKey: String,  commonExtension: CommonExtension<*, *, *, *, *, *>) {
    this.buildConfigField("String", "API_KEY", "\"${apiKey}\"")
    this.buildConfigField("String", "BASE_URL", "\"https://runique.pl-coding.com:8080\"")

    this.isMinifyEnabled = true

    this.proguardFiles(
        commonExtension.getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
    )
}