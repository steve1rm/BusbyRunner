plugins {
    alias(libs.plugins.busbyrunner.jvm.library)
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.connectivity.domain)

    implementation(libs.kotlinx.coroutines.core)
}