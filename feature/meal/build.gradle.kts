import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {

    androidLibrary {
        namespace = "com.maksimowiczm.foodyou.feature.meal"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core-ui"))
            implementation(project(":core-model"))
            implementation(project(":core-domain"))

            implementation(libs.kotlinx.serialization.json)

            // Compose reorderable list
            implementation(libs.reorderable)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)

            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }

        getByName("androidDeviceTest").dependencies {
            implementation(libs.androidx.runner)
            implementation(libs.androidx.core)
            implementation(libs.androidx.junit)
            implementation(libs.androidx.ui.test.manifest)
        }
    }
}
