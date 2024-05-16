plugins {
    alias(libs.plugins.busbyrunner.android.library)
    alias(libs.plugins.busbyrunner.jvm.ktor)
}

android {
    namespace = "me.androidbox.auth.data"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(projects.auth.domain)

    implementation(libs.bundles.koin)
}