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

        this.register("androidLibraryCompose") {
            this.id = "busbyrunner.android.library.compose"
            this.implementationClass = "AndroidLibraryComposeConventionPlugin"
        }

        this.register("androidFeatureUi") {
            this.id = "busbyrunner.android.feature.ui"
            this.implementationClass = "AndroidFeatureUiConventionPlugin"
        }

        this.register("androidRoom") {
            this.id = "busbyrunner.android.room"
            this.implementationClass = "AndroidRoomConventionPlugin"
        }

        this.register("jvmLibrary") {
            this.id = "busbyrunner.jvm.library"
            this.implementationClass = "JvmLibraryConventionPlugin"
        }

        this.register("jvmKtor") {
            this.id = "busbyrunner.jvm.ktor"
            this.implementationClass = "JvmKtorConventionPlugin"
        }
    }
}