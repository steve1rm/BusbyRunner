plugins {
    alias(libs.plugins.busbyrunner.android.feature.ui)
}

android {
    namespace = "me.androidbox.auth.presentation"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.auth.domain)
}