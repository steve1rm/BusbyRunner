plugins {
    alias(libs.plugins.busbyrunner.android.library)
}

android {
    namespace = "me.androidbox.core.data"

   compileOptions {
       this.isCoreLibraryDesugaringEnabled = true
   }
}


dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.database)

    implementation(libs.timber)
}