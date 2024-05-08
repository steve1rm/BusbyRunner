plugins {
    alias(libs.plugins.busbyrunner.android.library)
    alias(libs.plugins.busbyrunner.jvm.ktor)
}

android {
    namespace = "me.androidbox.core.data"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.database)

    implementation(libs.timber)
}