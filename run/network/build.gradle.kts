plugins {
    alias(libs.plugins.busbyrunner.android.library)
}

android {
    namespace = "me.androidbox.run.network"
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    implementation(projects.core.domain)
    implementation(projects.core.data)
}