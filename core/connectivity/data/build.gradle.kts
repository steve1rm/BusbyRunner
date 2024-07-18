plugins {
    alias(libs.plugins.busbyrunner.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "me.androidbox.core.connectivity.data"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.connectivity.domain)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.play.services.wearable)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.bundles.koin)
}