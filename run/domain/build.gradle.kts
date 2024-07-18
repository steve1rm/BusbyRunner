plugins {
    alias(libs.plugins.org.jetbrains.kotlin.jvm)
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.connectivity.domain)

    implementation(libs.kotlinx.coroutines.core)
}