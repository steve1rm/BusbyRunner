plugins {
    alias(libs.plugins.busbyrunner.android.library)
}

android {
    namespace = "me.androidbox.wear.run.data"

    defaultConfig {
        minSdk = libs.versions.projectMinSdkVersion.get().toInt()
    }
}

dependencies {
    implementation(libs.androidx.health.services.client)
    implementation(libs.bundles.koin)
}