plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {

    androidLibrary {
        namespace = "com.maksimowiczm.foodyou.core.ui"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        // Allow multi-module compose resources
        // https://www.jetbrains.com/help/kotlin-multiplatform-dev/whats-new-compose-180.html#support-for-multiplatform-resources-in-the-androidlibrary-target
        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))
            implementation(project(":core-model"))

            // Compose
            api(compose.runtime)
            api(compose.foundation)
//            api(compose.material3)
            api(libs.androidx.material3)
            api(compose.materialIconsExtended)
            api(compose.ui)
            api(compose.components.resources)
            api(libs.navigation.compose)

            // Koin
            api(libs.koin.compose)
            api(libs.koin.compose.viewmodel)

            // Shimmer
            api(libs.compose.shimmer)

            // Reorderable list
            implementation(libs.reorderable)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        androidMain.dependencies {
            // Koin
            api(libs.koin.androidx.compose)
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.junit)
            }
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "foodyou.app.generated.resources"
    generateResClass = always
}
