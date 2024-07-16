plugins {
    alias(libs.plugins.busbyrunner.android.library.compose)
}

android {
    namespace = "me.androidbox.core.presentation.designsystem_wear"

    defaultConfig {
        minSdk = libs.versions.projectMinSdkVersion.get().toInt()
    }
}

dependencies {
    api(projects.core.presentation.designsystem)

    implementation(libs.androidx.wear.compose.material)
}