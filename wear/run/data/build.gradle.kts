plugins {
    alias(libs.plugins.busbyrunner.android.library)
}

android {
    namespace = "me.androidbox.wear.run.data"

    defaultConfig {
        minSdk = 30
    }
}

dependencies {
    implementation(projects.wear.run.domain)
    implementation(projects.core.domain)
    implementation(projects.core.connectivity.domain)

    implementation(libs.androidx.health.services.client)
    implementation(libs.bundles.koin)
}