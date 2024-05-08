plugins {
    alias(libs.plugins.busbyrunner.android.library)
}

android {
    namespace = "me.androidbox.run.network"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.data)
}