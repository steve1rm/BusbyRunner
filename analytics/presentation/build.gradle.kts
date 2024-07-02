plugins {
    alias(libs.plugins.busbyrunner.android.feature.ui)
}

android {
    namespace = "me.androidbox.anlaytics.presentation"
}

dependencies {
    implementation(projects.analytics.domain)
}