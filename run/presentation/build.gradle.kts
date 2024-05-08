plugins {
    alias(libs.plugins.busbyrunner.android.feature.ui)
}

android {
    namespace = "me.androidbox.run.presentation"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.run.domain)

    implementation(libs.coil.compose)
    implementation(libs.google.maps.android.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.timber)
}