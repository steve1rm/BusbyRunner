plugins {
    alias(libs.plugins.busbyrunner.android.library)
    alias(libs.plugins.busbyrunner.android.room)
}

android {
    namespace = "me.androidbox.core.database"
}

dependencies {
    implementation(projects.core.domain)
    implementation(libs.bundles.koin)
    implementation(libs.org.mongodb.bson)
}