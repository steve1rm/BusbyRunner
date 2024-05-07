plugins {
    `kotlin-dsl`
}

group = "me.androidbox.busbyrunner.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

gradlePlugin {
    this.plugins {
        this.register("androidApplication") {
            this.id = "busbyrunner.application.android"
            this.implementationClass = "AndroidApplicationConventionPlugin"
        }

        this.register("androidApplicationCompose") {
            this.id = "busbyrunner.application.android.compose"
            this.implementationClass = "AndroidApplicationComposeConventionPlugin"
        }

        this.register("androidLibrary") {
            this.id = "busbyrunner.android.library"
            this.implementationClass = "AndroidLibraryConventionPlugin"

        }
    }
}