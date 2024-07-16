plugins {
    alias(libs.plugins.busbyrunner.android.application.wear.compose)
}

android {
    namespace = "me.androidbox.wear.app"

    defaultConfig {
        minSdk = libs.versions.projectMinSdkVersion.get().toInt()
    }
}

dependencies {
    implementation(projects.core.presentation.designsystemWear)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.bundles.koin)
}