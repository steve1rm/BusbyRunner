plugins {
    alias(libs.plugins.busbyrunner.android.library)
}

android {
    namespace = "me.androidbox.analytics.data"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)

    implementation(projects.core.database)
    implementation(projects.core.domain)
    implementation(projects.analytics.domain)
}