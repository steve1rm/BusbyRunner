plugins {
    alias(libs.plugins.busbyrunner.android.library)
    alias(libs.plugins.busbyrunner.android.room)
}

android {
    namespace = "me.androidbox.analytics.data"
}

dependencies {
    implementation(projects.core.database)
    implementation(projects.core.domain)
    implementation(projects.analytics.domain)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.bundles.koin)
}